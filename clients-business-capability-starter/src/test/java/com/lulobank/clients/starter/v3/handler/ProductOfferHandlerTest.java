package com.lulobank.clients.starter.v3.handler;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.lulobank.clients.services.domain.error.UseCaseResponseError;
import com.lulobank.clients.starter.adapter.in.dto.GenericResponse;
import com.lulobank.clients.v3.usecase.productoffers.GetClientProductOfferUseCase;
import com.lulobank.clients.v3.usecase.productoffers.command.ClientProductOffer;
import com.lulobank.clients.v3.usecase.productoffers.command.GetClientProductOfferRequest;

import io.vavr.control.Either;

public class ProductOfferHandlerTest {
	
	private ProductOfferHandler subject;
	
	@Mock
	private GetClientProductOfferUseCase getClientProductOfferUseCase;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		subject = new ProductOfferHandler(getClientProductOfferUseCase);
	}

	@Test
	public void getClientProductOffersSuccess() {
		String idClient = "23123123";
		HttpHeaders headers = new HttpHeaders();
		
		ClientProductOffer clientProductOffer = buildClientProductOffer(); 
		
		when(getClientProductOfferUseCase.execute(isA(GetClientProductOfferRequest.class))).thenReturn(Either.right(clientProductOffer));
		
		ResponseEntity<GenericResponse> response = subject.getClientProductOffers(headers, idClient);
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
	}
	
	@Test
	public void getClientProductOffersError() {
		String idClient = "23123123";
		HttpHeaders headers = new HttpHeaders();
		
		UseCaseResponseError error = new UseCaseResponseError("CLI_101", "providerCode", "detail");
		
		when(getClientProductOfferUseCase.execute(isA(GetClientProductOfferRequest.class))).thenReturn(Either.left(error));
		
		ResponseEntity<GenericResponse> response = subject.getClientProductOffers(headers, idClient);
		assertThat(response.getStatusCode(), is(HttpStatus.NOT_FOUND));
	}
	
	@Test
	public void getClientProductOffersInternalError() {
		String idClient = "23123123";
		HttpHeaders headers = new HttpHeaders();
		
		UseCaseResponseError error = new UseCaseResponseError("businessCode", "providerCode", "detail");
		
		when(getClientProductOfferUseCase.execute(isA(GetClientProductOfferRequest.class))).thenReturn(Either.left(error));
		
		ResponseEntity<GenericResponse> response = subject.getClientProductOffers(headers, idClient);
		assertThat(response.getStatusCode(), is(HttpStatus.INTERNAL_SERVER_ERROR));
	}

	private ClientProductOffer buildClientProductOffer() {
		return ClientProductOffer.builder()
				.idProductOffer("idProductOffer")
				.additionalInfo("additionalInfo")
				.state("state")
				.build();
	}
}
