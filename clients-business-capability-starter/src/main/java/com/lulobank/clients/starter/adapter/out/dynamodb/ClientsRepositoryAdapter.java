package com.lulobank.clients.starter.adapter.out.dynamodb;

import com.lulobank.clients.services.application.port.out.clientsdata.ClientsDataRepositoryPort;
import com.lulobank.clients.v3.error.ClientsDataError;
import com.lulobank.clients.starter.adapter.out.dynamodb.mapper.ClientsEntityMapper;
import com.lulobank.clients.starter.v3.adapters.out.dynamo.ClientsDataRepository;
import com.lulobank.clients.starter.v3.adapters.out.dynamo.dto.ClientEntity;
import com.lulobank.clients.starter.v3.mapper.ClientsEntityV3Mapper;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.tracing.DatabaseBrave;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Function;

import static com.lulobank.clients.v3.error.ClientsDataErrorStatus.CLI_101;

@Slf4j
@RequiredArgsConstructor
public class ClientsRepositoryAdapter implements ClientsDataRepositoryPort {
    private final ClientsDataRepository clientsDataRepository;
    private final DatabaseBrave databaseBrave;


    @Override
    public Either<ClientsDataError, ClientsV3Entity> findByEmailAddress(String emailAddress) {
        return Try.of(() -> getClientByEmailFromDB(emailAddress)
                .map(ClientsEntityMapper.INSTANCE::toClientsV3Entity))
                .fold(handleDynamoDBException(), getFindByIdResponseOk(emailAddress));
    }

    @Override
    public Either<ClientsDataError, ClientsV3Entity> findByIdClient(String idClient) {
          return Try.of(() -> getAccountFromDBByIdClient(idClient)
                  .map(ClientsEntityMapper.INSTANCE::toClientsV3Entity))
                  .fold(t -> Either.left(handleNotFoundException(idClient)), getFindByIdResponseOk(idClient));
    }

    private Option<ClientEntity> getAccountFromDBByIdClient(String idClient) {
        return Option.ofOptional(databaseBrave.queryOptional(() -> clientsDataRepository.findByIdClient(idClient)));
    }

    @Override
    public Either<ClientsDataError, ClientsV3Entity> save(ClientsV3Entity clientsV3Entity) {
        return Try.of(() -> clientsDataRepository.save(ClientsEntityV3Mapper.INSTANCE.toEntity(clientsV3Entity)))
                .map(ClientsEntityMapper.INSTANCE::toClientsV3Entity)
                .toEither()
                .mapLeft( t -> ClientsDataError.connectionFailure());
    }

    private Option<ClientEntity> getClientByEmailFromDB(String emailAddress) {
        return Option.ofOptional(clientsDataRepository.findByEmailAddress(emailAddress));
    }

    private Function<Option<ClientsV3Entity>, Either<ClientsDataError, ClientsV3Entity>> getFindByIdResponseOk(
            String param) {
        return clientEntity -> clientEntity.toEither(() -> handleNotFoundException(param));
    }

    private Function<Throwable, Either<ClientsDataError, ClientsV3Entity>> handleDynamoDBException() {
        return t -> Either.left(ClientsDataError.connectionFailure());
    }

    private ClientsDataError handleNotFoundException(String param) {
        log.error(CLI_101.getMessage(), param);
        return ClientsDataError.clientNotFound();
    }


}
