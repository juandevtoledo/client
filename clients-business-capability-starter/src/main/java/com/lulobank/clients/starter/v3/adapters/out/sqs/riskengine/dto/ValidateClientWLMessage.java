package com.lulobank.clients.starter.v3.adapters.out.sqs.riskengine.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ValidateClientWLMessage {
	private final String documentType;
	private final String documentNumber;
}
