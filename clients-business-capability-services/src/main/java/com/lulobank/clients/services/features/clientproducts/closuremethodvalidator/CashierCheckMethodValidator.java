package com.lulobank.clients.services.features.clientproducts.closuremethodvalidator;

import com.lulobank.clients.services.features.clientproducts.model.SavingsAccount;

import java.math.BigDecimal;
import java.util.function.Predicate;

public class CashierCheckMethodValidator extends ClosureMethodValidator {

  private final Predicate<SavingsAccount> validateIfCashierCheckMethodApplies =
      (savingsAccount) -> officeWithdrawalMaxBalance.compareTo(savingsAccount.getCalculatedBalance()) < 0;

  public CashierCheckMethodValidator(
      BigDecimal luloMaxBalance,
      BigDecimal cardlessWithdrawalMinBalance,
      BigDecimal cardlessWithdrawalMaxBalance,
      BigDecimal officeWithdrawalMinBalance,
      BigDecimal officeWithdrawalMaxBalance) {
    super(
        luloMaxBalance,
        cardlessWithdrawalMinBalance,
        cardlessWithdrawalMaxBalance,
        officeWithdrawalMinBalance,
        officeWithdrawalMaxBalance);
  }

  @Override
  protected Predicate<SavingsAccount> getMethodValidator() {
    return validateIfCashierCheckMethodApplies;
  }
}
