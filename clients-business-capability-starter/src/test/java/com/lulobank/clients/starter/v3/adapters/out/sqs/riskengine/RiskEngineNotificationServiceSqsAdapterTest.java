package com.lulobank.clients.starter.v3.adapters.out.sqs.riskengine;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.lulobank.clients.v3.adapters.port.out.riskengine.dto.ValidateClientWLRequest;

import co.com.lulobank.tracing.sqs.SqsBraveTemplate;
import io.vavr.control.Try;

public class RiskEngineNotificationServiceSqsAdapterTest {
	
	private RiskEngineServiceSqsAdapter riskEngineNotificationServiceSqsAdapter;

	@Mock
	private SqsBraveTemplate sqsBraveTemplate;
	
	private String riskEngineQueue;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		riskEngineQueue = "riskEngineQueue";
		riskEngineNotificationServiceSqsAdapter = new RiskEngineServiceSqsAdapter(sqsBraveTemplate, riskEngineQueue);
	}
	
	@Test
	public void createProductOfferSuccess() {
		ValidateClientWLRequest validateClientWLRequest = buildValidateClientWLRequest();
		Try<Void> response = riskEngineNotificationServiceSqsAdapter.sendValidateClientWLMessage(validateClientWLRequest);
		assertTrue(response.isSuccess());
		verify(sqsBraveTemplate).convertAndSend(eq(riskEngineQueue), any());
	}

	private ValidateClientWLRequest buildValidateClientWLRequest() {
		return ValidateClientWLRequest.builder()
				.documentNumber("documentNumber")
				.documentType("documentType")
				.build();
	}
}
