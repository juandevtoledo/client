package com.lulobank.clients.v3.usecase.createaddress;

import com.lulobank.clients.services.application.port.out.clientsdata.ClientsDataRepositoryPort;
import com.lulobank.clients.services.domain.error.UseCaseResponseError;
import com.lulobank.clients.services.utils.HttpCodes;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.clients.v3.usecase.command.ClientAddressData;
import com.lulobank.clients.v3.util.UseCase;
import io.vavr.API;
import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import lombok.CustomLog;

import java.util.function.Function;

import static com.lulobank.clients.sdk.operations.util.CheckPoints.CLIENT_ADDRESS_FINISHED;
import static com.lulobank.clients.v3.error.ClientsDataErrorStatus.CLI_106;
import static io.vavr.API.$;
import static io.vavr.API.Case;

@CustomLog
@AllArgsConstructor
public class ClientCreateAddressUseCase implements UseCase<ClientAddressData, Either<UseCaseResponseError,ClientAddressData>> {

    private final ClientsDataRepositoryPort clientsDataRepository;

    @Override
    public Either<UseCaseResponseError, ClientAddressData> execute(ClientAddressData command) {
        return clientsDataRepository.findByIdClient(command.getIdClient())
                .mapLeft(clientsDataError -> (UseCaseResponseError) clientsDataError)
                .flatMap(validateClientAddress(command));
    }

    private Function<ClientsV3Entity,Either<UseCaseResponseError,ClientAddressData>> validateClientAddress(ClientAddressData command) {
        return clientsV3Entity -> API.Match(isAddressAlreadyDefined(clientsV3Entity)).of(
                Case($(true),  () -> Either.left(new UseCaseResponseError(CLI_106.name(), HttpCodes.CONFLICT,CLI_106.getMessage())) ),
                Case($(false),  () -> createClientAddress(clientsV3Entity,command))
        );
    }

    private Either<UseCaseResponseError,ClientAddressData> createClientAddress(ClientsV3Entity clientsV3Entity, ClientAddressData command){
        return clientsDataRepository.save(updateEntity(clientsV3Entity, command))
                .mapLeft(clientsDataError -> (UseCaseResponseError) clientsDataError)
                .peekLeft(useCaseResponseError -> log.error("Error saving the client entity, clientID: {}, detail: {} ",clientsV3Entity.getIdClient(),useCaseResponseError.getDetail()))
                .peek( clientEntity -> log.info("Set Checkpoint: {} for client: {}",clientEntity.getOnBoardingStatus().getCheckpoint(),clientEntity.getIdClient()))
                .map(ClientAddressData::fromClientsV3Entity);

    }

    private ClientsV3Entity updateEntity(ClientsV3Entity clientsV3Entity, ClientAddressData command){
        setClientAddress(clientsV3Entity, command);
        return updateClientCheckpoint(clientsV3Entity);
    }

    private ClientsV3Entity setClientAddress(ClientsV3Entity clientsV3Entity, ClientAddressData command) {
        clientsV3Entity.setAddress(command.getAddress());
        clientsV3Entity.setAddressPrefix(command.getAddressPrefix());
        clientsV3Entity.setAddressComplement(command.getAddressComplement());
        clientsV3Entity.setCity(command.getCity());
        clientsV3Entity.setCityId(command.getCityId());
        clientsV3Entity.setDepartment(command.getDepartment());
        clientsV3Entity.setDepartmentId(command.getDepartmentId());
        clientsV3Entity.setCode(command.getCode());
        return clientsV3Entity;
    }

    private Boolean isAddressAlreadyDefined(ClientsV3Entity clientEntity) {
        return Option.of(clientEntity.getAddress()).isDefined()
                || Option.of(clientEntity.getAddressPrefix()).isDefined();
    }

    private ClientsV3Entity updateClientCheckpoint(ClientsV3Entity clientsV3Entity){
        clientsV3Entity.getOnBoardingStatus().setCheckpoint(CLIENT_ADDRESS_FINISHED.name());
        return clientsV3Entity;
    }
}
