package com.lulobank.clients.v3.usecase.fatca;

import com.lulobank.clients.sdk.operations.util.CheckPoints;
import com.lulobank.clients.services.application.port.in.UseCase;
import com.lulobank.clients.services.domain.error.UseCaseResponseError;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.clients.v3.adapters.port.out.notification.report.CreateReportNotification;
import com.lulobank.clients.v3.error.ClientsDataError;
import com.lulobank.clients.v3.service.fatca.FatcaInformationService;
import com.lulobank.clients.v3.usecase.command.ClientFatcaInformation;
import com.lulobank.clients.v3.usecase.command.ClientFatcaResponse;
import com.lulobank.clients.v3.usecase.fatca.event.FatcaDataReport;
import com.lulobank.clients.v3.util.DocumentTypes;
import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.CustomLog;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

import static com.lulobank.clients.v3.error.ClientsDataError.clientNotFound;
import static com.lulobank.clients.v3.error.ClientsDataError.connectionFailure;

@CustomLog
public class ClientFatcaOnboardingUseCase implements UseCase<ClientFatcaInformation, Either<UseCaseResponseError, ClientFatcaResponse>> {

    private final ClientsV3Repository clientsV3Repository;
    private final CreateReportNotification createReportNotification;

    public ClientFatcaOnboardingUseCase(ClientsV3Repository clientsV3Repository,
                                        CreateReportNotification createReportNotification) {
        this.clientsV3Repository = clientsV3Repository;
        this.createReportNotification = createReportNotification;
    }

    @Override
    public Either<UseCaseResponseError, ClientFatcaResponse> execute(ClientFatcaInformation command) {
        return clientsV3Repository.findByIdClient(command.getIdClient())
                .toEither(clientNotFound())
                .map(entity -> FatcaInformationService.buildFatcaInformation(entity, command))
                .map(this::setNewCheckPoint)
                .flatMap(this::saveEntity)
                .peek(entity -> log.info(String.format("Fatca information saved successful, idClient %s and new checkpoint is %s",
                        entity.getIdClient(),entity.getOnBoardingStatus().getCheckpoint())))
                .peek(v->log.info(String.format("Sending notification for digital evidence, idClient %s",command.getIdClient())))
                .peek(entity-> createReportNotification.sendReport(command.getIdClient(),"FATCA","FatcaReport",buildEvent(entity,command)))
                .map(entity -> new ClientFatcaResponse(true))
                .mapLeft(Function.identity());
    }

    private Either<ClientsDataError, ClientsV3Entity> saveEntity(ClientsV3Entity entity) {
        return clientsV3Repository.save(entity).toEither(connectionFailure());
    }

    private ClientsV3Entity setNewCheckPoint(ClientsV3Entity clientsV3Entity){
        clientsV3Entity.getOnBoardingStatus().setCheckpoint(CheckPoints.FATCA_FINISHED.name());
        return clientsV3Entity;
    }

    private FatcaDataReport buildEvent(ClientsV3Entity entity, ClientFatcaInformation command){
        return FatcaDataReport
                .builder()
                .fistName(entity.getAdditionalPersonalInformation().getFirstName())
                .middleName(entity.getAdditionalPersonalInformation().getSecondName())
                .surname(entity.getAdditionalPersonalInformation().getFirstSurname())
                .secondSurname(entity.getAdditionalPersonalInformation().getSecondSurname())
                .documentType(DocumentTypes.valueOf(entity.getTypeDocument()).getText())
                .idCard(entity.getIdCard())
                .birthDate(entity.getBirthDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .birthPlace(entity.getAdditionalPersonalInformation().getPlaceBirth())
                .address(entity.getAddress())
                .addressComplement(entity.getAddressComplement())
                .city(entity.getCity())
                .phoneNumber(entity.getPhoneNumber())
                .taxLiability(String.valueOf(command.isFatcaResponsibility()))
                .country(command.getCountryName())
                .taxNumber(Option.of(command.getTin()).fold(command::getTinObservation, tin -> tin))
                .reportDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy'T'HH:mm:ss")))
                .build();

    }

}
