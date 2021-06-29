package com.lulobank.clients.services.features.clientverificationresult;

import com.lulobank.clients.services.application.port.out.reporting.TransactionsMessagingPort;
import com.lulobank.clients.services.features.clientverificationresult.mapper.ClientInformationMapper;
import com.lulobank.clients.services.outboundadapters.ClientsOutboundAdapter;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.core.Response;
import com.lulobank.savingsaccounts.sdk.dto.createsavingsaccount.CreateSavingsAccountRequest;
import com.lulobank.savingsaccounts.sdk.dto.createsavingsaccount.CreateSavingsAccountResponse;
import com.lulobank.utils.exception.ServiceException;
import io.vavr.control.Try;
import lombok.CustomLog;
import org.owasp.encoder.Encode;

import java.util.Map;
import java.util.function.Consumer;

import static com.lulobank.clients.sdk.operations.util.CheckPoints.SAVING_ACCOUNT_CREATED;
import static com.lulobank.clients.services.features.clientverificationresult.NotificationHelper.notifySavingAccountCreated;
import static com.lulobank.clients.services.utils.ClientHelper.getHeadersClientCredentials;


@CustomLog
public class ProcessResultBySaving extends ProcessResultByType {

    private ClientsOutboundAdapter clientsOutboundAdapter;
    private ClientsV3Entity clientEntity;
    private ClientsV3Repository clientsV3Repository;
    private TransactionsMessagingPort transactionsMessagingService;

    public ProcessResultBySaving(ClientsOutboundAdapter clientsOutboundAdapter, ClientsV3Entity clientEntity, ClientsV3Repository clientsV3Repository, TransactionsMessagingPort transactionsMessagingService) {
        this.clientsOutboundAdapter = clientsOutboundAdapter;
        this.clientEntity = clientEntity;
        this.clientsV3Repository = clientsV3Repository;
        this.transactionsMessagingService = transactionsMessagingService;
    }

    @Override
    public void process() {
        Try.of(this::createSavingAccount)
                .andThen(notifySavingAccountCreated(clientsOutboundAdapter))
                .map(clientsV3Entity -> {
                    clientEntity.getOnBoardingStatus().setCheckpoint(SAVING_ACCOUNT_CREATED.name());
                    return clientsV3Entity;
                })
                .andThen(update)
                .andThen(transactionsMessagingService::checkReferralHold)
                .onSuccess(success -> log.info("Onboarding SavingsAccount was update by idClient : {}", clientEntity.getIdClient()))
                .onFailure(ServiceException.class, handlerServiceExceptionError());
    }

    public ClientsV3Entity createSavingAccount() {
        Map<String, String> headers = getHeadersClientCredentials(clientsOutboundAdapter, clientEntity.getIdClient());
        CreateSavingsAccountRequest savingAccountRequest = getSavingsAccountRequest(clientEntity);
        Response<CreateSavingsAccountResponse> response =
                clientsOutboundAdapter
                        .getSavingsAccount()
                        .createSavingsAccount(headers, savingAccountRequest);
        clientEntity.setIdCbs(response.getContent().getIdCbs());
        return clientEntity;
    }

    private Consumer<ClientsV3Entity> update = clientEntity -> {
        clientsV3Repository.updateOnBoarding(clientEntity);
    };

    private CreateSavingsAccountRequest getSavingsAccountRequest(ClientsV3Entity clientEntity) {
        CreateSavingsAccountRequest savingAccountRequest = new CreateSavingsAccountRequest();
        savingAccountRequest.setIdClient(clientEntity.getIdClient());
        savingAccountRequest.setClientInformation(ClientInformationMapper.INSTANCE.clientInformationFrom(clientEntity));
        return savingAccountRequest;
    }

    private Consumer<ServiceException> handlerServiceExceptionError() {
        return ex -> {
            log.error("Error Creating Saving Account , serviceMessage: {}, serviceCode: {} IdTransactionBiometric: {}, idClientId: {}",
                    ex.getMessage(), ex.getCode(), Encode.forJava(clientEntity.getIdentityBiometric().getIdTransaction()), Encode.forJava(clientEntity.getIdClient()));
        };
    }


}
