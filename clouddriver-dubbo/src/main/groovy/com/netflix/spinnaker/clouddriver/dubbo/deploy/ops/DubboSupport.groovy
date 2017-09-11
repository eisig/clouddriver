package com.netflix.spinnaker.clouddriver.dubbo.deploy.ops

import com.amazonaws.AmazonServiceException
import com.netflix.spinnaker.clouddriver.data.task.Task
import com.netflix.spinnaker.clouddriver.dubbo.api.DubboAdminApi
import com.netflix.spinnaker.clouddriver.dubbo.api.model.UpdateResult
import groovy.transform.InheritConstructors
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import retrofit.RetrofitError
import retrofit.client.Response

/**
 * Created by eisig on 2017/9/10.
 */
@Slf4j
@Component
class DubboSupport {

  DubboAdminApi getDubboAdmin(def credentials, String region) {
    return null
  }

  boolean verifyInstanceAndAsgExist(def credentials,
                                    String region,
                                    String instanceId,
                                    String asgName) {
    return false;
  }

  @Autowired(required = false)
  EurekaSupportConfigurationProperties eurekaSupportConfigurationProperties

  void updateDiscoveryStatusForInstances(def description,
                                         Task task,
                                         String phaseName,
                                         DiscoveryStatus discoveryStatus,
                                         List<String> instanceIds) {
    updateDiscoveryStatusForInstances(
      description, task, phaseName, discoveryStatus, instanceIds, eurekaSupportConfigurationProperties.retryMax, eurekaSupportConfigurationProperties.retryMax
    )
  }

  void updateDiscoveryStatusForInstances(def description,
                                         Task task,
                                         String phaseName,
                                         DiscoveryStatus discoveryStatus,
                                         List<String> instanceIds,
                                         int findApplicationNameRetryMax,
                                         int updateEurekaRetryMax) {

    if (eurekaSupportConfigurationProperties == null) {
      throw new IllegalStateException("eureka configuration not supplied")
    }

    def dubboAdmin = getDubboAdmin(description.credentials, description.region)
    def random = new Random()
    def applicationName = null



    def errors = [:]
    boolean shouldFail = false
    int index = 0
    for (String instanceId : instanceIds) {
      if (index > 0) {
        sleep eurekaSupportConfigurationProperties.throttleMillis
      }


      try {
        retry(task, phaseName, updateEurekaRetryMax) { retryCount ->
          task.updateStatus phaseName, "Attempting to mark ${instanceId} as '${discoveryStatus.value}' in discovery (attempt: ${retryCount})."

          UpdateResult resp

          if (discoveryStatus == DiscoveryStatus.Disable) {
            resp = dubboAdmin.disableServicesByInstanceId(instanceId)
          } else {
            resp = dubboAdmin.enableServiceByInstanceId(instanceId)
          }

          if (resp.code != '200') {
            throw new RetryableException("Non HTTP 200 response from discovery for instance ${instanceId}, will retry (attempt: $retryCount}).")
          }
        }
      } catch (RetrofitError retrofitError) {
        if (retrofitError.response?.status == 404 && discoveryStatus == DiscoveryStatus.Disable) {
          task.updateStatus phaseName, "Could not find ${instanceId} in application $applicationName in discovery, skipping disable operation."
        } else {
          errors[instanceId] = retrofitError
        }
      } catch (ex) {
        errors[instanceId] = ex
      }
      if (errors[instanceId]) {
        if (verifyInstanceAndAsgExist(description.credentials, description.region, instanceId, description.asgName)) {
          shouldFail = true
        } else {
          task.updateStatus phaseName, "Instance '${instanceId}' does not exist and will not be marked as '${discoveryStatus.value}'"
        }
      }
      index++
    }
    if (shouldFail) {
      task.updateStatus phaseName, "Failed marking instances '${discoveryStatus.value}' in discovery for instances ${errors.keySet()}"
      task.fail()
      DubboSupport.log.info("[$phaseName] - Failed marking discovery $discoveryStatus.value for instances ${errors}")
    }
  }

  def retry(Task task, String phaseName, int maxRetries, Closure c) {
    def retryCount = 0
    while (true) {
      try {
        if (!task.status.isFailed()) {
          return c.call(retryCount)
        }
        break
      } catch (RetryableException ex) {
        if (retryCount >= (maxRetries - 1)) {
          throw ex
        }

        DubboSupport.log.debug("[$phaseName] - Caught retryable exception", ex)

        retryCount++
        sleep(getDiscoveryRetryMs());
      } catch (RetrofitError re) {
        if (retryCount >= (maxRetries - 1)) {
          throw re
        }

        DubboSupport.log.debug("[$phaseName] - Failed calling external service", re)

        if (re.kind == RetrofitError.Kind.NETWORK || re.response.status == 404 || re.response.status == 406) {
          retryCount++
          sleep(getDiscoveryRetryMs())
        } else if (re.response.status >= 500) {
          // automatically retry on server errors (but wait a little longer between attempts)
          sleep(getDiscoveryRetryMs() * 10)
          retryCount++
        } else {
          throw re
        }
      } catch (AmazonServiceException ase) {
        if (ase.statusCode == 503) {
          DubboSupport.log.debug("[$phaseName] - Failed calling AmazonService", ase)
          retryCount++
          sleep(getDiscoveryRetryMs())
        } else {
          throw ase
        }
      }
    }


  }

  protected long getDiscoveryRetryMs() {
    return eurekaSupportConfigurationProperties.retryIntervalMillis
  }

  enum DiscoveryStatus {
    Enable('UP'),
    Disable('OUT_OF_SERVICE')

    String value

    DiscoveryStatus(String value) {
      this.value = value
    }
  }

  @InheritConstructors
  static class DiscoveryNotConfiguredException extends RuntimeException {}

  @InheritConstructors
  static class RetryableException extends RuntimeException {}

}
