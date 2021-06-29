package com.lulobank.clients.services.features.profile;

import com.lulobank.clients.sdk.operations.dto.UpdateClientAddressRequest;
import com.lulobank.clients.services.events.UpdateClientAddressEvent;
import com.lulobank.clients.services.features.profile.mapper.UpdateClientAddressMapper;
import com.lulobank.core.events.Event;
import com.lulobank.core.events.EventHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UpdateClientAddressEventHandler implements EventHandler<Event<UpdateClientAddressEvent>> {

    private UpdateClientAddressEventUseCase updateClientAddressEventUseCase;

    public UpdateClientAddressEventHandler(UpdateClientAddressEventUseCase updateClientAddressEventUseCase) {
        this.updateClientAddressEventUseCase = updateClientAddressEventUseCase;
    }

    @Override
    public void apply(Event<UpdateClientAddressEvent> updateClientAddressEventEvent) {
        UpdateClientAddressRequest event = UpdateClientAddressMapper.INSTANCE.toUpdateClientProfileRequest(
                (updateClientAddressEventEvent.getPayload()));
        event.setSendNotification(false);
        updateClientAddressEventUseCase.execute(event);
    }

}
