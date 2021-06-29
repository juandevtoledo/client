package com.lulobank.clients.starter.v3.adapters.out.sqs;

import co.com.lulobank.tracing.sqs.SqsBraveTemplate;
import com.lulobank.clients.starter.v3.adapters.out.sqs.dto.CreateReportMessage;
import com.lulobank.clients.v3.adapters.port.out.notification.report.CreateReportNotification;
import com.lulobank.events.api.EventFactory;
import io.vavr.control.Try;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;

@CustomLog
@RequiredArgsConstructor
public class CreateFatcaReportNotificationAdapter implements CreateReportNotification {

    private final String reportingXbcSqsEndpoint;
    private final SqsBraveTemplate sqsBraveTemplate;


    @Override
    public <T> Try<Void> sendReport(String idClient, String productType, String reportType, T data) {

        return Try.run(() -> sqsBraveTemplate.convertAndSend(reportingXbcSqsEndpoint,
                EventFactory.ofDefaults(CreateReportMessage
                        .builder()
                        .idClient(idClient)
                        .productType(productType)
                        .reportType(reportType)
                        .data(data)
                        .build())
                        .build()))
                .peek(v->log.info(String.format("Notification for create fatca digital evidence was send successful %s", idClient)))
                .onFailure(e->log.error(String.format("Notification for create fatca digital evidence was failed %s %s", idClient,e.getMessage())));
    }
}
