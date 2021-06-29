package com.lulobank.clients.services.inboundadapters.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoanConditions {
  private Double amount;
  private Float interestRate;
  private Float defaultRate;
  private Integer installments;
  private Double maxAmountInstallment;
  private String type;

  public LoanConditions(
      Double amount,
      Float interestRate,
      Float defaultRate,
      Integer installments,
      Double maxAmountInstallment,
      String type) {
    this.amount = amount;
    this.interestRate = interestRate;
    this.defaultRate = defaultRate;
    this.installments = installments;
    this.maxAmountInstallment = maxAmountInstallment;
    this.type = type;
  }
}
