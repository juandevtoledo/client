package com.lulobank.clients.services.features.onboardingvalidations;

import lombok.Getter;

@Getter
public class ValidateExistenceResult {
  private Boolean exists;

  public ValidateExistenceResult(Boolean value) {
    this.exists = value;
  }
}
