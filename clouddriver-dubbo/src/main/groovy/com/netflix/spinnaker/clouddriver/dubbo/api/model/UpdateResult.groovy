package com.netflix.spinnaker.clouddriver.dubbo.api.model

import groovy.transform.EqualsAndHashCode

/**
 * Created by eisig on 2017/9/10.
 */
@EqualsAndHashCode
class UpdateResult {
  String code;
  List<Provider> updateProviders;
  Map<String, String> body;
}


