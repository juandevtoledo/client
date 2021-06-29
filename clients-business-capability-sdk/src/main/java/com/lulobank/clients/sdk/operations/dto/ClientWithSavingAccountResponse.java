package com.lulobank.clients.sdk.operations.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientWithSavingAccountResponse {
  private String idClient;
  private String idClientCbs;
  private String idClientHash;
  private String savingAccountNumber;
  private String savingAccountHash;
}
