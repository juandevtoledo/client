package com.lulobank.clients.services.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoanRequestedStatusFirebase {
  private String status;
  private String detail;

  public LoanRequestedStatusFirebase(String status) {
    this.status = status;
  }
}
