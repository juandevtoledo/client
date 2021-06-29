package com.lulobank.clients.starter.v3.adapters.in.sqs.handler;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.isA;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.lulobank.clients.services.domain.error.UseCaseResponseError;
import com.lulobank.clients.starter.v3.adapters.in.sqs.event.CreateProductOfferMessage;
import com.lulobank.clients.starter.v3.adapters.in.sqs.handler.CreateProductOfferHandler;
import com.lulobank.clients.v3.usecase.productoffers.CreateClientProductOfferUseCase;
import com.lulobank.clients.v3.usecase.productoffers.command.ClientProductOffer;
import com.lulobank.clients.v3.usecase.productoffers.command.CreateClientProductOfferRequest;

import io.vavr.control.Either;
import io.vavr.control.Try;

public class CreateProductOfferHandlerTest {
	
	private CreateProductOfferHandler createProductOfferHandler;
	
	@Mock
	private CreateClientProductOfferUseCase createClientProductOfferUseCase;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		createProductOfferHandler = new CreateProductOfferHandler(createClientProductOfferUseCase);
	}
	
	@Test
	public void executeShouldReturnSuccess() {
		CreateProductOfferMessage event = buildCreateProductOfferMessage();
		ClientProductOffer clientProductOffer = buildClientProductOffer();
		when(createClientProductOfferUseCase.execute(isA(CreateClientProductOfferRequest.class))).thenReturn(Either.right(clientProductOffer));
		Try<Void> response = createProductOfferHandler.execute(event);
		assertThat(response.isSuccess(), is(true));
	}
	
	@Test
	public void executeShouldReturnFailed() {
		CreateProductOfferMessage event = buildCreateProductOfferMessage();
		UseCaseResponseError useCaseResponseError = buildUseCaseResponseError();
		when(createClientProductOfferUseCase.execute(isA(CreateClientProductOfferRequest.class))).thenReturn(Either.left(useCaseResponseError));
		Try<Void> response = createProductOfferHandler.execute(event);
		assertThat(response.isFailure(), is(true));
	}

	private UseCaseResponseError buildUseCaseResponseError() {
		UseCaseResponseError useCaseResponseError = new UseCaseResponseError();
		return useCaseResponseError;
	}

	private ClientProductOffer buildClientProductOffer() {
		return ClientProductOffer.builder().build();
	}

	private CreateProductOfferMessage buildCreateProductOfferMessage() {
		CreateProductOfferMessage createProductOfferMessage = new CreateProductOfferMessage();
		createProductOfferMessage.setIdClient("idClient");
		createProductOfferMessage.setType("type");
		createProductOfferMessage.setValue(10);
		return createProductOfferMessage;
	}

}
