package com.lulobank.clients.starter.v3.adapters.out.credits;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.lulobank.clients.services.domain.error.UseCaseResponseError;
import com.lulobank.clients.starter.v3.adapters.out.credits.dto.LoanDetailResponse;
import com.lulobank.clients.v3.adapters.port.out.credits.CreditsService;
import com.lulobank.clients.v3.adapters.port.out.credits.dto.GetActiveLoandResponse;

import co.com.lulobank.tracing.restTemplate.RestTemplateClient;
import io.vavr.control.Either;
import lombok.AllArgsConstructor;
import lombok.CustomLog;

@CustomLog
@AllArgsConstructor
public class CreditsServiceAdapter implements CreditsService {

	private static final String LOAN_DETAIL_RESOURCE = "credits/api/v1/loan/client/%s/detail";

	private final RestTemplateClient creditsRestTemplateClient;
	
	@Override
	public Either<UseCaseResponseError, GetActiveLoandResponse> getActiveLoan(String idClient,
			Map<String, String> auth) {
		log.info("[CreditsServiceAdapter] getActiveLoan()");
		String context = String.format(LOAN_DETAIL_RESOURCE, idClient);
		
		return creditsRestTemplateClient.get(context, auth, LoanDetailResponse.class)
				.peekLeft(error -> log.error(String.format("Error getting loan detail: %s", error.getBody())))
				.mapLeft(error -> new UseCaseResponseError("CLI_115", "404", "S_CR"))
				.map(ResponseEntity::getBody)
				.map(this::mapGetResponse);
	}
	
	private GetActiveLoandResponse mapGetResponse(LoanDetailResponse loanDetailResponse) {
		return GetActiveLoandResponse.builder()
				.idCredit(loanDetailResponse.getIdCredit())
				.idLoanCBS(loanDetailResponse.getIdLoanCBS())
				.createOn(loanDetailResponse.getCreateOn())
				.installments(loanDetailResponse.getInstallments())
				.state(loanDetailResponse.getState())
				.build();
	}

}
