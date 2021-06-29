package com.lulobank.clients.sdk.operations.dto.login;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {
  private UserInfo content;
  private boolean hasErrors;
  private String accessToken;
}
