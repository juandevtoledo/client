package com.lulobank.clients.v3.usecase.command;

import lombok.Getter;

@Getter
public class UseCaseError {
	
	private final String message;

	public UseCaseError(String message) {
		this.message = message;
	}
}
