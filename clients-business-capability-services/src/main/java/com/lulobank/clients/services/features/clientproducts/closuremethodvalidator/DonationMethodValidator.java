package com.lulobank.clients.services.features.clientproducts.closuremethodvalidator;

import com.lulobank.clients.services.features.clientproducts.model.SavingsAccount;

import java.math.BigDecimal;
import java.util.function.Predicate;

public class DonationMethodValidator extends ClosureMethodValidator {
  private final Predicate<SavingsAccount> validateIfDonationMethodApplies =
      savingsAccount -> savingsAccount.getCalculatedBalance().compareTo(BigDecimal.ZERO) > 0 ||
              savingsAccount.getInterestAccrued().compareTo(BigDecimal.ZERO) > 0;

  public DonationMethodValidator(
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
    return validateIfDonationMethodApplies;
  }
}
