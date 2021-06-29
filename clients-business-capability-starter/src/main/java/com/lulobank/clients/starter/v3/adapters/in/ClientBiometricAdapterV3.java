package com.lulobank.clients.starter.v3.adapters.in;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lulobank.clients.starter.v3.handler.ClientBiometricHandler;
import com.lulobank.clients.starter.v3.result.ClientResult;
import com.lulobank.clients.v3.usecase.command.ClientBiometricIdTransactionRequest;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v3")
public class ClientBiometricAdapterV3 {

	private final ClientBiometricHandler clientBiometricHandler;

	@Autowired
	public ClientBiometricAdapterV3(ClientBiometricHandler clientBiometricHandler) {
		this.clientBiometricHandler = clientBiometricHandler;
	}

	@PostMapping(value = "/client/{idClient}/identity/biometric")
	public ResponseEntity<ClientResult> updateIdentityBiometric(@RequestHeader final HttpHeaders headers,
			@Valid @PathVariable("idClient") @NotBlank(message = "IdClient is null or empty") String idClient,
			@RequestBody ClientBiometricIdTransactionRequest clientBiometricIdTransactionRequest) {
		clientBiometricIdTransactionRequest.setIdClient(idClient);
		return clientBiometricHandler.updateClientsBiometric(clientBiometricIdTransactionRequest);
	}
}
