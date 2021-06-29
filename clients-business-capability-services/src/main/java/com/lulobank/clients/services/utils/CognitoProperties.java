package com.lulobank.clients.services.utils;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import lombok.Getter;

@Getter
public class CognitoProperties {
  private String pool_id;
  private String clientapp_id;
  private String custom_domain;
  private AWSCognitoIdentityProvider awsCognitoIdentityProvider;

  public CognitoProperties(
      String pool_id,
      String clientapp_id,
      String custom_domain,
      AWSCognitoIdentityProvider awsCognitoIdentityProvider) {
    this.pool_id = pool_id;
    this.clientapp_id = clientapp_id;
    this.custom_domain = custom_domain;
    this.awsCognitoIdentityProvider = awsCognitoIdentityProvider;
  }
}
