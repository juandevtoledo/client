package com.lulobank.clients.services.application.port.out.transactions.savingsaccounts.error;

import com.lulobank.clients.services.application.util.HttpDomainStatus;
import com.lulobank.clients.services.domain.error.UseCaseResponseError;

import static com.lulobank.clients.services.application.port.out.transactions.savingsaccounts.error.TransactionsErrorStatus.CLI_110;


public class TransactionsError extends UseCaseResponseError {
    public TransactionsError(TransactionsErrorStatus transactionsErrorStatus, String providerCode) {
        super(transactionsErrorStatus.name(), providerCode, TransactionsErrorStatus.DEFAULT_DETAIL);
    }

    public static TransactionsError connectionError(String providerCode) {
        return new TransactionsError(CLI_110, providerCode);
    }

    public static TransactionsError connectionError() {
        return new TransactionsError(CLI_110, String.valueOf(HttpDomainStatus.BAD_GATEWAY.value()));
    }

}
