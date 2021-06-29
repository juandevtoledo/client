package com.lulobank.clients.services.application.port.out.transactions.savingsaccounts.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @apiNote This enum must handle business codes from CLI_110 to CLI_114
 */
@Getter
@AllArgsConstructor
public enum TransactionsErrorStatus {
    CLI_110("Connection error on transactions service"),
    ;

    private final String message;

    public static final String DEFAULT_DETAIL = "S";
}
