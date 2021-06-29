package com.lulobank.clients.sdk.operations.dto.onboardingclients;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OnBoardingStatus {
  private String checkpoint;
  private String productSelected;
  private LoanClientRequested loanClientRequested;
}
