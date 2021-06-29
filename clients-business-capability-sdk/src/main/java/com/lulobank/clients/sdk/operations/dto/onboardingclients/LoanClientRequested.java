package com.lulobank.clients.sdk.operations.dto.onboardingclients;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoanClientRequested {
  private Double amount;
  private String loanPurpose;
}
