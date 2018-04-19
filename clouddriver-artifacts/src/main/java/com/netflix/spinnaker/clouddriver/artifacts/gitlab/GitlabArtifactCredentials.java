/*
 * Copyright 2018 Joel Wilsson
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
 *
 */

package com.netflix.spinnaker.clouddriver.artifacts.gitlab;

import com.amazonaws.util.StringInputStream;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.spinnaker.clouddriver.artifacts.config.ArtifactCredentials;
import com.netflix.spinnaker.clouddriver.artifacts.github.GitHubArtifactCredentials;
import com.netflix.spinnaker.kork.artifacts.model.Artifact;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Request.Builder;
import com.squareup.okhttp.Response;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Data
public class GitlabArtifactCredentials implements ArtifactCredentials {
  private final String name;

  @JsonIgnore
  private final Builder requestBuilder;

  @JsonIgnore
  OkHttpClient okHttpClient;

  @JsonIgnore
  ObjectMapper objectMapper;

  public GitlabArtifactCredentials(GitlabArtifactAccount account, OkHttpClient okHttpClient, ObjectMapper objectMapper) {
    this.name = account.getName();
    this.okHttpClient = okHttpClient;
    this.objectMapper = objectMapper;
    Builder builder = new Request.Builder();
    boolean useAuth = account.usesAuth();
    boolean userPrivateTokenFile = !StringUtils.isEmpty(account.getPrivateTokenFile());
    if (useAuth) {
      String authHeader = "";

      if (userPrivateTokenFile) {
        authHeader = credentialsFromFile(account.getPrivateTokenFile());
      } else {
        authHeader = account.getPrivateToken();
      }
      builder.header("Private-Token", authHeader);
      log.info("Loaded credentials for gitlab artifact account {}", account.getName());
    } else {
      log.info("No credentials included with gitlab artifact account {}", account.getName());
    }
    requestBuilder = builder;
  }

  private String credentialsFromFile(String filename) {
    try {
      String credentials = FileUtils.readFileToString(new File(filename));
      return credentials.replace("\n", "");
    } catch (IOException e) {
      log.error("Could not read gitlab credentials file {}", filename);
      return null;
    }
  }

  public InputStream download(Artifact artifact) throws IOException {
    HttpUrl.Builder metadataUrlBuilder = HttpUrl.parse(artifact.getReference()).newBuilder();
    String version = artifact.getVersion();
    if (StringUtils.isEmpty(version)) {
      log.info("No version specified for artifact {}, using 'master'.", version);
      version = "master";
    }

    metadataUrlBuilder.addQueryParameter("ref", version);
    Request metadataRequest = requestBuilder
      .url(metadataUrlBuilder.build().toString())
      .build();

    Response response;
    response = okHttpClient.newCall(metadataRequest).execute();
    String body = response.body().string();
    ContentData contentData = objectMapper.readValue(body, ContentData.class);
    if (StringUtils.isEmpty(contentData.content)) {
      throw new FailedDownloadException("Unable to download the contents of artifact:" + artifact);
    }
    return new ByteArrayInputStream(Base64.decodeBase64(contentData.content));

  }

  @Data
  public static class ContentData {
    private String content;
  }


  @Override
  public boolean handlesType(String type) {
    return type.equals("gitlab/file");
  }

  public class FailedDownloadException extends IOException {
    public FailedDownloadException(String message) {
      super(message);
    }

    public FailedDownloadException(String message, Throwable cause) {
      super(message, cause);
    }
  }
}
