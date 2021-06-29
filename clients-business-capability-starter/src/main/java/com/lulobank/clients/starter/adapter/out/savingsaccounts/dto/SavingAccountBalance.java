package com.lulobank.clients.starter.adapter.out.savingsaccounts.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SavingAccountBalance {
    private BigDecimal amount;
    private String currency;
    private BigDecimal availableAmount;

}
