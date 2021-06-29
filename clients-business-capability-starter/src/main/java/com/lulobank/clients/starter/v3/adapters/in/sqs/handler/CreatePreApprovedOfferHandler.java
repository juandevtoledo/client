package com.lulobank.clients.starter.v3.adapters.in.sqs.handler;

import com.lulobank.clients.starter.v3.adapters.in.sqs.event.CreatePreApprovedOfferMessage;
import com.lulobank.clients.v3.adapters.port.in.CreatePreApprovedOffer.CreatePreApprovedOfferPort;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.events.api.EventHandler;
import io.vavr.control.Try;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;

@CustomLog
@RequiredArgsConstructor
public class CreatePreApprovedOfferHandler implements EventHandler<CreatePreApprovedOfferMessage> {

    private final CreatePreApprovedOfferPort createPreApprovedOfferPort;

    @Override
    public Try<Void> execute(CreatePreApprovedOfferMessage event) {
        return Try.of(() -> buildRequest(event))
                .flatMap(createPreApprovedOfferPort::execute)
                .onSuccess(success -> log.info("Create Pre-Approved Offer event was successful process , idClient {} ",
                        event.getIdClient()))
                .onFailure(error -> log.info("Create Pre-Approved Offer event was failed , idClient {}, msg : {} ",
                        event.getIdClient(), error.getMessage(), error));
    }

    private ClientsV3Entity buildRequest(CreatePreApprovedOfferMessage event) {
        ClientsV3Entity clientsV3Entity = new ClientsV3Entity();
        clientsV3Entity.setIdClient(event.getIdClient());
        clientsV3Entity.setValue(event.getMaxTotalAmount());
        return clientsV3Entity;
    }

    @Override
    public Class<CreatePreApprovedOfferMessage> eventClass() {
        return CreatePreApprovedOfferMessage.class;
    }
}
