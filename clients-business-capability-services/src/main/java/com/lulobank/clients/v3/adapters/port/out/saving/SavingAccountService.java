package com.lulobank.clients.v3.adapters.port.out.saving;

import java.util.Map;

import com.lulobank.clients.services.domain.error.UseCaseResponseError;
import com.lulobank.clients.v3.adapters.port.out.saving.dto.GetSavingAcountTypeResponse;

import io.vavr.control.Either;

public interface SavingAccountService {
	
	Either<UseCaseResponseError, GetSavingAcountTypeResponse> getSavingAccount(String idClient, Map<String,String> auth);
}
