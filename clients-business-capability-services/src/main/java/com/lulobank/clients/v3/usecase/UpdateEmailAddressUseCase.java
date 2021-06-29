package com.lulobank.clients.v3.usecase;

import com.lulobank.clients.services.domain.error.UseCaseResponseError;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.error.ClientsDataError;
import com.lulobank.clients.v3.usecase.command.UpdateEmailAddress;
import com.lulobank.clients.v3.util.UseCase;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;

import java.util.function.Function;

public class UpdateEmailAddressUseCase implements UseCase<UpdateEmailAddress, Either<UseCaseResponseError, Boolean>> {

    private final ClientsV3Repository clientsV3Repository;

    public UpdateEmailAddressUseCase(ClientsV3Repository clientsV3Repository) {
        this.clientsV3Repository = clientsV3Repository;
    }

    @Override
    public Either<UseCaseResponseError, Boolean> execute(UpdateEmailAddress command) {
        return clientsV3Repository.findByIdClient(command.getIdClient())
                .toEither(ClientsDataError.clientNotFound())
                .flatMap(clientsV3Entity -> validateUniqueEmail(command))
                .mapLeft(Function.identity());
    }

    public Either<ClientsDataError, Boolean> validateUniqueEmail(UpdateEmailAddress command) {
        return Try.of(() -> clientsV3Repository.findByEmailAddress(command.getNewEmail()))
                .toEither(ClientsDataError::internalServerError)
                .filter(Option::isEmpty)
                .fold(() -> Either.left(ClientsDataError.emailIsNotUnique()),
                        notExits -> updateEmail(command));
    }

    public Either<ClientsDataError, Boolean> updateEmail(UpdateEmailAddress command) {
        return clientsV3Repository
                .updateEmailByIdClient(command.getIdClient(), command.getNewEmail())
                .fold(error -> Either.left(ClientsDataError.internalServerError()),
                        response -> Either.right(Boolean.TRUE));
    }

}
