package com.lulobank.clients.v3.usecase;

import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.usecase.command.ClientDemographicError;
import com.lulobank.clients.v3.usecase.command.ClientDemographicInfo;
import com.lulobank.clients.v3.usecase.mapper.ClientsDemographicMapper;
import com.lulobank.clients.v3.util.UseCase;

import io.vavr.control.Either;

public class ClientsDemographicUseCase implements UseCase<String, Either<ClientDemographicError, ClientDemographicInfo>> {

    private final ClientsV3Repository clientsV3Repository;

    public ClientsDemographicUseCase(ClientsV3Repository clientsV3Repository) {
        this.clientsV3Repository = clientsV3Repository;
    }

    @Override
    public Either<ClientDemographicError, ClientDemographicInfo> execute(String idClient) {

        return clientsV3Repository.findByIdClient(idClient)
                .toEither(() -> new ClientDemographicError(String.format("Client %s not found", idClient)))
                .map(ClientsDemographicMapper.INSTANCE::clientEntityToDemographicInfo);
    }
}
