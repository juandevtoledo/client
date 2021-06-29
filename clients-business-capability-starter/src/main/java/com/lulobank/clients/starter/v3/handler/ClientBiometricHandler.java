package com.lulobank.clients.starter.v3.handler;

import static com.lulobank.clients.starter.v3.result.ClientErrorCode.CLIENT_UPDATE_BIOMETRIC_ERROR;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.lulobank.clients.starter.v3.result.ClientFailureResult;
import com.lulobank.clients.starter.v3.result.ClientResult;
import com.lulobank.clients.starter.v3.result.ClientSuccessResult;
import com.lulobank.clients.v3.usecase.ClientsBiometricUseCase;
import com.lulobank.clients.v3.usecase.command.ClientBiometricIdTransactionRequest;

import lombok.CustomLog;

@Component
@CustomLog
public class ClientBiometricHandler {

	private final ClientsBiometricUseCase clientsBiometricUseCase;

	@Autowired
	public ClientBiometricHandler(ClientsBiometricUseCase clientsBiometricUseCase) {
		this.clientsBiometricUseCase = clientsBiometricUseCase;
	}

	public ResponseEntity<ClientResult> updateClientsBiometric(
			ClientBiometricIdTransactionRequest clientBiometricIdTransaction) {

		return clientsBiometricUseCase.execute(clientBiometricIdTransaction)
				.peek(clientsBiometric -> log.info("Client Biometric was update")).map(ClientSuccessResult::new)
				.peekLeft(error -> log.error(error.getMessage())).mapLeft(error -> mapError())
				.fold(left -> new ResponseEntity<>(left, HttpStatus.NOT_ACCEPTABLE),
						right -> new ResponseEntity<>(HttpStatus.OK));
	}

	private ClientFailureResult mapError() {
		return new ClientFailureResult(CLIENT_UPDATE_BIOMETRIC_ERROR, "406", "U");
	}

}
