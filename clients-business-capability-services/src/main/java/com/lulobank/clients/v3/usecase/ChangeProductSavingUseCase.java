package com.lulobank.clients.v3.usecase;

import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.clients.v3.adapters.port.out.savingsaccount.SavingsAccountV3Service;
import com.lulobank.clients.v3.adapters.port.out.savingsaccount.dto.SavingsAccountRequest;
import com.lulobank.clients.v3.adapters.port.out.savingsaccount.dto.SavingsAccountResponse;
import com.lulobank.clients.v3.adapters.port.out.savingsaccount.error.SavingsAccountError;
import com.lulobank.clients.v3.usecase.command.ChangeProductResponseError;
import com.lulobank.clients.v3.usecase.command.ChangeProductSaving;
import com.lulobank.clients.v3.usecase.mapper.SavingsAccountRequestMapper;
import com.lulobank.clients.v3.util.UseCase;
import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.extern.slf4j.Slf4j;

import static com.lulobank.clients.sdk.operations.util.CheckPoints.SAVING_ACCOUNT_CREATED;
import static com.lulobank.clients.services.utils.ProductTypeEnum.SAVING_ACCOUNT;

@Slf4j
public class ChangeProductSavingUseCase implements UseCase<ChangeProductSaving, Either<ChangeProductResponseError, Boolean>> {

    private final ClientsV3Repository clientsV3Repository;
    private final SavingsAccountV3Service savingsAccountV3Service;

    public ChangeProductSavingUseCase(ClientsV3Repository clientsV3Repository, SavingsAccountV3Service savingsAccountV3Service) {
        this.clientsV3Repository = clientsV3Repository;
        this.savingsAccountV3Service = savingsAccountV3Service;
    }

    @Override
    public Either<ChangeProductResponseError, Boolean> execute(ChangeProductSaving command) {
        return clientsV3Repository.findByIdClient(command.getIdClient())
                .toTry()
                .map(clientsV3Entity -> createSavingAccount(command, clientsV3Entity))
                .onFailure(error -> log.error("Error trying to Change Product to Saving , idClient :{}, msg : {} ", command.getIdClient(), error.getMessage(), error))
                .get();
    }


    private Either<ChangeProductResponseError, Boolean> createSavingAccount(ChangeProductSaving command, ClientsV3Entity clientsV3Entity) {
        SavingsAccountRequest savingsAccountRequest=SavingsAccountRequestMapper.INSTANCE.toSavingsAccountRequest(clientsV3Entity);
        return savingsAccountV3Service.create(savingsAccountRequest, command.getCredentials().getHeaders())
                .peekLeft(error -> log.error("Error in retrofit saving account , idClient: {}, msg : {}, code: {}, detail :{}", command.getIdClient(), error.getBusinessCode(), error.getProviderCode(), error.getDetail()))
                .fold(this::errorCreateSaving, savingsAccountResponse -> response(clientsV3Entity, savingsAccountResponse));
    }


    private Either<ChangeProductResponseError, Boolean> errorCreateSaving(SavingsAccountError error) {
        return Either.left(new ChangeProductResponseError(error.getBusinessCode(), error.getDetail()));
    }

    private Either<ChangeProductResponseError, Boolean> response(ClientsV3Entity clientsV3Entity, SavingsAccountResponse savingsAccountResponse) {
        return Option.of(savingsAccountResponse)
                .map(response -> {
                    clientsV3Entity.setIdCbs(savingsAccountResponse.getIdCbs());
                    return clientsV3Entity;
                }).map(this::setCheckPointSavings)
                .peek(clientsV3Repository::save)
                .peek(entity -> log.info("Client Update to Saving Product , idClient {}, idSavings {} ", entity.getIdClient(), entity.getIdCbs()))
                .map(entity -> true)
                .map(Either::<ChangeProductResponseError, Boolean>right)
                .get();
    }

    private ClientsV3Entity setCheckPointSavings(ClientsV3Entity clientsV3Entity) {
        clientsV3Entity.getOnBoardingStatus().setCheckpoint(SAVING_ACCOUNT_CREATED.name());
        clientsV3Entity.getOnBoardingStatus().setProductSelected(SAVING_ACCOUNT.name());
        return clientsV3Entity;
    }
}
