package com.lulobank.clients.v3.usecase.validations;

import com.lulobank.clients.services.application.port.in.UseCase;
import com.lulobank.clients.services.domain.error.UseCaseResponseError;
import com.lulobank.clients.services.ports.out.CustomerService;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.error.ClientsDataError;
import com.lulobank.clients.v3.usecase.command.ValidateEmailIsUnique;
import io.vavr.control.Either;

import java.util.Map;

public class EmailValidationsUseCase implements UseCase<ValidateEmailIsUnique, Either<UseCaseResponseError, Boolean>> {

    private final ClientsV3Repository clientsV3Repository;
    private final CustomerService customerService;

    public EmailValidationsUseCase(ClientsV3Repository clientsV3Repository, CustomerService customerService) {
        this.clientsV3Repository = clientsV3Repository;
        this.customerService = customerService;
    }

    @Override
    public Either<UseCaseResponseError, Boolean> execute(ValidateEmailIsUnique command) {
        return isEmailExistInBD(command.getEmail())
                .flatMap(r ->isEmailExistInCustomerService(command.getCredentials().getHeaders(),command.getEmail()));
    }

    private Either<UseCaseResponseError, Boolean> isEmailExistInCustomerService(Map<String,String> header, String email){
        return customerService.isEmailExist(header,email)
                .flatMap(this::createResponse);
    }

    private Either<UseCaseResponseError, Boolean> createResponse(boolean response) {
        return response ? Either.left(ClientsDataError.emailIsNotUniqueInCustomerService()) : Either.right(false);
    }

    private Either<UseCaseResponseError, Boolean> isEmailExistInBD(String email) {
        return clientsV3Repository.findByEmailAddress(email)
                .fold(()-> Either.right(false),o-> Either.left(ClientsDataError.emailIsNotUnique()));
    }
}
