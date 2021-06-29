package com.lulobank.clients.v3.adapters.port.out.savingsaccount;

import com.lulobank.clients.v3.adapters.port.out.savingsaccount.error.SavingsAccountError;
import com.lulobank.clients.v3.adapters.port.out.savingsaccount.dto.SavingsAccountRequest;
import com.lulobank.clients.v3.adapters.port.out.savingsaccount.dto.SavingsAccountResponse;
import io.vavr.control.Either;

import java.util.Map;

public interface SavingsAccountV3Service {
    Either<SavingsAccountError,SavingsAccountResponse> create(SavingsAccountRequest createSavingsAccountEntity, Map<String, String> auth);
}
