package com.lulobank.clients.sdk.operations.dto.onboardingclients;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountBasicInfo {
  private String savingsAccount;
  private String gmf;

  public AccountBasicInfo(String savingsAccount, String gmf) {
    this.savingsAccount = savingsAccount;
    this.gmf = gmf;
  }
}
