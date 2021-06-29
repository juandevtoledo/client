package com.lulobank.clients.starter.v3.adapters.out.savingsaccountservice;

import java.util.Map;

import com.lulobank.clients.services.domain.error.UseCaseResponseError;
import org.springframework.http.ResponseEntity;

import com.lulobank.clients.starter.v3.adapters.out.savingsaccountservice.dto.SavingAccountType;
import com.lulobank.clients.v3.adapters.port.out.saving.SavingAccountService;
import com.lulobank.clients.v3.adapters.port.out.saving.dto.GetSavingAcountTypeResponse;

import co.com.lulobank.tracing.restTemplate.RestTemplateClient;
import io.vavr.control.Either;
import lombok.CustomLog;

@CustomLog
public class SavingAccountServiceAdapter implements SavingAccountService {

	private static final String GET_SAVING_RESOURCE = "savingsaccounts/account/client/%s";

	private final RestTemplateClient savingsRestTemplateClient;

	public SavingAccountServiceAdapter(RestTemplateClient savingsRestTemplateClient) {
		this.savingsRestTemplateClient = savingsRestTemplateClient;
	}

	@Override
	public Either<UseCaseResponseError, GetSavingAcountTypeResponse> getSavingAccount(String idClient, Map<String,String> auth) {
		log.info("[SavingAccountServiceAdapter] getSavingAccount()");
		String context = String.format(GET_SAVING_RESOURCE, idClient);

		return savingsRestTemplateClient.get(context, auth, SavingAccountType.class)
				.peekLeft(error -> log.error(String.format("Error getting data from getSavingAccount(): %s", error.getBody())))
				.mapLeft(error -> new UseCaseResponseError("CRE_114", "404", "S_SA"))
				.map(ResponseEntity::getBody)
				.map(this::mapGetResponse);

	}
	
	private GetSavingAcountTypeResponse mapGetResponse(SavingAccountType savingAccountType) {

        SavingAccountType.Content content = savingAccountType.getContent();
        return GetSavingAcountTypeResponse.builder()
			.idSavingAccount(content.getIdSavingAccount())
			.state(content.getState())
			.type(content.getType())
			.build();
	}
}
