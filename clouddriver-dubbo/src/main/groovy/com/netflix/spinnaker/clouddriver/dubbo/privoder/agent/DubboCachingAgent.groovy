package com.netflix.spinnaker.clouddriver.dubbo.privoder.agent

import com.fasterxml.jackson.databind.ObjectMapper
import com.netflix.spinnaker.cats.agent.AgentDataType
import com.netflix.spinnaker.cats.agent.CacheResult
import com.netflix.spinnaker.cats.agent.CachingAgent
import com.netflix.spinnaker.cats.agent.DefaultCacheResult
import com.netflix.spinnaker.cats.cache.CacheData
import com.netflix.spinnaker.cats.provider.Provider
import com.netflix.spinnaker.cats.provider.ProviderCache
import com.netflix.spinnaker.clouddriver.core.provider.agent.HealthProvidingCachingAgent
import com.netflix.spinnaker.clouddriver.dubbo.api.DubboAdminApi
import groovy.util.logging.Slf4j

import static com.netflix.spinnaker.clouddriver.core.provider.agent.Namespace.HEALTH
import static com.netflix.spinnaker.clouddriver.core.provider.agent.Namespace.INSTANCES

/**
 * Created by eisig on 17/9/10.
 */

@Slf4j
class DubboCachingAgent implements CachingAgent, HealthProvidingCachingAgent {


  private final String dubboAdminHost;
  private final DubboAdminApi prometheusApi;
  final String healthId = "dubbo"
  final List<Provider> providerList;
  final String accountName;
  final String region;

  DubboCachingAgent(DubboAdminApi dubboAdminApi, String dubboAdminHost, String accountName, String region, ObjectMapper objectMapper, providerList) {
    this.dubboAdminHost = dubboAdminHost
    this.prometheusApi = dubboAdminApi
    this.accountName = accountName;
    this.providerList = providerList
    this.region = region;
  }

  @Override
  String getAgentType() {
    "${dubboAdminHost}/${DubboCachingAgent.simpleName}"
  }

  @Override
  String getProviderName() {
    'dubbo'
  }

  @Override
  Collection<AgentDataType> getProvidedDataTypes() {
    types
  }


  @Override
  CacheResult loadData(ProviderCache providerCache) {
    log.info("Describing items in ${agentType}")
//    PrometheusResponse disco = prometheusApi.loadApplicationHealthz()
//
    Collection<CacheData> eurekaCacheData = new LinkedList<CacheData>()
    Collection<CacheData> instanceCacheData = new LinkedList<CacheData>()
//
//    if (disco.status == PrometheusResponse.Status.success) {
//      List<PrometheusDataInstantResult> dataInstantResultList = disco.data.results
//
//      for (PrometheusDataInstantResult healthResult : dataInstantResultList) {
//        if (healthResult.metric.ec2_instance_id && healthResult.metric.prometheusHealthCheck) {
//          String instance_id = healthResult.metric.ec2_instance_id
//          String instanceKey = Keys.getInstanceKey(instance_id, accountName, region)
//          Map<String, Object> attributes = new HashMap<>()
//          String instanceHealthKey = Keys.getInstanceHealthKey(instance_id, accountName, region, healthId)
//
//          if (healthResult.values.last() >= 1.0 ) {
//            attributes.state = "Up"
//          }else {
//            attributes.state = "Down"
//          }
//          Map<String, Collection<String>> relationships = [(INSTANCES.ns): [instanceKey]]
//          eurekaCacheData.add(new DefaultCacheData(instanceHealthKey, attributes, relationships))
//        }
//      }
//    }
//
//    log.info("Caching ${eurekaCacheData.size()} items in ${agentType}")
    new DefaultCacheResult(
      (INSTANCES.ns): instanceCacheData,
      (HEALTH.ns): eurekaCacheData)
  }
}
