package com.netflix.spinnaker.clouddriver.dubbo.privoder.config

import groovy.transform.ToString

/**
 * Created by eisig on 2017/9/10.
 */
@ToString(includeNames = true)
class DubboAccountConfigurationProperties {
  @ToString(includeNames = true)
  static class DubboAccount {
    String name
    List<String> regions
    String url
    List<String> stacks
  }
  List<DubboAccount> accounts = []
}
