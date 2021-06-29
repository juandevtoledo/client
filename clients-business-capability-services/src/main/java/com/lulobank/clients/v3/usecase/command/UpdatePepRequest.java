package com.lulobank.clients.v3.usecase.command;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePepRequest {
	private String idClient;
	private boolean pep;
}
