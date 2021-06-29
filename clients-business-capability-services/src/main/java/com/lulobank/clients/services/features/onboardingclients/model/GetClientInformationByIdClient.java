package com.lulobank.clients.services.features.onboardingclients.model;

import com.lulobank.core.Command;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetClientInformationByIdClient implements Command {

  private String idClient;

  public GetClientInformationByIdClient(String idClient) {
    this.idClient = idClient;
  }
}
