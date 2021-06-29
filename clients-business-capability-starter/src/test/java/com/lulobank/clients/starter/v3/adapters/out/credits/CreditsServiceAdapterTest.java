package com.lulobank.clients.starter.v3.adapters.out.credits;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.lulobank.clients.services.domain.error.UseCaseResponseError;
import com.lulobank.clients.starter.v3.adapters.out.credits.dto.LoanDetailResponse;
import com.lulobank.clients.v3.adapters.port.out.credits.dto.GetActiveLoandResponse;

import co.com.lulobank.tracing.restTemplate.HttpError;
import co.com.lulobank.tracing.restTemplate.RestTemplateClient;
import io.vavr.control.Either;

public class CreditsServiceAdapterTest {
	
	private static final String LOAN_DETAIL_RESOURCE = "credits/api/v1/loan/client/%s/detail";
	
	private CreditsServiceAdapter creditsServiceAdapter;
	
	@Mock
	private RestTemplateClient creditsRestTemplateClient;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		creditsServiceAdapter = new CreditsServiceAdapter(creditsRestTemplateClient);
	}
	
	@Test
	public void getActiveLoanShouldReturnRight() {

		String idClient = "235345";
		Map<String, String> auth = new HashMap<String, String>();
		String resource = String.format(LOAN_DETAIL_RESOURCE, idClient);
		LoanDetailResponse loanDetailResponse = buildLoanDetailResponse();
		when(creditsRestTemplateClient.get(resource, auth, LoanDetailResponse.class))
				.thenReturn(Either.right(new ResponseEntity<>(loanDetailResponse, HttpStatus.ACCEPTED)));
		Either<UseCaseResponseError, GetActiveLoandResponse> response = creditsServiceAdapter.getActiveLoan(idClient, auth);
		
		assertTrue(response.isRight());
		assertThat(response.get().getIdCredit(), is(loanDetailResponse.getIdCredit()));
	}
	
	@Test
	public void getActiveLoanShouldReturnLeft() {

		String idClient = "235345";
		Map<String, String> auth = new HashMap<String, String>();
		String resource = String.format(LOAN_DETAIL_RESOURCE, idClient);
		when(creditsRestTemplateClient.get(resource, auth, LoanDetailResponse.class))
				.thenReturn(Either.left(new HttpError("500", "Unxepected error trying to consume rest client", null)));
		Either<UseCaseResponseError, GetActiveLoandResponse> response = creditsServiceAdapter.getActiveLoan(idClient, auth);
		
		assertTrue(response.isLeft());
	}

	private LoanDetailResponse buildLoanDetailResponse() {
		LoanDetailResponse loanDetailResponse = new LoanDetailResponse();
		loanDetailResponse.setIdCredit("idCredit");
		loanDetailResponse.setIdLoanCBS("idLoanCBS");
		loanDetailResponse.setInstallments(12);
		return loanDetailResponse;
	}
}
