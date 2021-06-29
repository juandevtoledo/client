package com.lulobank.clients.v3.adapters.port.out.savingsaccount.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SavingAccountServiceErrorStatus {
    CLI_140("Connection error on Savings account v3 service"),
    CLI_141("Check referral hold error on Savings account v3 service"),
    CLI_142("Client Don't have product"),
    ;

    private final String message;

    public static final String DEFAULT_DETAIL = "SAS";
}
