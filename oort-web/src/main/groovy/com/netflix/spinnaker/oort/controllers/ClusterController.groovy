/*
 * Copyright 2014 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netflix.spinnaker.oort.controllers

import com.netflix.spinnaker.oort.applications.Application
import com.netflix.spinnaker.oort.applications.ApplicationProvider
import com.netflix.spinnaker.oort.clusters.ClusterProvider
import groovy.transform.Canonical
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/applications/{application}/clusters/{account}")
class ClusterController {

  @Autowired
  MessageSource messageSource

  @Autowired
  List<ApplicationProvider> applicationProviders

  @Autowired
  List<ClusterProvider> clusterProviders

  @RequestMapping(method = RequestMethod.GET)
  def list(@PathVariable("account") String account, @PathVariable("application") String application) {
    Map<String, Application> applications = [:]
    applicationProviders.each {
      def applicationObject = it.get(application)
      if (!applicationObject) {
        return
      }
      if (applications.containsKey(applicationObject.name)) {
        def existing = applications[applicationObject.name]
        applications[applicationObject.name] = Application.merge(existing, applicationObject)
      } else {
        applications[applicationObject.name] = applicationObject
      }
    }
    applications.values()?.getAt(0)?.getClusters(account)?.list()
  }

  @RequestMapping(value = "/{cluster}", method = RequestMethod.GET)
  def get(@PathVariable("account") String account, @PathVariable("application") String application, @PathVariable("cluster") String clusterName,
          @RequestParam(value = "zone", required = false) String zoneName) {
    clusterProviders.collect {
      zoneName ? it.getByNameAndZone(application, account, clusterName, zoneName) : it.getByName(application, account, clusterName)
    }?.flatten()
  }

  @RequestMapping(value = "/{cluster}/serverGroups", method = RequestMethod.GET)
  def listServerGroups(@PathVariable("account") String account, @PathVariable("application") String application, @PathVariable("cluster") String clusterName) {
    def serverGroups = []
    for (provider in clusterProviders) {
      def clusters = provider.getByName(application, account, clusterName)
      for (cluster in clusters) {
        serverGroups.addAll cluster.serverGroups
      }
    }
    serverGroups
  }

  @RequestMapping(value = "/{cluster}/serverGroups/{serverGroup}", method = RequestMethod.GET)
  def getServerGroup(@PathVariable("account") String account, @PathVariable("application") String application, @PathVariable("cluster") String clusterName,
              @PathVariable("serverGroup") String serverGroupName) {
    def serverGroups = []
    for (provider in clusterProviders) {
      def clusters = provider.getByName(application, account, clusterName)
      for (cluster in clusters) {
        def serverGroup = cluster.serverGroups.find { it.name == serverGroupName }
        if (serverGroup) {
          serverGroups << serverGroup
        }
      }
    }
    if (!serverGroups) {
      throw new ServerGroupNotFoundException(serverGroupName: serverGroupName)
    } else {
      serverGroups
    }
  }

  @RequestMapping(value = "/{cluster}/serverGroups/{serverGroup}/{zone}", method = RequestMethod.GET)
  def getServerGroupWithZone(@PathVariable("account") String account, @PathVariable("application") String application, @PathVariable("cluster") String clusterName,
             @PathVariable("serverGroup") String serverGroupName, @PathVariable("zone") String zoneName) {
    def serverGroup
    for (provider in clusterProviders) {
      def clusters = provider.getByNameAndZone(application, account, clusterName, zoneName)
      serverGroup = clusters.serverGroups?.flatten()?.find { it.name == serverGroupName }
      if (serverGroup) {
        return serverGroup
      }
    }
    throw new ServerGroupNotFoundException(serverGroupName)
  }

  @ExceptionHandler(ServerGroupNotFoundException)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  Map handleServerGroupNotFoundException(HttpServletRequest req, ServerGroupNotFoundException ex) {
    def message = messageSource.getMessage("serverGroup.not.found", [ex.serverGroupName] as String[], "serverGroup.not.found", LocaleContextHolder.locale)
    [error: "Server group not found", messages: [message], status: HttpStatus.NOT_FOUND]
  }

  @Canonical
  static class ServerGroupNotFoundException extends RuntimeException {
    String serverGroupName
  }


}
