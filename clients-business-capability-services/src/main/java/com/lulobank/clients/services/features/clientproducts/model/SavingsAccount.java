package com.lulobank.clients.services.features.clientproducts.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Getter
@Setter
public class SavingsAccount {

  private static final BigDecimal GMF_DIVISOR = BigDecimal.valueOf(1000D);
  private static final BigDecimal GMF_MULTIPLIER = BigDecimal.valueOf(4D);

  private String idSavingAccount;
  private BigDecimal balance;
  @JsonIgnore
  private BigDecimal calculatedBalance;
  private BigDecimal cardlessAmount;
  private BigDecimal interestAccrued;
  private LocalDateTime createOn;
  private LocalDateTime startPeriodDate;
  private LocalDateTime lastPeriodDate;
  private boolean gmf;
  private boolean savingAccountClosable;

  public BigDecimal calculateBalance() {
    return isGmf() ? getBalance().subtract(getBalance()
            .divide(GMF_DIVISOR, 0, RoundingMode.DOWN)
            .multiply(GMF_MULTIPLIER))
            : getBalance();
  }
}
