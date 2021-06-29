package com.lulobank.clients.sdk.operations.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BiometricIdentification {
  private String idApplicant;

  public BiometricIdentification() {}

  public BiometricIdentification(String idApplicant) {
    this.idApplicant = idApplicant;
  }
}
