package com.lulobank.clients.starter.v3.adapters.out.sqs;

import co.com.lulobank.tracing.sqs.SqsBraveTemplate;
import com.lulobank.clients.services.application.port.out.reporting.TransactionsMessagingPort;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.events.api.EventFactory;
import io.vavr.control.Try;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;

import static com.lulobank.clients.starter.v3.adapters.out.sqs.mapper.SqsTransactionsMapper.buildCheckReferralHoldsForNewClient;

@CustomLog
@RequiredArgsConstructor
public class SqsTransactionsAdapter implements TransactionsMessagingPort {

    private final String transactionsSqsEndpoint;
    private final SqsBraveTemplate sqsBraveTemplate;

    @Override
    public Try<Void> checkReferralHold(ClientsV3Entity clientsV3Entity) {
        log.info("Sending message to find and apply holds for client with id {} and phone number {}", clientsV3Entity.getIdClient(), clientsV3Entity.getPhoneNumber());
        return Try.run(() -> sqsBraveTemplate.convertAndSend(transactionsSqsEndpoint,
                EventFactory.ofDefaults(buildCheckReferralHoldsForNewClient(clientsV3Entity)).build()));
    }
}
