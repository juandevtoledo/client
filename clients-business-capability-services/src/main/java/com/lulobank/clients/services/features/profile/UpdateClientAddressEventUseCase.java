package com.lulobank.clients.services.features.profile;

import com.lulobank.clients.sdk.operations.dto.UpdateClientAddressRequest;
import com.lulobank.clients.services.application.port.in.UseCase;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UpdateClientAddressEventUseCase
        implements UseCase<UpdateClientAddressRequest, Try<Boolean>> {

    private final UpdateClientAddressService updateClientAddressService;
    private final ClientsV3Repository clientsRepository;

    public UpdateClientAddressEventUseCase(UpdateClientAddressService updateClientAddressService,
                                           ClientsV3Repository clientsRepository) {

        this.updateClientAddressService = updateClientAddressService;
        this.clientsRepository = clientsRepository;
    }

    @Override
    public Try<Boolean> execute(UpdateClientAddressRequest event) {
        return updateClientAddressService.updateAddress(event)
                .onFailure(e -> log.error("Error updating client address. Error {}", e.getMessage(), e))
                .onSuccess(r -> log.info("Address updated for client {}", event.getIdClient()))
                .andThen(() -> registerOnBoardingStatus(event));

    }

    private void registerOnBoardingStatus(UpdateClientAddressRequest event) {
        clientsRepository.findByIdClient(event.getIdClient())
                .map(entity -> updateCheckpoint(entity, event.getCheckpoint()))
                .peek(clientsRepository::save)
                .onEmpty(() -> log.error("Error updating OnBoarding Status. Client not found"));
    }

    private ClientsV3Entity updateCheckpoint(ClientsV3Entity entity, String checkpoint) {
        entity.getOnBoardingStatus().setCheckpoint(checkpoint);
        return entity;
    }

}
