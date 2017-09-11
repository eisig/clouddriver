package com.netflix.spinnaker.clouddriver.dubbo.api.model

import groovy.transform.EqualsAndHashCode

/**
 * Created by eisig on 2017/9/9.
 */
@EqualsAndHashCode
class Provider {
  String address
  Boolean dynamic
  Boolean enabled;
  Double weight
  String application
  String expired
  int alived
}
