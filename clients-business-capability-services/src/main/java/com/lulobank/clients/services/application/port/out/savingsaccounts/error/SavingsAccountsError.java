package com.lulobank.clients.services.application.port.out.savingsaccounts.error;

import com.lulobank.clients.services.application.util.HttpDomainStatus;
import com.lulobank.clients.services.domain.error.UseCaseResponseError;

import static com.lulobank.clients.services.application.port.out.savingsaccounts.error.SavingsAccountsErrorStatus.CLI_110;
import static com.lulobank.clients.services.application.port.out.savingsaccounts.error.SavingsAccountsErrorStatus.CLI_111;

public class SavingsAccountsError extends UseCaseResponseError {
    public SavingsAccountsError(SavingsAccountsErrorStatus savingsAccountsErrorStatus, String providerCode) {
        super(savingsAccountsErrorStatus.name(), providerCode, SavingsAccountsErrorStatus.DEFAULT_DETAIL);
    }

    public static SavingsAccountsError connectionError(String providerCode) {
        return new SavingsAccountsError(CLI_110, providerCode);
    }

    public static SavingsAccountsError connectionError() {
        return new SavingsAccountsError(CLI_110, String.valueOf(HttpDomainStatus.BAD_GATEWAY.value()));
    }

    public static SavingsAccountsError accountNotFound() {
        return new SavingsAccountsError(CLI_111, String.valueOf(HttpDomainStatus.NOT_FOUND.value()));
    }
}
