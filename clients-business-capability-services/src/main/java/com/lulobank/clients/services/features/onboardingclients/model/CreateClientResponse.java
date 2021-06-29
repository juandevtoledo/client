package com.lulobank.clients.services.features.onboardingclients.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateClientResponse {
  private String userId;

  public CreateClientResponse(String userId) {
    this.userId = userId;
  }
}
