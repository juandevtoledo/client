package com.lulobank.clients.v3.usecase.economicinformation;

import com.lulobank.clients.sdk.operations.dto.economicinformation.ClientEconomicInformation;
import com.lulobank.clients.sdk.operations.util.CheckPoints;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.clients.v3.events.RiskEngineNotificationService;
import com.lulobank.clients.v3.service.economicinformation.EconomicInformationService;
import com.lulobank.clients.v3.usecase.command.UseCaseError;
import com.lulobank.clients.v3.util.UseCase;
import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.CustomLog;

import static com.lulobank.clients.services.utils.ConverterObjectUtils.getEconomicInfoEvent;
import static com.lulobank.clients.services.utils.ConverterObjectUtils.getIdentityInformation;

@CustomLog
public class SaveEconomicInformationInOnboardingUseCase implements UseCase<ClientEconomicInformation, Either<UseCaseError, ClientsV3Entity>> {


    private final ClientsV3Repository clientsRepository;
    private final EconomicInformationService economicInformationService;
    private final RiskEngineNotificationService riskEngineNotificationService;

    public SaveEconomicInformationInOnboardingUseCase(ClientsV3Repository clientsRepository,
                                                      EconomicInformationService economicInformationService,
                                                      RiskEngineNotificationService riskEngineNotificationService) {

        this.clientsRepository = clientsRepository;
        this.economicInformationService = economicInformationService;
        this.riskEngineNotificationService = riskEngineNotificationService;
    }

    @Override
    public Either<UseCaseError, ClientsV3Entity> execute(ClientEconomicInformation command) {
        return economicInformationService.build(command)
                .map(this::setCheckpoint)
                .flatMap(clientsRepository::save)
                .peek(client -> log.info(String.format("Onboarding economic information for %s was saved and new client's checkpoint is %s",
                        client.getIdClient(),
                        client.getOnBoardingStatus().getCheckpoint())))
                .flatMap(client -> sendNotifications(client, command))
                .onFailure(ex -> log.error(String.format("Error saving economic information. ClientId: %s", command.getIdClient()), ex))
                .toEither(() -> new UseCaseError(String.format("Update client biometric failed. IdClient %s ",
                        command.getIdClient())));
    }

    private Try<ClientsV3Entity> sendNotifications(ClientsV3Entity clientsV3Entity,ClientEconomicInformation command){
        return sendNotificationEconomicInformation(command)
                .flatMap(v->sendNotificationIdentityInformation(clientsV3Entity,command))
                .map(v-> clientsV3Entity);

    }

    private Try<Void> sendNotificationEconomicInformation(ClientEconomicInformation command) {
        return riskEngineNotificationService.setEconomicInformation(getEconomicInfoEvent(command),
                command.getAdapterCredentials().getAuthorizationHeadersToSqs(), command.getIdClient());
    }

    private Try<Void> sendNotificationIdentityInformation(ClientsV3Entity clientsV3Entity,ClientEconomicInformation command) {
        return riskEngineNotificationService.setIdentityInformation(getIdentityInformation(clientsV3Entity),
                command.getAdapterCredentials().getAuthorizationHeadersToSqs(),command.getIdClient());
    }

    private ClientsV3Entity setCheckpoint(ClientsV3Entity clientEntity) {
        clientEntity.getOnBoardingStatus().setCheckpoint(CheckPoints.ECONOMIC_INFO_FINISHED.name());
        return clientEntity;
    }

}
