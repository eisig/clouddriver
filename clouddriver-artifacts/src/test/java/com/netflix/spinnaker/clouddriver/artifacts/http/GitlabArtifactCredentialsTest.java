package com.netflix.spinnaker.clouddriver.artifacts.http;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.spinnaker.clouddriver.artifacts.gitlab.GitlabArtifactAccount;
import com.netflix.spinnaker.clouddriver.artifacts.gitlab.GitlabArtifactCredentials;
import com.netflix.spinnaker.clouddriver.artifacts.s3.S3ArtifactAccount;
import com.netflix.spinnaker.clouddriver.artifacts.s3.S3ArtifactCredentials;
import com.netflix.spinnaker.kork.artifacts.model.Artifact;
import com.squareup.okhttp.OkHttpClient;
import org.junit.Test;
import org.springframework.util.StreamUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import static org.junit.Assert.*;

/**
 * Created by eisig on 2018/4/19.
 */
public class GitlabArtifactCredentialsTest {

  @Test
  public void download() throws IOException {
    OkHttpClient httpClient = new OkHttpClient();
    GitlabArtifactAccount artifactAccount = new GitlabArtifactAccount();
    artifactAccount.setPrivateToken("xx");
    Artifact artifact = new Artifact();
    artifact.setName("nginx-deploy");
    artifact.setReference("https://code.xhqb.io/api/v4/projects/devops%2Fartifacts/repository/files/nginx-demo.yml");
    artifact.setType("gitlb/file");
    Yaml yamlParser = new Yaml();
   ObjectMapper objectMapper =  new ObjectMapper();
   objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    GitlabArtifactCredentials artifactCredentials = new GitlabArtifactCredentials(artifactAccount, httpClient, objectMapper);
    InputStream is = artifactCredentials.download(artifact);
//    String str = StreamUtils.copyToString(is, Charset.forName("utf-8"));
    Object parsed = yamlParser.load(is);


  }



}
