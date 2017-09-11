package com.netflix.spinnaker.clouddriver.dubbo.api

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import retrofit.converter.Converter
import retrofit.converter.JacksonConverter
import spock.lang.Specification

/**
 * Created by eisig on 2017/9/9.
 */
class DubboAdminApiFactoryTest extends Specification {
  def "CreateApi"() {
    setup:
    def api= new DubboAdminApiFactory(dubboAdminConverter()).createApi("localhost")
    expect:
      api != null
  }

  Converter dubboAdminConverter() {
    new JacksonConverter(new ObjectMapper()
      .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
      .enable(MapperFeature.AUTO_DETECT_CREATORS))
  }
}
