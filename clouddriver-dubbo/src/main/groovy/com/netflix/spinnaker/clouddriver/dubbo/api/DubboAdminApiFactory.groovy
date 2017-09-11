package com.netflix.spinnaker.clouddriver.dubbo.api

import retrofit.RestAdapter
import retrofit.converter.Converter

/**
 * Created by eisig on 2017/9/9.
 */
class DubboAdminApiFactory {
  private Converter dubboAdminConverter

  DubboAdminApiFactory(Converter dubboAdminConverter) {
    this.dubboAdminConverter = dubboAdminConverter
  }

  public DubboAdminApi createApi(String endpoint) {
    new RestAdapter.Builder()
      .setConverter(dubboAdminConverter)
      .setEndpoint(endpoint)
      .build()
      .create(DubboAdminApi)
  }
}
