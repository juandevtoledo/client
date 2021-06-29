package com.lulobank.clients.v3.usecase.phone;

import com.lulobank.clients.services.application.port.in.UseCase;
import com.lulobank.clients.services.domain.error.UseCaseResponseError;
import com.lulobank.clients.services.ports.repository.ClientsRepositoryV2;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.error.ClientsDataError;
import com.lulobank.clients.v3.usecase.command.UpdatePhoneNumber;
import io.vavr.control.Either;

public class UpdatePhoneNumberUseCase implements UseCase<UpdatePhoneNumber, Either<UseCaseResponseError, Boolean>> {

    private final ClientsV3Repository clientsV3Repository;
    private final ClientsRepositoryV2 clientsRepositoryV2;

    public UpdatePhoneNumberUseCase(ClientsV3Repository clientsV3Repository, ClientsRepositoryV2 clientsRepositoryV2) {
        this.clientsV3Repository = clientsV3Repository;
        this.clientsRepositoryV2 = clientsRepositoryV2;
    }

    @Override
    public Either<UseCaseResponseError, Boolean> execute(UpdatePhoneNumber command) {
        return clientsRepositoryV2.findByPhonePrefixAndPhoneNumber(command.getCountryCode(), command.getNewPhoneNumber())
                .fold(() -> clientsV3Repository.updatePhoneNumber(command.getIdClient(), command.getNewPhoneNumber(), command.getCountryCode()),
                        e -> Either.left(ClientsDataError.phoneIsNotUniqueInCustomerService()));

    }
}
