package com.lulobank.clients.services.features.clientproducts.closuremethodvalidator;

import com.lulobank.clients.services.features.clientproducts.model.SavingsAccount;

import java.math.BigDecimal;
import java.util.function.Predicate;

public class InterbankMethodValidator extends ClosureMethodValidator {

  public final Predicate<SavingsAccount> validateIfInterbankMethodApplies =
      (savingsAccount) -> luloMaxBalance.compareTo(savingsAccount.getCalculatedBalance().subtract(transferCost)) < 0;

  public InterbankMethodValidator(
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
    return validateIfInterbankMethodApplies;
  }
}
