package com.lulobank.clients.services.features.onboardingclients.model;

import com.lulobank.core.Command;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetClientInformationByPhone implements Command {

  private Integer country;
  private String phoneNumber;

  public GetClientInformationByPhone(Integer country, String phoneNumber) {
    this.country = country;
    this.phoneNumber = phoneNumber;
  }
}
