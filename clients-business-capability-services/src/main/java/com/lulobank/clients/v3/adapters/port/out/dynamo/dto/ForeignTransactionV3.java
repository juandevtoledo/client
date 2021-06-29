package com.lulobank.clients.v3.adapters.port.out.dynamo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForeignTransactionV3 {
  private String name;
  private CheckingAccountV3 checkingAccount;
}
