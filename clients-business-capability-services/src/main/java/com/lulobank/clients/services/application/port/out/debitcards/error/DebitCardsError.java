package com.lulobank.clients.services.application.port.out.debitcards.error;

import com.lulobank.clients.services.application.util.HttpDomainStatus;
import com.lulobank.clients.services.domain.error.UseCaseResponseError;

import static com.lulobank.clients.services.application.port.out.debitcards.error.DebitCardsErrorStatus.CLI_115;
import static com.lulobank.clients.services.application.port.out.debitcards.error.DebitCardsErrorStatus.CLI_116;

public class DebitCardsError extends UseCaseResponseError {
    public DebitCardsError(DebitCardsErrorStatus debitCardsErrorStatus, String providerCode) {
        super(debitCardsErrorStatus.name(), providerCode, DebitCardsErrorStatus.DEFAULT_DETAIL);
    }

    public static DebitCardsError connectionError(String providerCode) {
        return new DebitCardsError(CLI_115, providerCode);
    }

    public static DebitCardsError connectionError() {
        return new DebitCardsError(CLI_115, String.valueOf(HttpDomainStatus.BAD_GATEWAY.value()));
    }

    public static DebitCardsError cardNotFound() {
        return new DebitCardsError(CLI_116, String.valueOf(HttpDomainStatus.NOT_FOUND.value()));
    }
}
