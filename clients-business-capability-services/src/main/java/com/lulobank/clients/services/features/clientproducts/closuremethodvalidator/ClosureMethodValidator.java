package com.lulobank.clients.services.features.clientproducts.closuremethodvalidator;

import com.lulobank.clients.services.features.clientproducts.model.SavingsAccount;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.function.Predicate;

@AllArgsConstructor
public abstract class ClosureMethodValidator {

  protected static final BigDecimal transferCost = BigDecimal.valueOf(1000D);

  protected BigDecimal luloMaxBalance;
  protected BigDecimal cardlessWithdrawalMinBalance;
  protected BigDecimal cardlessWithdrawalMaxBalance;
  protected BigDecimal officeWithdrawalMinBalance;
  protected BigDecimal officeWithdrawalMaxBalance;

  public boolean validateIfMethodApplies(SavingsAccount savingsAccount) {
    return getMethodValidator().test(savingsAccount);
  }

  protected abstract Predicate<SavingsAccount> getMethodValidator();
}
