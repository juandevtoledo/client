package com.lulobank.clients.sdk.operations.dto.login;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
  private String password;
  private String username;
}
