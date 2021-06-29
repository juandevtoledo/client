package com.lulobank.clients.services.features.onboardingclients.model;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForeignTransactionCreateClientRequest implements Serializable {
  private static final long serialVersionUID = 1L;
  private String name;
  private CheckingAccountCreateClientRequest checkingAccount;

  public ForeignTransactionCreateClientRequest() {
    // Empty constructor required for mapping in Jackson
  }

  public ForeignTransactionCreateClientRequest(
      String name, CheckingAccountCreateClientRequest checkingAccount) {
    this.name = name;
    this.checkingAccount = checkingAccount;
  }
}
