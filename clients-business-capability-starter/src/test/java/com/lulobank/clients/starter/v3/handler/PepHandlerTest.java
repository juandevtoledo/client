package com.lulobank.clients.starter.v3.handler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import com.lulobank.clients.starter.v3.handler.pep.PepHandler;
import com.lulobank.clients.v3.usecase.pep.PepStatus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.lulobank.clients.starter.v3.result.ClientFailureResult;
import com.lulobank.clients.v3.usecase.pep.GetPepUseCase;
import com.lulobank.clients.v3.usecase.pep.UpdatePepUseCase;
import com.lulobank.clients.v3.usecase.command.GetPepResponse;
import com.lulobank.clients.v3.usecase.command.PepError;
import com.lulobank.clients.v3.usecase.command.UpdatePepRequest;
import com.lulobank.clients.v3.usecase.command.UpdatePepResponse;

import io.vavr.control.Either;

public class PepHandlerTest {
	
	private PepHandler subject;
	
	@Mock
	private UpdatePepUseCase updatePepUseCase;
	
	@Mock
	private GetPepUseCase getPepUseCase;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		subject = new PepHandler(updatePepUseCase, getPepUseCase);
	}
	
	@Test
	public void updatePepSuccess() {
		UpdatePepRequest updatePepRequest = buildUpdatePepRequest();
		when(updatePepUseCase.execute(updatePepRequest)).thenReturn(Either.right(new UpdatePepResponse(updatePepRequest.getIdClient())));
		ResponseEntity<Object> result = subject.updatePep(updatePepRequest);
		assertEquals(HttpStatus.OK ,result.getStatusCode());
		assertTrue(result.getBody() instanceof UpdatePepResponse);
	}
	
	@Test
	public void getPepSuccess() {
		String idClient = "23344-4234";
		when(getPepUseCase.execute(idClient)).thenReturn(Either.right(new GetPepResponse(PepStatus.PEP_WAIT_LIST.value())));
		ResponseEntity<Object> result = subject.getPep(idClient);
		assertEquals(HttpStatus.OK ,result.getStatusCode());
		assertTrue(result.getBody() instanceof GetPepResponse);
	}

	@Test
	public void updatePepFailed() {
		UpdatePepRequest updatePepRequest = buildUpdatePepRequest();
		when(updatePepUseCase.execute(updatePepRequest)).thenReturn(Either.left(new PepError("")));
		ResponseEntity<Object> result = subject.updatePep(updatePepRequest);
		assertEquals(HttpStatus.NOT_ACCEPTABLE ,result.getStatusCode());
		assertTrue(result.getBody() instanceof ClientFailureResult);
	}
	
	@Test
	public void getPepFailed() {
		String idClient = "23245234";
		when(getPepUseCase.execute(idClient)).thenReturn(Either.left(new PepError("")));
		ResponseEntity<Object> result = subject.getPep(idClient);
		assertEquals(HttpStatus.NOT_ACCEPTABLE ,result.getStatusCode());
		assertTrue(result.getBody() instanceof ClientFailureResult);
	}
	
	private UpdatePepRequest buildUpdatePepRequest() {
		UpdatePepRequest updatePepRequest = new UpdatePepRequest();
		updatePepRequest.setIdClient("idClient");
		updatePepRequest.setPep(true);
		return updatePepRequest;
	}
}
