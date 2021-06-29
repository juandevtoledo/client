package com.lulobank.clients.v3.service.productoffers;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.clients.v3.usecase.pep.PepStatus;

import io.vavr.control.Option;
import lombok.CustomLog;

@CustomLog
public class PepValidatorService implements ProductOfferValidatorService {

	private static final List<String> PEP_STATES = Arrays.asList("-1", "0", "3");

	@Override
	public boolean validateProductOffer(ClientsV3Entity clientsV3Entity, Map<String,String> auth) {
		log.info("[PepValidatorService] validateProductOffer()");
		return Option.of(clientsV3Entity)
				.filter(this::isPep)
				.map(validAccount -> true)
				.getOrElse(false);
	}
	
	private boolean isPep(ClientsV3Entity client) {
		return PEP_STATES.contains(getPepResponseStatus(client));
	}

	private String getPepResponseStatus(ClientsV3Entity client) {
		return Option.of(client)
				.filter(clientsV3Entity -> Objects.nonNull(client.getPep()) && Objects.nonNull(client.getDateResponsePep()))
				.map(ClientsV3Entity::getPep)
				.peek(pepResponse -> log.info(String.format("[PepValidatorService] Pep response: %s, idClient: %s", 
						pepResponse, client.getIdClient())))
				.getOrElse(PepStatus.EMPTY_PEP::value);
	}
}
