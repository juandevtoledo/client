package com.lulobank.clients.services.application.port.out.clientsdata;

import com.lulobank.clients.v3.error.ClientsDataError;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import io.vavr.control.Either;

public interface ClientsDataRepositoryPort {

    Either<ClientsDataError, ClientsV3Entity> findByEmailAddress(String emailAddress);

    Either<ClientsDataError, ClientsV3Entity> findByIdClient(String idClient);

    Either<ClientsDataError, ClientsV3Entity> save(ClientsV3Entity clientsV3Entity);
}
