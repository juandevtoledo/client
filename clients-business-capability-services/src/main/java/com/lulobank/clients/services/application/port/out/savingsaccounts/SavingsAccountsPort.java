package com.lulobank.clients.services.application.port.out.savingsaccounts;

import com.lulobank.clients.services.application.port.out.savingsaccounts.error.SavingsAccountsError;
import com.lulobank.clients.services.application.port.out.savingsaccounts.model.SavingAccount;
import io.vavr.control.Either;

import java.util.Map;

public interface SavingsAccountsPort {
    Either<SavingsAccountsError, SavingAccount> getSavingsAccountsByIdClient(Map<String, String> headers, String idClient);

}
