package com.lulobank.clients.starter.utils;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

public class AWSCognitoBearerTokenRequestPostProcessor implements RequestPostProcessor {

  private String token;

  public AWSCognitoBearerTokenRequestPostProcessor(String token) {
    this.token = token;
  }

  @Override
  public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
    request.addHeader("Authorization", "Bearer " + this.token);
    return request;
  }
}
