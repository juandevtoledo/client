package com.lulobank.clients.services.features.login.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpResult {

  private String idClient;
  private String email;
  private Boolean emailVerified;
  private String name;
  private String lastName;
  private String idCard;
  private String nickname;
  private String phoneNumber;
  private TokensSignUp tokens;
  private String previousSuccessfulLogin;
  private String checkpoint;
  private String productSelected;

  public SignUpResult(TokensSignUp tokens) {
    this.tokens = tokens;
  }

  public SignUpResult() {}
}
