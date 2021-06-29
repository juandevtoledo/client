package com.lulobank.clients.services.features.riskengine.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientCreated {

  private String userId;

  public ClientCreated(String userId) {
    this.userId = userId;
  }
}
