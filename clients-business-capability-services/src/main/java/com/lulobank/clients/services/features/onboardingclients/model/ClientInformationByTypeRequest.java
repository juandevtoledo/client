package com.lulobank.clients.services.features.onboardingclients.model;

import com.lulobank.core.Command;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientInformationByTypeRequest implements Command {
  private String value;
  private String searchType;

  public ClientInformationByTypeRequest(String searchType, String value) {
    this.value = value;
    this.searchType = searchType;
  }
}
