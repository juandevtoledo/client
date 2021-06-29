package com.lulobank.clients.starter.v3.adapters.out.sqs;

import co.com.lulobank.tracing.sqs.SqsBraveTemplate;
import com.lulobank.clients.services.events.EconomicInformationEvent;
import com.lulobank.clients.services.events.IdentityInformation;
import com.lulobank.clients.v3.events.RiskEngineNotificationService;
import com.lulobank.events.api.EventFactory;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.CustomLog;

import java.util.Map;

@CustomLog
@AllArgsConstructor
public class RiskEngineNotificationAdapter implements RiskEngineNotificationService {

    private final String notificationSqsEndpoint;
    private final SqsBraveTemplate sqsBraveTemplate;

    @Override
    public Try<Void> setEconomicInformation(EconomicInformationEvent economicInformationEvent, Map<String, Object> header, String idClient) {
        return Try.run(() -> sqsBraveTemplate.convertAndSend(notificationSqsEndpoint,
                EventFactory.ofDefaults(economicInformationEvent).id(idClient)
                        .build()))
                .peek(entity -> log.info("Sending economic information event: {}",economicInformationEvent.getSavingPurpose()))
                .onFailure(e -> log.error("Error sending identity information event: {}", e.getMessage()))
                ;
    }

    @Override
    public Try<Void> setIdentityInformation(IdentityInformation identityInformation, Map<String, Object> header, String idClient) {
        return Try.run(() -> sqsBraveTemplate.convertAndSend(notificationSqsEndpoint,
                EventFactory.ofDefaults(identityInformation).id(idClient)
                        .build()))
                .peek(resp -> log.info("Sending identity information event: {}", identityInformation.getDocumentNumber()))
                .onFailure(e -> log.error("Error sending identity information event: {}", e.getMessage()))
                ;
    }
}
