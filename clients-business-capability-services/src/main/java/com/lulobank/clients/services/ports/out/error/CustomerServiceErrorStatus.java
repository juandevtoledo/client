package com.lulobank.clients.services.ports.out.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @apiNote This enum must handle business codes from CLI_120 to CLI_121
 */
@Getter
@AllArgsConstructor
public enum CustomerServiceErrorStatus {
    CLI_120("Connection error on customer service"),
    ;

    private final String message;

    public static final String DEFAULT_DETAIL = "CS";
}
