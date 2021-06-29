package com.lulobank.clients.v3.adapters.port.out.credits;

import java.util.Map;

import com.lulobank.clients.services.domain.error.UseCaseResponseError;
import com.lulobank.clients.v3.adapters.port.out.credits.dto.GetActiveLoandResponse;

import io.vavr.control.Either;

public interface CreditsService {
	
	Either<UseCaseResponseError, GetActiveLoandResponse> getActiveLoan(String idClient, Map<String,String> auth);
}
