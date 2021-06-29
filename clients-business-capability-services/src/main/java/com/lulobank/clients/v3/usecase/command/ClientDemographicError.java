package com.lulobank.clients.v3.usecase.command;

import lombok.Getter;

@Getter
public class ClientDemographicError {
	
	private final String message;
	
	public ClientDemographicError(String message) {
		this.message = message;
	}
}
