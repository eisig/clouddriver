package com.netflix.spinnaker.clouddriver.dubbo.api

import com.netflix.spinnaker.clouddriver.dubbo.api.model.Provider
import com.netflix.spinnaker.clouddriver.dubbo.api.model.UpdateResult
import retrofit.http.GET
import retrofit.http.*
/**
 * Created by eisig on 2017/9/9.
 */
interface DubboAdminApi {

  @GET("/providers/provider/listAll")
  List<Provider> providers()

  @GET("/providers/provider/listProviderByAddress")
  List<Provider> providers(@Query("providerAddress") String providerAddress)

  @GET("/providers/provider/listByInstanceId")
  List<Provider> getProviderInfo(@Query("providerAddress") String providerAddress)

  @GET("/providers/provider/enable")
  UpdateResult enableService(@Query("providerAddress") String providerAddress)


  @GET("/providers/provider/disable")
  UpdateResult disableServicesByAddress(@Query("providerAddress") String instanceId)

  @GET("/providers/provider/enable")
  UpdateResult enableServiceByInstanceId(@Query("instanceId") String instanceId)


  @GET("/providers/provider/disable")
  UpdateResult disableServicesByInstanceId(@Query("instanceId") String instanceId)

}
