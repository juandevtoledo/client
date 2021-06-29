package com.lulobank.clients.v3.adapters.port.out.riskengine;

import com.lulobank.clients.v3.adapters.port.out.riskengine.dto.ValidateClientWLRequest;

import io.vavr.control.Try;

public interface RiskEngineService {
	
	Try<Void> sendValidateClientWLMessage(ValidateClientWLRequest validateClientWLRequest);
}
