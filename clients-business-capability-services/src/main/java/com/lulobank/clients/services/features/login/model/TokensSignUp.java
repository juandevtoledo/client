package com.lulobank.clients.services.features.login.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokensSignUp {
  private String idToken;
  private String refreshToken;
  private String accessToken;

  public TokensSignUp(String idToken, String refreshToken, String accessToken) {
    this.idToken = idToken;
    this.refreshToken = refreshToken;
    this.accessToken = accessToken;
  }
}
