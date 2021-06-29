package com.lulobank.clients.starter.v3.handler;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.lulobank.clients.starter.v3.result.ClientFailureResult;
import com.lulobank.clients.starter.v3.result.ClientResult;
import com.lulobank.clients.v3.usecase.ClientsBiometricUseCase;
import com.lulobank.clients.v3.usecase.command.ClientBiometricError;
import com.lulobank.clients.v3.usecase.command.ClientBiometricIdTransactionRequest;

import io.vavr.control.Either;

public class ClientBiometricHandlerTest {
	
	private ClientBiometricHandler subject;
	
	@Mock
	private ClientsBiometricUseCase clientsBiometricUseCase;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		subject = new ClientBiometricHandler(clientsBiometricUseCase);
	}
	
	@Test
	public void getDemographicInfoByClientSuccess() {
		ClientBiometricIdTransactionRequest clientBiometricIdTransaction = buildClientBiometricIdTransactionRequest();
		when(clientsBiometricUseCase.execute(clientBiometricIdTransaction)).thenReturn(Either.right(true));
		ResponseEntity<ClientResult> result = subject.updateClientsBiometric(clientBiometricIdTransaction);
		assertTrue(result.getStatusCode().equals(HttpStatus.OK));
		assertNull(result.getBody());
	}
	
	@Test
	public void getDemographicInfoByClientFailure() {
		ClientBiometricIdTransactionRequest clientBiometricIdTransaction = buildClientBiometricIdTransactionRequest();
		when(clientsBiometricUseCase.execute(clientBiometricIdTransaction)).thenReturn(Either.left(new ClientBiometricError("")));
		ResponseEntity<ClientResult> result = subject.updateClientsBiometric(clientBiometricIdTransaction);
		assertTrue(result.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE));
		assertTrue(result.getBody() instanceof ClientFailureResult);
	}
	
	private ClientBiometricIdTransactionRequest buildClientBiometricIdTransactionRequest() {
		ClientBiometricIdTransactionRequest clientBiometricIdTransactionRequest = new ClientBiometricIdTransactionRequest();
		clientBiometricIdTransactionRequest.setIdClient("idClient");
		clientBiometricIdTransactionRequest.setIdTransactionBiometric("idTransactionBiometric");
		return clientBiometricIdTransactionRequest;
	}
}
