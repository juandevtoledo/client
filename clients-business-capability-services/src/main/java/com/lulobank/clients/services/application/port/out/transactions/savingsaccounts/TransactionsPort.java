package com.lulobank.clients.services.application.port.out.transactions.savingsaccounts;

import com.lulobank.clients.services.application.port.out.transactions.savingsaccounts.error.TransactionsError;
import io.vavr.control.Either;

import java.util.Map;

public interface TransactionsPort {
    Either<TransactionsError, Boolean> hasPendingTransactions(Map<String, String> headers, String idClient);

}
