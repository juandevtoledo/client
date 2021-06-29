package com.lulobank.clients.starter.outboundadapter.sqs;

import com.lulobank.clients.services.events.EventMapperV2;
import com.lulobank.clients.services.events.EventV2;
import com.lulobank.clients.services.events.StoreDigitalEvidence;

public class DigitalEvidenceEvent extends SqsIntegration<String, StoreDigitalEvidence>{


    public DigitalEvidenceEvent(String endpoint) {
        super(endpoint);
    }

    @Override
    public EventV2<StoreDigitalEvidence> map(String event) {
        StoreDigitalEvidence storeDigitalEvidence = new StoreDigitalEvidence();
        storeDigitalEvidence.setIdClient(event);
        return EventMapperV2.of(storeDigitalEvidence);
    }
}
