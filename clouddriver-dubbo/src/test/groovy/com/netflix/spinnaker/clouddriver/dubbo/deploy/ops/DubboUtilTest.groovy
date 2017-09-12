package com.netflix.spinnaker.clouddriver.dubbo.deploy.ops

import spock.lang.Specification

/**
 * Created by eisig on 2017/9/12.
 */
class DubboUtilTest extends Specification {

  def "StackOfAsgName"() {
    expect:
      DubboUtil.stackOfAsgName(asgName) == stackName
    where: "some asgName"

    asgName             || stackName
    null                ||  null
    ''                  || null
    'oidc'              || null
    'oidc-test1'        || 'test1'
    'oidc-test1-v001'   || 'test1'
    'oidc-test1-detail-v001'   || 'test1'
  }

  def "DubboOnlyDiscover"() {
    expect:
    DubboUtil.dubboOnlyDiscover(discovery) == dubboOnly
    where: "some discovery"
    discovery             ||  dubboOnly
    'dubbo'               ||  true
    ''                    ||  false
    'dubbo:'              ||  false
    null             || false
  }

  def "DubboDiscoveryEnabled"() {
    expect:
    DubboUtil.dubboDiscoveryEnabled(discovery) == dubboEnalbed
    where: "some discovery"
    discovery                   ||  dubboEnalbed
    'dubbo'                     ||  true
    'http://abc.com?dubbo=true' || true
    'http://abc.com?abc=1&&dubbo=true' || true
    ''                    ||  false
    'dubbo:'              ||  false
    null             || false
    'http://abc.com?dubbo' || false
  }
}
