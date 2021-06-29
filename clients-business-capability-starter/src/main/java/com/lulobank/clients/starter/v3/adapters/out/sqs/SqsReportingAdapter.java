package com.lulobank.clients.starter.v3.adapters.out.sqs;

import co.com.lulobank.tracing.sqs.SqsBraveTemplate;
import com.lulobank.clients.services.application.port.out.reporting.ReportingMessagingPort;
import com.lulobank.clients.starter.v3.adapters.out.sqs.dto.SendSignedDocumentGroupEvent;
import com.lulobank.clients.starter.v3.adapters.out.sqs.mapper.SqsReportingMapper;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.events.api.EventFactory;
import io.vavr.control.Try;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;

@CustomLog
@RequiredArgsConstructor
public class SqsReportingAdapter implements ReportingMessagingPort {

    private final String reportingSqsEndpoint;
    private final SqsBraveTemplate sqsBraveTemplate;

    @Override
    public Try<Void> sendBlacklistedDocuments(ClientsV3Entity clientEntity) {
        SendSignedDocumentGroupEvent event = SqsReportingMapper.buildSignedDocumentEventBlacklisted(clientEntity);
        return Try.run(() -> sqsBraveTemplate.convertAndSend(reportingSqsEndpoint,
                EventFactory.ofDefaults(event).build()));
    }

    @Override
    public Try<Void> sendCatDocument(ClientsV3Entity clientEntity){
        SendSignedDocumentGroupEvent event = SqsReportingMapper.buildSignedDocumentCatDocument(clientEntity);
        return Try.run(() -> sqsBraveTemplate.convertAndSend(reportingSqsEndpoint,
                EventFactory.ofDefaults(event).build()));
    }
}
