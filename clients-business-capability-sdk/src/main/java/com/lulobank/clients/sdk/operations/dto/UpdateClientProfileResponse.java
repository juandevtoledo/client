package com.lulobank.clients.sdk.operations.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateClientProfileResponse {
  private Boolean isAddressUpdated;
  private String email;
  private String phonePrefix;
  private String phoneNumber;

  public UpdateClientProfileResponse() {
    this.isAddressUpdated = false;
  }
}
