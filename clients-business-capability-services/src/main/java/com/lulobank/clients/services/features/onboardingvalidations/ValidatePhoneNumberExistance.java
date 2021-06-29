package com.lulobank.clients.services.features.onboardingvalidations;

import com.lulobank.core.Command;
import lombok.Getter;

@Getter
public class ValidatePhoneNumberExistance implements Command {
  private final int country;
  private final String number;

  public ValidatePhoneNumberExistance(int country, String number) {
    this.country = country;
    this.number = number;
  }
}
