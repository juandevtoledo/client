package com.lulobank.clients.v3.service.productoffers;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.clients.v3.adapters.port.out.saving.SavingAccountService;

import io.vavr.control.Option;
import lombok.CustomLog;

@CustomLog
public class SavingValidatorService implements ProductOfferValidatorService {

	private static final List<String> ACCOUNT_STATES = Arrays.asList("ACTIVE", "APPROVED");

	private final SavingAccountService savingAccountService;
	
	public SavingValidatorService(SavingAccountService savingAccountService) {
		this.savingAccountService = savingAccountService;
	}

	@Override
	public boolean validateProductOffer(ClientsV3Entity clientsV3Entity, Map<String,String> auth) {
		log.info("[SavingValidatorService] validateProductOffer()");
		return Option.of(clientsV3Entity)
				.filter(pep -> accountStateIsValid(clientsV3Entity.getIdClient(), auth))
				.map(validAccount -> true)
				.getOrElse(false);
	}
	
	private boolean accountStateIsValid(String idClient, Map<String,String> auth) {
		return savingAccountService.getSavingAccount(idClient, auth)
				.peek(account -> log.info(String.format("[SavingValidatorService] Saving state:  %s, idClient: %s",
						account.getState(), idClient)))
				.filter(account -> ACCOUNT_STATES.contains(account.getState()))
				.map(account -> true)
				.getOrElse(false);
	}
}
