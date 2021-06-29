package com.lulobank.clients.v3.usecase.command;

import lombok.Getter;

@Getter
public class PepError {
	
	private final String message;
	
	public PepError(String message) {
		this.message = message;
	}
}
