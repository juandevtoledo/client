package com.lulobank.clients.v3.usecase.command;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdatePepResponse {
	private String idClient;

	public UpdatePepResponse(String idClient) {
		this.idClient = idClient;
	}
}
