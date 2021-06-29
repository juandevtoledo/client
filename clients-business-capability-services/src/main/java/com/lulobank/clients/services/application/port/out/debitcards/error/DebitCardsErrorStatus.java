package com.lulobank.clients.services.application.port.out.debitcards.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @apiNote This enum must handle business codes from CLI_115 to CLI_119
 */
@Getter
@AllArgsConstructor
public enum DebitCardsErrorStatus {
    CLI_115("Connection error on cards service"),
    CLI_116("Card not found for idClient ");


    private final String message;

    public static final String DEFAULT_DETAIL = "S";
}
