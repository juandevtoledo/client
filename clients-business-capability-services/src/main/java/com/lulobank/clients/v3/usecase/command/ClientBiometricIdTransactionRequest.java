package com.lulobank.clients.v3.usecase.command;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientBiometricIdTransactionRequest {
	private String idClient;
	private String idTransactionBiometric;
}
