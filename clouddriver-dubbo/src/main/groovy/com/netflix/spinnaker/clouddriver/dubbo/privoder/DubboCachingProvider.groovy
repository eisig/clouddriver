package com.netflix.spinnaker.clouddriver.dubbo.privoder

import com.netflix.spinnaker.cats.agent.Agent
import com.netflix.spinnaker.clouddriver.core.provider.agent.ExternalHealthProvider

/**
 * Created by eisig on 2017/9/10.
 */
class DubboCachingProvider implements ExternalHealthProvider {

  public static final String PROVIDER_NAME = 'dubbo'

  final Set<String> defaultCaches = Collections.emptySet()

  final Collection<Agent> agents

  DubboCachingProvider(Collection<Agent> agents) {
    this.agents = agents
  }

  @Override
  String getProviderName() {
    return PROVIDER_NAME
  }

}
