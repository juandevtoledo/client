package com.lulobank.clients.starter.v3.adapters.out.sqs;

import co.com.lulobank.tracing.sqs.SqsBraveTemplate;
import com.lulobank.clients.services.application.port.out.clientnotify.ClientNotifyService;
import com.lulobank.clients.services.domain.RiskLevelBlackList;
import com.lulobank.clients.services.events.LoanAutomaticDebitMessage;
import com.lulobank.clients.starter.v3.adapters.out.sqs.dto.EmailNotificationMessage;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.events.api.EventFactory;
import io.vavr.API;
import io.vavr.control.Try;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;

import static com.lulobank.clients.starter.v3.adapters.out.sqs.Common.CommonSqsNotification.buildParamsBlacklistNotification;
import static io.vavr.API.$;
import static io.vavr.API.Case;

@CustomLog
@RequiredArgsConstructor
public class SqsClientNotificationAdapter implements ClientNotifyService {

    private final String notificationSqsEndpoint;
    private final SqsBraveTemplate sqsBraveTemplate;
    private final Integer maximumReceives;
    private final Integer delay;

    @Override
    public Try<Void> automaticPayment(LoanAutomaticDebitMessage loanAutomaticDebitMessage) {
        return Try.run(() -> sqsBraveTemplate.convertAndSend(notificationSqsEndpoint,
                EventFactory.ofDefaults(loanAutomaticDebitMessage)
                        .delay(delay)
                        .maximumReceives(maximumReceives)
                        .build()));

    }

    @Override
    public Try<Void> sendBlacklistNotification(ClientsV3Entity clientEntity) {
        return Try.run(() -> sqsBraveTemplate.convertAndSend(notificationSqsEndpoint,
                EventFactory.ofDefaults(buildBlacklistEmailNotification(clientEntity))
                        .build()));
    }

    private EmailNotificationMessage buildBlacklistEmailNotification(ClientsV3Entity clientEntity) {
        return EmailNotificationMessage.builder()
                .clientId(clientEntity.getIdClient())
                .to(clientEntity.getEmailAddress())
                .notificationType(getNotificationType(clientEntity))
                .attributes(buildParamsBlacklistNotification(clientEntity).toJavaMap())
                .build();
    }

    private String getNotificationType(ClientsV3Entity clientEntity) {
        return API.Match(clientEntity.getBlackListRiskLevel()).of(
                Case($(RiskLevelBlackList.MID_RISK.getLevel()), "BLACKLISTED_MEDIUM_RISK"),
                Case($(RiskLevelBlackList.HIGH_RISK.getLevel()), "BLACKLISTED_HIGH_RISK")
        );
    }
}
