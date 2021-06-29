package com.lulobank.clients.sdk.operations.impl;

import lombok.Getter;

@Getter
public class CognitoResponse {

  private final String access_token;
  private final int expires_in;
  private final String token_type;

  public CognitoResponse(String access_token, int expires_in, String token_type) {
    this.access_token = access_token;
    this.expires_in = expires_in;
    this.token_type = token_type;
  }
}
