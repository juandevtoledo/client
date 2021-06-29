package com.lulobank.clients.starter.v3.handler;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.lulobank.clients.sdk.operations.dto.economicinformation.ClientEconomicInformation;
import com.lulobank.clients.starter.v3.result.ClientFailureResult;
import com.lulobank.clients.starter.v3.result.ClientResult;
import com.lulobank.clients.v3.usecase.economicinformation.SaveEconomicInformationUseCase;
import com.lulobank.clients.v3.usecase.command.UseCaseError;

import io.vavr.control.Either;

import java.util.HashMap;

public class EconomicInformationHandlerTest {

	private EconomicInformationHandler subject;

	@Mock
	private SaveEconomicInformationUseCase economicInformationUseCase;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		subject = new EconomicInformationHandler(economicInformationUseCase);
	}

	@Test
	public void shouldSaveEconomicInformationSuccess() {
		ClientEconomicInformation clientEconomicInformation = new ClientEconomicInformation();
		when(economicInformationUseCase.execute(clientEconomicInformation))
				.thenReturn(Either.right(new ClientsV3Entity()));
		ResponseEntity<ClientResult> result = subject.saveEconomicInformation(clientEconomicInformation, new HashMap<>());
		assertEquals(result.getStatusCode(), HttpStatus.CREATED);
		assertNull(result.getBody());
	}

	@Test
	public void shouldSaveEconomicInformationFailed() {
		ClientEconomicInformation clientEconomicInformation = new ClientEconomicInformation();
		when(economicInformationUseCase.execute(clientEconomicInformation))
				.thenReturn(Either.left(new UseCaseError("")));
		ResponseEntity<ClientResult> result = subject.saveEconomicInformation(clientEconomicInformation, new HashMap<>());
		assertEquals(result.getStatusCode(), HttpStatus.NOT_ACCEPTABLE);
		assertTrue(result.getBody() instanceof ClientFailureResult);
	}
}
