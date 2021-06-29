package com.lulobank.clients.starter.v3.handler;

import static com.lulobank.clients.starter.v3.result.ClientErrorCode.CLIENT_SAVE_ECONOMIC_INFORMATION_ERROR;

import com.lulobank.clients.sdk.operations.AdapterCredentials;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.lulobank.clients.sdk.operations.dto.economicinformation.ClientEconomicInformation;
import com.lulobank.clients.starter.v3.result.ClientFailureResult;
import com.lulobank.clients.starter.v3.result.ClientResult;
import com.lulobank.clients.v3.usecase.economicinformation.SaveEconomicInformationUseCase;

import lombok.CustomLog;

import java.util.Map;

@Component
@CustomLog
public class EconomicInformationHandler {
	
	private final SaveEconomicInformationUseCase economicInformationUseCase;
	
	public EconomicInformationHandler(SaveEconomicInformationUseCase economicInformationUseCase) {
		this.economicInformationUseCase = economicInformationUseCase;
	}

	public ResponseEntity<ClientResult> saveEconomicInformation(ClientEconomicInformation clientEconomicInformation, Map<String,String> header){
		return economicInformationUseCase.execute(setCredentials(clientEconomicInformation,header))
				.peek(clientsBiometric -> log.info("Economic information was saved"))
				.peekLeft(error -> log.error(error.getMessage()))
				.mapLeft(error -> mapError())
				.fold(left -> new ResponseEntity<>(left, HttpStatus.NOT_ACCEPTABLE),
						right -> new ResponseEntity<>(HttpStatus.CREATED));
	}
	
	private ClientFailureResult mapError() {
		return new ClientFailureResult(CLIENT_SAVE_ECONOMIC_INFORMATION_ERROR, "406", "D");
	}

	private ClientEconomicInformation setCredentials(ClientEconomicInformation clientEconomicInformation, Map<String,String> header){
		clientEconomicInformation.setAdapterCredentials(new AdapterCredentials(header));
		return clientEconomicInformation;
	}
}
