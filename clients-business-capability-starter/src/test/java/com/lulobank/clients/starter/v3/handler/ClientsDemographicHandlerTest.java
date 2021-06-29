package com.lulobank.clients.starter.v3.handler;

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
import com.lulobank.clients.starter.v3.result.ClientSuccessResult;
import com.lulobank.clients.v3.usecase.ClientsDemographicUseCase;
import com.lulobank.clients.v3.usecase.command.ClientDemographicError;
import com.lulobank.clients.v3.usecase.command.ClientDemographicInfo;

import io.vavr.control.Either;

public class ClientsDemographicHandlerTest {
	
	private ClientsDemographicHandler subject;
	
	@Mock
	private ClientsDemographicUseCase clientsDemographicUseCase;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		subject = new ClientsDemographicHandler(clientsDemographicUseCase);
	}
	
	@Test
	public void getDemographicInfoByClientSuccess() {
		String idClient = "353qer4";
		when(clientsDemographicUseCase.execute(idClient)).thenReturn(Either.right(new ClientDemographicInfo()));
		ResponseEntity<ClientResult> result = subject.getDemographicInfoByClient(idClient);
		assertTrue(result.getStatusCode().equals(HttpStatus.OK));
		assertTrue(result.getBody() instanceof ClientSuccessResult);
	}
	
	@Test
	public void getDemographicInfoByClientFailure() {
		String idClient = "wqd324";
		when(clientsDemographicUseCase.execute(idClient)).thenReturn(Either.left(new ClientDemographicError("")));
		ResponseEntity<ClientResult> result = subject.getDemographicInfoByClient(idClient);
		assertTrue(result.getStatusCode().equals(HttpStatus.NOT_FOUND));
		assertTrue(result.getBody() instanceof ClientFailureResult);
	}
}
