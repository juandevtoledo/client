package com.lulobank.clients.sdk.operations.dto.login;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfo {
  private String idClient;
  private String email;
  private boolean emailVerified;
  private String name;
  private String lastName;
  private String phoneNumber;
  private Tokens tokens;
  private String previousSuccessfulLogin;
}
