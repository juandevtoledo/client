package com.lulobank.clients.services.features.clientproducts.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Credit {
  private String idCredit;
  private BigDecimal balance;
  private LocalDateTime startPeriodDate;
  private LocalDateTime lastPeriodDate;
  private LocalDateTime createOn;

  public Credit() {}

  public Credit(
      String idCredit,
      BigDecimal balance,
      LocalDateTime startPeriodDate,
      LocalDateTime lastPeriodDate) {
    this.idCredit = idCredit;
    this.balance = balance;
    this.startPeriodDate = startPeriodDate;
    this.lastPeriodDate = lastPeriodDate;
  }
}
