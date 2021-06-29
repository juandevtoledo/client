package com.lulobank.clients.services.features.clientproducts.closuremethodvalidator;

import com.lulobank.clients.services.features.clientproducts.model.SavingsAccount;

import java.math.BigDecimal;
import java.util.function.Predicate;

public class OfficeWithdrawalMethodValidator extends ClosureMethodValidator {

  private final Predicate<SavingsAccount> validateIfOfficeWithdrawalMethodApplies =
      (savingsAccount) ->
          officeWithdrawalMinBalance.compareTo(savingsAccount.getCalculatedBalance()) < 0
              && officeWithdrawalMaxBalance.compareTo(savingsAccount.getCalculatedBalance()) >= 0;

  public OfficeWithdrawalMethodValidator(
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
    return validateIfOfficeWithdrawalMethodApplies;
  }
}
