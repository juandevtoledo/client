package com.lulobank.clients.services.features.recoverpassword.model;

import lombok.Getter;

@Getter
public class RecoverPasswordUpdated {
  private final Boolean successUpdated;

  public RecoverPasswordUpdated(Boolean successUpdated) {
    this.successUpdated = successUpdated;
  }
}
