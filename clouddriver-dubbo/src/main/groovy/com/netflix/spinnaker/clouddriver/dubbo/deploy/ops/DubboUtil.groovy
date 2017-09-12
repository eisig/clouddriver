package com.netflix.spinnaker.clouddriver.dubbo.deploy.ops

/**
 * Created by eisig on 2017/9/11.
 */
class DubboUtil {
  static String stackOfAsgName(String asgName) {
    if (asgName == null)
      return null
    def comps = asgName.split("-")
    if (comps.size() > 1) {
      return comps[1]
    }
    return null
  }

  static boolean dubboOnlyDiscover(String discovery) {
    return discovery != null && discovery.trim() == 'dubbo'
  }

  static boolean dubboDiscoveryEnabled(String discovery) {
    if (discovery == null) {
      return false
    }
    return dubboOnlyDiscover(discovery) ||
      enableDubboInQuery(discovery)
  }

  private static boolean enableDubboInQuery(String discovery) {
    def url
    try {
      url = new URL(discovery)
    } catch (Exception ex) {
      return false
    }

    def queryParams = url.query?.split('&') // safe operator for urls without query params
    def mapParams = queryParams.collectEntries { param -> param.split('=').collect { URLDecoder.decode(it, 'utf-8') } }
    return mapParams.get('dubbo') == 'true'
  }
}
