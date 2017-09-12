//package com.netflix.spinnaker.clouddriver.dubbo.api
//
//import spock.lang.Shared
//import spock.lang.Specification
//
///**
// * Created by eisig on 2017/9/9.
// */
//
//class DubboAdminApiTest extends Specification {
//
//  static def apiEndpoint = "http://10.110.3.203:8080/dubbo-admin-api"
//
//  @Shared
//  DubboAdminApi dubboAdminApi
//
//  def setupSpec() {
//    def dubboAdminConfig = new DubboAdminConfig()
//    def apiFactory = dubboAdminConfig.dubboAdminApiFactory(dubboAdminConfig.dubboAdminConverter())
//    dubboAdminApi = apiFactory.createApi(apiEndpoint)
//  }
//
//
//  def "Providers"() {
//    when:
//    def provoders = dubboAdminApi.providers()
//    then:
//    provoders != null
//  }
//
//  def "list providers by address"() {
//    setup:
//    def address = "10.110.4.8:20880"
//    when:
//    def provoders = dubboAdminApi.providers(address)
//    then:
//    provoders != null
//    provoders.size() > 0
//    def otherProvider = provoders.find { it.address != address}
//    otherProvider == null
//  }
//
//
////
//  def "EnableService"() {
//    setup:
//    def address = "10.110.4.8:20880"
//    when:
//    def updateResult = dubboAdminApi.enableService(address)
//    then:
//    updateResult.code == '200'
//    def otherProvider = updateResult.updateProviders.find { (!it.enabled) }
//    otherProvider == null
//  }
////
//  def "EnableService not exist"() {
//    setup:
//    def address = "101.110.4.8:20880"
//    when:
//    def updateResult = dubboAdminApi.enableService(address)
//    then:
//    updateResult.code == '200'
//    def otherProvider = updateResult.updateProviders.find { (!it.enabled) }
//    otherProvider == null
//  }
//
//  def "disable service"() {
//    setup:
//    def address = "10.110.4.8:20880"
//    when:
//    def updateResult = dubboAdminApi.disableServices(address)
//    then:
//    updateResult.code == '200'
//    def otherProvider = updateResult.updateProviders.find { (!it.enabled) }
//    otherProvider == null
//  }
////
//  def "disable service not exist"() {
//    setup:
//    def address = "101.110.4.8:20880"
//    when:
//    def updateResult = dubboAdminApi.enableService(address)
//    then:
//    updateResult.code == '200'
//    def otherProvider = updateResult.updateProviders.find { (!it.enabled) }
//    otherProvider == null
//  }
//}
