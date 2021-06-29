package com.lulobank.clients.services.application.port.out.savingsaccounts.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @apiNote This enum must handle business codes from CLI_110 to CLI_114
 */
@Getter
@AllArgsConstructor
public enum SavingsAccountsErrorStatus {
    CLI_110("Connection error on savingsAccounts service"),
    CLI_111("Account not found for idClient "),
    ;

    private final String message;

    public static final String DEFAULT_DETAIL = "S";
}
