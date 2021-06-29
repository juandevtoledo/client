package com.lulobank.clients.services.features.onboardingclients.model;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoanConditionsRequest implements Serializable {
  private static final long serialVersionUID = 1L;
  private Double amount;
  private Float interestRate;
  private Float defaultRate;
  private Integer installments;
  private Double maxAmountInstallment;
  private String type;
}
