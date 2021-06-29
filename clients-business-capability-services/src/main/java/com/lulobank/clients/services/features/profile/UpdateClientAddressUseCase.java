package com.lulobank.clients.services.features.profile;

import com.lulobank.clients.sdk.operations.dto.UpdateClientAddressRequest;
import com.lulobank.clients.services.application.port.in.UseCase;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UpdateClientAddressUseCase
        implements UseCase<UpdateClientAddressRequest, Try<Boolean>> {

    private final UpdateClientAddressService updateClientAddressService;

    public UpdateClientAddressUseCase(UpdateClientAddressService updateClientAddressService) {
        this.updateClientAddressService = updateClientAddressService;
    }

    @Override
    public Try<Boolean> execute(UpdateClientAddressRequest command) {
        return updateClientAddressService.updateAddress(command)
                .onFailure(e -> log.error("Error updating client address. Error {}", e.getMessage(), e))
                .onSuccess(r -> log.info("Address updated for client {}", command.getIdClient()));
    }

}
