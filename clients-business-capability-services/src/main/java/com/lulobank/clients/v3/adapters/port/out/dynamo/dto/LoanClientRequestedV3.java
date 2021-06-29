package com.lulobank.clients.v3.adapters.port.out.dynamo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoanClientRequestedV3 {
  private Double amount;
  private String loanPurpose;

  public LoanClientRequestedV3() {}

  public LoanClientRequestedV3(Double amount, String loanPurpose) {
    this.amount = amount;
    this.loanPurpose = loanPurpose;
  }
}
