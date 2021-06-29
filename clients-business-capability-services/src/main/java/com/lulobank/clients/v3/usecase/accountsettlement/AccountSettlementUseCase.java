package com.lulobank.clients.v3.usecase.accountsettlement;

import com.lulobank.clients.sdk.operations.dto.onboardingclients.AccountSettlement;
import com.lulobank.clients.services.application.port.out.reporting.ReportingMessagingPort;
import com.lulobank.clients.services.application.port.out.reporting.TransactionsMessagingPort;
import com.lulobank.clients.services.domain.error.UseCaseResponseError;
import com.lulobank.clients.services.outboundadapters.ClientsOutboundAdapter;
import com.lulobank.clients.services.ports.out.CustomerServiceV2;
import com.lulobank.clients.services.ports.out.DigitalEvidenceService;
import com.lulobank.clients.services.ports.out.error.CustomerServiceError;
import com.lulobank.clients.v3.adapters.port.out.digitalevidence.DigitalEvidenceError;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.clients.v3.adapters.port.out.savingsaccount.SavingsAccountV3Service;
import com.lulobank.clients.v3.adapters.port.out.savingsaccount.dto.SavingsAccountRequest;
import com.lulobank.clients.v3.adapters.port.out.savingsaccount.error.SavingsAccountError;
import com.lulobank.clients.v3.error.ClientsDataErrorStatus;
import com.lulobank.clients.v3.usecase.command.BiometricResponse;
import com.lulobank.clients.v3.usecase.mapper.SavingsAccountRequestMapper;
import com.lulobank.clients.v3.util.UseCase;
import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.CustomLog;
import org.owasp.encoder.Encode;

import java.util.function.Consumer;
import java.util.function.Function;

import static com.lulobank.clients.sdk.operations.util.CheckPoints.SAVING_ACCOUNT_CREATED;
import static com.lulobank.clients.services.application.util.HttpDomainStatus.INTERNAL_SERVER_ERROR;
import static com.lulobank.clients.services.features.clientverificationresult.NotificationHelper.notifyCreateCustomerError;
import static com.lulobank.clients.services.features.clientverificationresult.NotificationHelper.notifyCreateSavingAccountError;
import static com.lulobank.clients.services.features.clientverificationresult.NotificationHelper.notifyDigitalEvidenceError;
import static com.lulobank.clients.services.features.clientverificationresult.NotificationHelper.notifySavingAccountCreated;
import static com.lulobank.clients.services.utils.ClientHelper.getHeadersClientCredentials;
import static com.lulobank.clients.services.utils.ProductTypeEnum.SAVING_ACCOUNT;
import static com.lulobank.clients.v3.adapters.port.out.savingsaccount.error.SavingsAccountError.clientDoNotHaveProduct;
import static com.lulobank.clients.v3.error.ClientsDataError.clientNotFound;
import static com.lulobank.clients.v3.error.ClientsDataError.internalError;
import static com.lulobank.clients.v3.error.ClientsDataErrorStatus.CLI_107;
import static com.lulobank.clients.v3.util.DigitalEvidenceTypes.SAVINGS_ACCOUNT;

@CustomLog
public class AccountSettlementUseCase
        implements UseCase<AccountSettlement, Either<UseCaseResponseError, BiometricResponse>> {

    private ClientsOutboundAdapter clientsOutboundAdapter;
    private CustomerServiceV2 customerServiceAdapterV2;
    private ClientsV3Repository clientsV3Repository;
    private TransactionsMessagingPort transactionsMessagingService;
    private SavingsAccountV3Service savingsAccountV3Service;
    private DigitalEvidenceService digitalEvidenceService;
    private final ReportingMessagingPort reportingMessagingPort;

    private static final String ERROR_CREATING_CUSTOMER = "Error creating customer for idClient {}. Error {}.";
    private static final String ERROR_CREATING_SAVING_ACCOUNT = "Error creating saving account for idClient {}. Error {}.";
    private static final String ERROR_CREATING_DIGITAL_EVIDENCE = "Error creating digital evidence for idClient {}. Error {}.";

    public AccountSettlementUseCase(ClientsOutboundAdapter clientsOutboundAdapter,
                                    CustomerServiceV2 customerServiceAdapterV2,
                                    ClientsV3Repository clientsV3Repository,
                                    TransactionsMessagingPort transactionsMessagingService,
                                    SavingsAccountV3Service savingsAccountV3Service,
                                    DigitalEvidenceService digitalEvidenceService,
                                    ReportingMessagingPort reportingMessagingPort
                                    ) {
        this.clientsOutboundAdapter = clientsOutboundAdapter;
        this.customerServiceAdapterV2 = customerServiceAdapterV2;
        this.clientsV3Repository = clientsV3Repository;
        this.transactionsMessagingService = transactionsMessagingService;
        this.savingsAccountV3Service = savingsAccountV3Service;
        this.digitalEvidenceService = digitalEvidenceService;
        this.reportingMessagingPort = reportingMessagingPort;
    }

    @Override
    public Either<UseCaseResponseError, BiometricResponse> execute(AccountSettlement command) {
        return clientsV3Repository.findByIdClient(command.getIdClient())
                .toEither((UseCaseResponseError) clientNotFound())
                .flatMap(this::processSettlement)
                .map(clientsV3Entity -> new BiometricResponse(clientsV3Entity.getIdentityBiometricId()));
    }

    private Either<UseCaseResponseError,ClientsV3Entity> processSettlement(ClientsV3Entity clientEntity) {
        return createCustomer(clientEntity)
                .flatMap(this::createSavingAccount)
                .flatMap(checkReferralHold())
                .flatMap(this::createDigitalEvidence)
                .peek(this::sendDigitalEvidenceDocument)
                .map(updateCheckpoint())
                .map(this::updateFirebase)
                .map(client -> clientsV3Repository.save(client))
                .flatMap( t -> t.toEither(internalError()));
    }

    private Either<UseCaseResponseError,ClientsV3Entity> createCustomer(ClientsV3Entity client) {
        return customerServiceAdapterV2
                .createUserCustomer(getHeadersClientCredentials(clientsOutboundAdapter, client.getIdClient()),client)
                .peekLeft(error -> handleExceptionCustomerService(error, client))
                .mapLeft(error -> (UseCaseResponseError) error)
                .map( response -> setCustomerCreatedStatus(client,response.getCreated()));
    }

    private ClientsV3Entity setCustomerCreatedStatus(ClientsV3Entity client, Boolean created){
        client.setCustomerCreatedStatus(created);
        return client;
    }

    private Either<UseCaseResponseError,ClientsV3Entity> createSavingAccount(ClientsV3Entity clientsV3Entity) {
        return validateClientProduct(clientsV3Entity)
                .flatMap(callSavingAccountCreate());
    }

    private Function<ClientsV3Entity,Either<UseCaseResponseError,ClientsV3Entity>> callSavingAccountCreate(){
        return  client -> savingsAccountV3Service
                .create(getSavingsAccountRequest(client)
                        ,getHeadersClientCredentials(clientsOutboundAdapter, client.getIdClient()))
                .peekLeft( error -> handleExceptionSavingAccount(error,client))
                .mapLeft(error -> (UseCaseResponseError) error)
                .map( savingsAccountResponse -> setIdCbs(client,savingsAccountResponse.getIdCbs()));
    }
    private ClientsV3Entity setIdCbs(ClientsV3Entity clientsV3Entity, String idCbs){
        clientsV3Entity.setIdCbs(idCbs);
        return clientsV3Entity;
    }

    private Either<UseCaseResponseError,ClientsV3Entity> validateClientProduct(ClientsV3Entity clientsV3Entity){
        return Option.of(clientsV3Entity)
                .filter(client -> client.getOnBoardingStatus().getProductSelected().equals(SAVING_ACCOUNT.name()))
                .toEither(clientDoNotHaveProduct())
                .peekLeft( error -> handleExceptionSavingAccount( error,clientsV3Entity))
                .mapLeft(error -> (UseCaseResponseError) error);
    }

    private SavingsAccountRequest getSavingsAccountRequest (ClientsV3Entity clientsV3Entity){
        return SavingsAccountRequestMapper.INSTANCE.toSavingsAccountRequest(clientsV3Entity);
    }

    private Either<UseCaseResponseError,ClientsV3Entity> createDigitalEvidence(ClientsV3Entity client) {
        return digitalEvidenceService.saveDigitalEvidence(getHeadersClientCredentials(clientsOutboundAdapter, client.getIdClient()), client, SAVINGS_ACCOUNT)
                .onFailure(error -> handleExceptionDigitalEvidence(DigitalEvidenceError.unknownError(), client))
                .toEither(() -> new UseCaseResponseError(CLI_107.name(), INTERNAL_SERVER_ERROR, ClientsDataErrorStatus.DEFAULT_DETAIL))
                .map(value -> setDigitalStorageStatus(client, value));
    }

    private ClientsV3Entity setDigitalStorageStatus(ClientsV3Entity client,Boolean digitalStorageStatus){
        client.setCatsDocumentStatus(digitalStorageStatus);
        return client;
    }

    private void handleExceptionDigitalEvidence(UseCaseResponseError error, ClientsV3Entity client) {
        handleException(error,client,notifyDigitalEvidenceError(clientsOutboundAdapter),ERROR_CREATING_DIGITAL_EVIDENCE);
    }


    private Function<ClientsV3Entity,ClientsV3Entity> updateCheckpoint() {
        return clientsV3Entity -> {
            clientsV3Entity.getOnBoardingStatus().setCheckpoint(SAVING_ACCOUNT_CREATED.name());
            return clientsV3Entity;
        };
    }

    private Function<ClientsV3Entity,Either<UseCaseResponseError,ClientsV3Entity>> checkReferralHold(){
        return clientsV3Entity ->
            transactionsMessagingService.checkReferralHold(clientsV3Entity)
                    .toEither()
                    .mapLeft( error -> SavingsAccountError.checkReferralHoldError())
                    .peekLeft( error -> handleExceptionSavingAccount(error,clientsV3Entity))
                    .mapLeft( error -> (UseCaseResponseError) error)
                    .map( __ -> clientsV3Entity);
    }

    private void sendDigitalEvidenceDocument(ClientsV3Entity clientEntity) {
        Option.of(clientEntity)
                .peek(clientsV3Entity -> reportingMessagingPort.sendCatDocument(clientEntity)
                        .onFailure(t -> log.error("Error sending digital evidence document.")));
    }

    private void handleExceptionCustomerService(CustomerServiceError error, ClientsV3Entity client) {
        handleException(error,client,notifyCreateCustomerError(clientsOutboundAdapter),ERROR_CREATING_CUSTOMER);
    }

    private void handleExceptionSavingAccount(SavingsAccountError error, ClientsV3Entity client) {
        handleException(error,client,notifyCreateSavingAccountError(clientsOutboundAdapter),ERROR_CREATING_SAVING_ACCOUNT);
    }

    private void handleException(UseCaseResponseError error, ClientsV3Entity client, Consumer<ClientsV3Entity> consumer, String logMsg){
        Option.of(client)
                .peek(consumer)
                .peek(clientEntity -> log.error(logMsg,Encode.forJava(client.getIdClient()), error.getDetail()));
    }

    private ClientsV3Entity updateFirebase(ClientsV3Entity clientsV3Entity) {
        return Option.of(clientsV3Entity)
                .peek(notifySavingAccountCreated(clientsOutboundAdapter))
                .peek(client -> log.info("Onboarding SavingsAccount was update by idClient : {}", client.getIdClient()))
                .fold( () -> clientsV3Entity, Function.identity());
    }
}
