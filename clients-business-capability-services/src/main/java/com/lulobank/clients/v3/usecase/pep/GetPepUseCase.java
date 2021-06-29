package com.lulobank.clients.v3.usecase.pep;

import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.clients.v3.usecase.command.GetPepResponse;
import com.lulobank.clients.v3.usecase.command.PepError;
import com.lulobank.clients.v3.usecase.pep.PepStatus;
import com.lulobank.clients.v3.util.UseCase;

import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.CustomLog;

import java.util.Objects;

@CustomLog
public class GetPepUseCase implements UseCase<String, Either<PepError, GetPepResponse>> {

	private final ClientsV3Repository clientsV3Repository;

	public GetPepUseCase(ClientsV3Repository clientsV3Repository) {
		this.clientsV3Repository = clientsV3Repository;
	}

	@Override
	public Either<PepError, GetPepResponse> execute(String idClient) {

		return clientsV3Repository.findByIdClient(idClient)
				.onEmpty(() -> log.error(String.format("Client not found. idClient: %s", idClient)))
				.map(client -> new GetPepResponse(getPepResponseStatus(client)))
				.toEither(() -> new PepError("PEP information not found"));
	}

	private String getPepResponseStatus(ClientsV3Entity client) {
		return Option.of(client)
				.filter(clientsV3Entity -> Objects.nonNull(client.getPep()) && Objects.nonNull(client.getDateResponsePep()))
				.map(ClientsV3Entity::getPep)
				.getOrElse(PepStatus.EMPTY_PEP::value);
	}
}
