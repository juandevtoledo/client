package com.lulobank.clients.v3.usecase.updatecheckpoint;

import com.lulobank.clients.sdk.operations.util.CheckPoints;
import com.lulobank.clients.services.application.port.out.clientsdata.ClientsDataRepositoryPort;
import com.lulobank.clients.services.domain.error.UseCaseResponseError;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.clients.v3.usecase.command.UpdateCheckpointInfo;
import com.lulobank.clients.v3.util.UseCase;
import io.vavr.control.Either;
import lombok.AllArgsConstructor;
import lombok.CustomLog;

@CustomLog
@AllArgsConstructor
public class UpdateCheckpointUseCase implements UseCase<UpdateCheckpointInfo, Either<UseCaseResponseError, UpdateCheckpointInfo>> {

    private final ClientsDataRepositoryPort clientsDataRepository;

    @Override
    public Either<UseCaseResponseError, UpdateCheckpointInfo> execute(UpdateCheckpointInfo command) {
        return clientsDataRepository.findByIdClient(command.getClientId())
                .mapLeft(clientsDataError -> (UseCaseResponseError) clientsDataError)
                .flatMap( clientsV3Entity -> updateClientEntity(setClientCheckpoint(clientsV3Entity,command.getCheckpoint())))
                .peekLeft( __ -> log.error("Error updating the Checkpoint: {} for client: {}",command.getCheckpoint(),command.getClientId()));
    }

    private ClientsV3Entity setClientCheckpoint(ClientsV3Entity clientsV3Entity,CheckPoints checkPoint) {
        clientsV3Entity.getOnBoardingStatus().setCheckpoint(checkPoint.name());
        return clientsV3Entity;
    }

    private Either<UseCaseResponseError, UpdateCheckpointInfo> updateClientEntity(ClientsV3Entity clientsV3Entity) {
        return clientsDataRepository.save(clientsV3Entity)
                .mapLeft( clientsDataError -> (UseCaseResponseError) clientsDataError)
                .peek( clientEntity -> log.info("Checkpoint updated to: {} , for client: {}",
                        clientEntity.getOnBoardingStatus().getCheckpoint(),clientEntity.getIdClient()))
                .map( clientEntity -> buildUpdateCheckpointInfo(clientEntity.getIdClient(),
                        CheckPoints.valueOf(clientEntity.getOnBoardingStatus().getCheckpoint())));

    }

    private UpdateCheckpointInfo buildUpdateCheckpointInfo(String clientId, CheckPoints checkpoint){
        return UpdateCheckpointInfo.builder()
                .checkpoint(checkpoint)
                .clientId(clientId)
                .build();
    }

}
