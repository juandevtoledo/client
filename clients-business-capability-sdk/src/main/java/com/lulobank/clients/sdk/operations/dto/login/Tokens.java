package com.lulobank.clients.sdk.operations.dto.login;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Tokens {
  private String idToken;
  private String refreshToken;
  private String accessToken;
}
