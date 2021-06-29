package com.lulobank.clients.starter.v3.adapters.in.sqs.handler;

import com.lulobank.clients.starter.v3.adapters.in.sqs.event.CreateProductOfferMessage;
import com.lulobank.clients.v3.usecase.productoffers.CreateClientProductOfferUseCase;
import com.lulobank.clients.v3.usecase.productoffers.command.CreateClientProductOfferRequest;
import com.lulobank.events.api.EventHandler;

import io.vavr.control.Try;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;

@CustomLog
@RequiredArgsConstructor
public class CreateProductOfferHandler implements EventHandler<CreateProductOfferMessage> {

    private final CreateClientProductOfferUseCase createClientProductOfferUseCase;

    @Override
    public Try<Void> execute(CreateProductOfferMessage event) {
        return createClientProductOfferUseCase.execute(buildRequest(event))
        	.toTry()
        	.flatMap(clientProductOffer -> Try.run(() -> log.info("CreateProductOfferHandler was succes")));
    }

    private CreateClientProductOfferRequest buildRequest(CreateProductOfferMessage event) {
    	return CreateClientProductOfferRequest.builder()
    			.idClient(event.getIdClient())
    			.type(event.getType())
    			.value(event.getValue())
    			.build();
    }

    @Override
    public Class<CreateProductOfferMessage> eventClass() {
        return CreateProductOfferMessage.class;
    }
}
