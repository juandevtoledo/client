package com.lulobank.clients.services.domain;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForeignTransaction extends ValueObject<ForeignTransaction> {
  private String name;
  private CheckingAccount checkingAccount;

  public ForeignTransaction() {}

  public ForeignTransaction(String name, CheckingAccount checkingAccount) {
    this.name = name;
    this.checkingAccount = checkingAccount;
  }

  @Override
  protected List<Supplier> supplyGettersToIncludeInEqualityCheck() {
    return Arrays.asList(this::getName, this::getCheckingAccount);
  }
}
