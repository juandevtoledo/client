package com.lulobank.clients.v3.usecase.pep;

import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.service.pep.PepInformationService;
import com.lulobank.clients.v3.usecase.command.PepError;
import com.lulobank.clients.v3.usecase.command.UpdatePepRequest;
import com.lulobank.clients.v3.usecase.command.UpdatePepResponse;
import com.lulobank.clients.v3.util.UseCase;

import io.vavr.control.Either;
import lombok.CustomLog;

@CustomLog
public class UpdatePepUseCase implements UseCase<UpdatePepRequest, Either<PepError, UpdatePepResponse>> {

	private final ClientsV3Repository clientsV3Repository;

	public UpdatePepUseCase(ClientsV3Repository clientsV3Repository) {
		this.clientsV3Repository = clientsV3Repository;
	}

	@Override
	public Either<PepError, UpdatePepResponse> execute(UpdatePepRequest updatePepRequest) {

		return  clientsV3Repository.findByIdClient(updatePepRequest.getIdClient())
				.onEmpty(() -> log.error(String.format("Client not found. idClient: %s", updatePepRequest.getIdClient())))
				.toTry()
				.map(entity-> PepInformationService.setPepInformation(entity,updatePepRequest))
				.flatMap(clientsV3Repository::save)
				.peek(entity -> log.info(String.format("PEP information updated idClient: %s", entity.getIdClient())))
				.map(client -> new UpdatePepResponse(client.getIdClient()))
				.onFailure(ex -> log.error(String.format("PEP information update failed. idClient: %s", updatePepRequest.getIdClient()), ex))
				.toEither(() -> new PepError("PEP information update failed"));
	}
	
}
