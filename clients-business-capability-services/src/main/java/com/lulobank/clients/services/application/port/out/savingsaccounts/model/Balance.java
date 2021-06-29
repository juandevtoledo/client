package com.lulobank.clients.services.application.port.out.savingsaccounts.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class Balance {
    private BigDecimal amount;
    private String currency;
    private BigDecimal availableAmount;

}
