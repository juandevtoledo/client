package com.lulobank.clients.starter.v3.adapters.out.sqs.riskengine;

import com.lulobank.clients.starter.v3.adapters.out.sqs.riskengine.dto.ValidateClientWLMessage;
import com.lulobank.clients.v3.adapters.port.out.riskengine.RiskEngineService;
import com.lulobank.clients.v3.adapters.port.out.riskengine.dto.ValidateClientWLRequest;
import com.lulobank.events.api.EventFactory;

import co.com.lulobank.tracing.sqs.SqsBraveTemplate;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.CustomLog;

@CustomLog
@AllArgsConstructor
public class RiskEngineServiceSqsAdapter implements RiskEngineService {

	private final SqsBraveTemplate sqsBraveTemplate;
	private final String riskEngineQueue;
	
	@Override
	public Try<Void> sendValidateClientWLMessage(ValidateClientWLRequest validateClientWLRequest) {
		log.info("[RiskEngineNotificationServiceSqsAdapter] sendValidateClientWLMessage()");
		return Try.run(() -> sqsBraveTemplate.convertAndSend(riskEngineQueue,
				EventFactory.ofDefaults(buildRequestRiskEngineEvent(validateClientWLRequest)).build()));
	}

	private ValidateClientWLMessage buildRequestRiskEngineEvent(ValidateClientWLRequest validateClientWLRequest) {
		return ValidateClientWLMessage.builder()
				.documentNumber(validateClientWLRequest.getDocumentNumber())
				.documentType(validateClientWLRequest.getDocumentType())
				.build();
	}
}
