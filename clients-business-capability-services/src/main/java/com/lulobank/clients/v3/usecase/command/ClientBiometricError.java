package com.lulobank.clients.v3.usecase.command;

import lombok.Getter;

@Getter
public class ClientBiometricError {
	
	private final String message;
	
	public ClientBiometricError(String message) {
		this.message = message;
	}
}
