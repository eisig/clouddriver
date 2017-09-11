package com.netflix.spinnaker.clouddriver.dubbo.privoder.agent

/**
 * Created by eisig on 2017/9/10.
 */
interface DubboAwareProvider {

  Boolean isProviderForDubboRecord(Map<String, Object> attributes)

  String getInstanceKey(Map<String, Object> attributes, String region)

  String getInstanceHealthKey(Map<String, Object> attributes,  String region, String healthId)

}
