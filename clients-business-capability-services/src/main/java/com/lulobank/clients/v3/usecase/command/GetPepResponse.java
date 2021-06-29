package com.lulobank.clients.v3.usecase.command;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetPepResponse {
	private String pep;

	public GetPepResponse(String pep) {
		this.pep = pep;
	}
}
