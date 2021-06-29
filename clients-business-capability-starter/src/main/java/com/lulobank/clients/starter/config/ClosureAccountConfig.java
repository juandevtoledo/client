package com.lulobank.clients.starter.config;

import com.lulobank.clients.services.features.clientproducts.closuremethodvalidator.CardlessWithdrawalMethodValidator;
import com.lulobank.clients.services.features.clientproducts.closuremethodvalidator.CashierCheckMethodValidator;
import com.lulobank.clients.services.features.clientproducts.closuremethodvalidator.ClosureMethodValidator;
import com.lulobank.clients.services.features.clientproducts.closuremethodvalidator.DonationMethodValidator;
import com.lulobank.clients.services.features.clientproducts.closuremethodvalidator.InterbankMethodValidator;
import com.lulobank.clients.services.features.clientproducts.closuremethodvalidator.LuloMethodValidator;
import com.lulobank.clients.services.features.clientproducts.closuremethodvalidator.OfficeWithdrawalMethodValidator;
import com.lulobank.clients.services.utils.BalanceClosureMethods;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClosureAccountConfig {
  @Value("${savingaccount.closure.balance.lulo.max}")
  private BigDecimal luloMaxBalance;

  @Value("${savingaccount.closure.balance.cardlessWithdrawal.min}")
  private BigDecimal cardlessWithdrawalMinBalance;

  @Value("${savingaccount.closure.balance.cardlessWithdrawal.max}")
  private BigDecimal cardlessWithdrawalMaxBalance;

  @Value("${savingaccount.closure.balance.officeWithdrawal.min}")
  private BigDecimal officeWithdrawalMinBalance;

  @Value("${savingaccount.closure.balance.officeWithdrawal.max}")
  private BigDecimal officeWithdrawalMaxBalance;

  @Bean
  public Map<String, ClosureMethodValidator> closureMethodValidatorMap() {
    Map<String, ClosureMethodValidator> closureMethodValidatorMap = new HashMap<>();
    closureMethodValidatorMap.put(
        BalanceClosureMethods.LULO.name(),
        new LuloMethodValidator(
            luloMaxBalance,
            cardlessWithdrawalMinBalance,
            cardlessWithdrawalMaxBalance,
            officeWithdrawalMinBalance,
            officeWithdrawalMaxBalance));
    closureMethodValidatorMap.put(
        BalanceClosureMethods.CARDLESS_WITHDRAWAL.name(),
        new CardlessWithdrawalMethodValidator(
            luloMaxBalance,
            cardlessWithdrawalMinBalance,
            cardlessWithdrawalMaxBalance,
            officeWithdrawalMinBalance,
            officeWithdrawalMaxBalance));
    closureMethodValidatorMap.put(
        BalanceClosureMethods.INTERBANK.name(),
        new InterbankMethodValidator(
            luloMaxBalance,
            cardlessWithdrawalMinBalance,
            cardlessWithdrawalMaxBalance,
            officeWithdrawalMinBalance,
            officeWithdrawalMaxBalance));
    closureMethodValidatorMap.put(
        BalanceClosureMethods.OFFICE_WITHDRAWAL.name(),
        new OfficeWithdrawalMethodValidator(
            luloMaxBalance,
            cardlessWithdrawalMinBalance,
            cardlessWithdrawalMaxBalance,
            officeWithdrawalMinBalance,
            officeWithdrawalMaxBalance));
    closureMethodValidatorMap.put(
        BalanceClosureMethods.CASHIERCHECK.name(),
        new CashierCheckMethodValidator(
            luloMaxBalance,
            cardlessWithdrawalMinBalance,
            cardlessWithdrawalMaxBalance,
            officeWithdrawalMinBalance,
            officeWithdrawalMaxBalance));
    closureMethodValidatorMap.put(
        BalanceClosureMethods.DONATION.name(),
        new DonationMethodValidator(
            luloMaxBalance,
            cardlessWithdrawalMinBalance,
            cardlessWithdrawalMaxBalance,
            officeWithdrawalMinBalance,
            officeWithdrawalMaxBalance));
    return closureMethodValidatorMap;
  }
}
