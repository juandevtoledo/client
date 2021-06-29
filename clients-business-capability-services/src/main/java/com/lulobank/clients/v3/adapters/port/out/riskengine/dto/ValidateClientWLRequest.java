package com.lulobank.clients.v3.adapters.port.out.riskengine.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ValidateClientWLRequest {
	private final String documentType;
	private final String documentNumber;
}
