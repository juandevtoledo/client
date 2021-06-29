package com.lulobank.clients.services.features.clientproducts.closuremethodvalidator;

import com.lulobank.clients.services.features.clientproducts.model.SavingsAccount;

import java.math.BigDecimal;
import java.util.function.Predicate;

public class CardlessWithdrawalMethodValidator extends ClosureMethodValidator {

  private final Predicate<SavingsAccount> validateIfCardlessWithdrawalMethodApplies =
      (savingsAccount) ->
          cardlessWithdrawalMinBalance.compareTo(savingsAccount.getCalculatedBalance()) <= 0
              && cardlessWithdrawalMaxBalance.compareTo(savingsAccount.getCalculatedBalance()) >= 0
              && savingsAccount.getCalculatedBalance().remainder(cardlessWithdrawalMinBalance).compareTo(BigDecimal.ZERO)
                  == 0;

  public CardlessWithdrawalMethodValidator(
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
    return validateIfCardlessWithdrawalMethodApplies;
  }
}
