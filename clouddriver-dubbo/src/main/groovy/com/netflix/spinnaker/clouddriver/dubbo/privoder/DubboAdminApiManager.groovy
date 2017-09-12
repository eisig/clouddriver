package com.netflix.spinnaker.clouddriver.dubbo.privoder

import com.netflix.spinnaker.clouddriver.dubbo.api.DubboAdminApi

/**
 * Created by eisig on 2017/9/11.
 */
class DubboAdminApiManager {
  Map<String, DubboAdminApi> dubboAminApis

  DubboAdminApiManager(dubboAminApis) {
    this.dubboAminApis = dubboAminApis
  }

  DubboAdminApi find(String  stack) {
     dubboAminApis.get(stack)
  }
}
