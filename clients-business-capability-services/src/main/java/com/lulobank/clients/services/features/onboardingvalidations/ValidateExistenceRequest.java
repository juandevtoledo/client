package com.lulobank.clients.services.features.onboardingvalidations;

import com.lulobank.core.Command;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ValidateExistenceRequest implements Command {
  private String propertie;

  public ValidateExistenceRequest(String propertie) {
    this.propertie = propertie;
  }
}
