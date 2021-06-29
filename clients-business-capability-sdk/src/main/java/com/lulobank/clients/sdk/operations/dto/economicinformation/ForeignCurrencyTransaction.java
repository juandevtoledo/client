package com.lulobank.clients.sdk.operations.dto.economicinformation;

import java.io.Serializable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ForeignCurrencyTransaction implements Serializable {
  private static final long serialVersionUID = 1L;
  private String name;
  private CheckingAccount checkingAccount;

  public ForeignCurrencyTransaction(String name, CheckingAccount checkingAccount) {
    this.name = name;
    this.checkingAccount = checkingAccount;
  }
}
