package com.lulobank.clients.services.features.initialclient.model;

import com.lulobank.clients.services.features.login.model.TokensSignUp;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InitialClientCreated {
  private String idClient;
  private TokensSignUp tokensSignUp;

  public InitialClientCreated(String idClient, TokensSignUp tokensSignUp) {
    this.idClient = idClient;
    this.tokensSignUp = tokensSignUp;
  }
}
