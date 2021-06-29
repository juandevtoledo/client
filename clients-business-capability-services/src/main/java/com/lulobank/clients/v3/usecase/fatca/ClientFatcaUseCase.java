package com.lulobank.clients.v3.usecase.fatca;

import com.lulobank.clients.services.application.port.in.UseCase;
import com.lulobank.clients.v3.error.ClientsDataError;
import com.lulobank.clients.services.domain.error.UseCaseResponseError;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.clients.v3.service.fatca.FatcaInformationService;
import com.lulobank.clients.v3.usecase.command.ClientFatcaInformation;
import com.lulobank.clients.v3.usecase.command.ClientFatcaResponse;
import io.vavr.control.Either;
import lombok.CustomLog;

import java.util.function.Function;

import static com.lulobank.clients.v3.error.ClientsDataError.*;

@CustomLog
public class ClientFatcaUseCase implements UseCase<ClientFatcaInformation, Either<UseCaseResponseError, ClientFatcaResponse>> {

    private final ClientsV3Repository clientsV3Repository;

    public ClientFatcaUseCase(ClientsV3Repository clientsV3Repository) {
        this.clientsV3Repository = clientsV3Repository;
    }

    @Override
    public Either<UseCaseResponseError, ClientFatcaResponse> execute(ClientFatcaInformation command) {
        return clientsV3Repository.findByIdClient(command.getIdClient())
                .toEither(clientNotFound())
                .map(entity -> FatcaInformationService.buildFatcaInformation(entity, command))
                .flatMap(this::saveEntity)
                .peek(entity -> log.info(String.format("Fatca information saved successful, idClient %s",entity.getIdClient())))
                .map(entity -> new ClientFatcaResponse(true))
                .mapLeft(Function.identity());
    }

    private Either<ClientsDataError, ClientsV3Entity> saveEntity(ClientsV3Entity entity) {
        return clientsV3Repository.save(entity).toEither(connectionFailure());
    }

}
