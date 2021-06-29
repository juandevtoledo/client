package com.lulobank.clients.v3.adapters.port.out.dynamo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IdentityBiometricV3 {
  private String idTransaction;
  private String status;
  private TransactionStateV3 transactionState;
}
