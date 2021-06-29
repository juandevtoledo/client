package com.lulobank.clients.v3.usecase.productoffers;

import java.util.List;

import com.lulobank.clients.services.domain.error.UseCaseResponseError;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.OfferState;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.RiskOfferV3;
import com.lulobank.clients.v3.error.ClientsDataError;
import com.lulobank.clients.v3.usecase.productoffers.command.ClientProductOffer;
import com.lulobank.clients.v3.usecase.productoffers.command.CreateClientProductOfferRequest;
import com.lulobank.clients.v3.util.UseCase;

import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.CustomLog;

@CustomLog
@AllArgsConstructor
public class CreateClientProductOfferUseCase
		implements UseCase<CreateClientProductOfferRequest, Either<UseCaseResponseError, ClientProductOffer>> {

	private final ClientsV3Repository clientsV3Repository;

	@Override
	public Either<UseCaseResponseError, ClientProductOffer> execute(
			CreateClientProductOfferRequest createClientProductOfferRequest) {
		log.info("[CreateClientProductOfferUseCase] execute");
		return clientsV3Repository.findByIdClient(createClientProductOfferRequest.getIdClient())
				.onEmpty(() -> log.error(String.format("[CreateClientProductOfferUseCase] Client not found. idClient: %s", createClientProductOfferRequest.getIdClient())))
				.toTry()
				.flatMap(clientsV3Entity -> saveRiskOfferV3(clientsV3Entity, createClientProductOfferRequest))
				.map(this::mapRiskOffer)
				.onSuccess(clientsV3Entity -> log.info("[CreateClientProductOfferUseCase] Client product offert was created successful"))
				.onFailure(error -> log.error(String.format("[CreateClientProductOfferUseCase] Error saving client product offer: %s", error)))
				.toEither(ClientsDataError.clientNotFound());
	}
	
	private ClientProductOffer mapRiskOffer(RiskOfferV3 riskOfferV3) {
        return ClientProductOffer.builder()
				.idProductOffer(riskOfferV3.getIdProductOffer())
				.state(riskOfferV3.getState().name())
				.type(riskOfferV3.getType())
				.value(riskOfferV3.getValue())
				.build();
	}

	private Try<RiskOfferV3> saveRiskOfferV3(ClientsV3Entity clientsV3Entity,
			CreateClientProductOfferRequest createClientProductOfferRequest) {
		RiskOfferV3 newRiskOfferV3 = buildRiskOfferV3(createClientProductOfferRequest);
		return Try.of(() -> getClientsV3EntityWithNewRiskOfferV3(clientsV3Entity, newRiskOfferV3))
				.flatMap(newClientsV3Entity -> clientsV3Repository.save(newClientsV3Entity))
				.map(entity -> newRiskOfferV3);
	}
	
	private ClientsV3Entity getClientsV3EntityWithNewRiskOfferV3(ClientsV3Entity clientsV3Entity, RiskOfferV3 newRiskOfferV3) {
		closeSameTypeIfActive(clientsV3Entity, newRiskOfferV3.getType()).add(newRiskOfferV3);
		return clientsV3Entity;
	}
	
	private List<RiskOfferV3> closeSameTypeIfActive(ClientsV3Entity clientsV3Entity, String type) {
		clientsV3Entity.getApprovedRiskAnalysis().getResults().stream()
			.filter(result -> type.equalsIgnoreCase(result.getType()) && OfferState.ACTIVE_STATES.contains(result.getState()))
			.forEach(result -> result.setState(OfferState.EXPIRED));
		
		return clientsV3Entity.getApprovedRiskAnalysis().getResults();
	}

	private RiskOfferV3 buildRiskOfferV3(CreateClientProductOfferRequest createClientProductOfferRequest) {
		return RiskOfferV3.builder()
				.idProductOffer(java.util.UUID.randomUUID().toString())
				.state(OfferState.ACTIVE)
				.type(createClientProductOfferRequest.getType())
				.value(createClientProductOfferRequest.getValue())
				.offerDate(java.time.LocalDateTime.now())
				.build();
	}
}
