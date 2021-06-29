package com.lulobank.clients.services.features.onboardingclients.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientPendingValidations {
  private String blackListState;
  private Boolean emailVerified;
  private Boolean clientExists;
}
