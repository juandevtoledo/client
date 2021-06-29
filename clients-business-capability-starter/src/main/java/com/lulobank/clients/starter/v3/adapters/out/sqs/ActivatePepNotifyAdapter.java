package com.lulobank.clients.starter.v3.adapters.out.sqs;

import co.com.lulobank.tracing.sqs.SqsBraveTemplate;
import com.lulobank.clients.starter.v3.adapters.out.sqs.dto.EmailNotificationMessage;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.clients.v3.adapters.port.out.notification.ActivatePepNotifyPort;
import com.lulobank.events.api.EventFactory;
import io.vavr.control.Try;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;

import static com.lulobank.clients.starter.v3.adapters.out.sqs.Common.CommonSqsNotification.buildParamsPepReactivationNotification;

@CustomLog
@RequiredArgsConstructor
public class ActivatePepNotifyAdapter implements ActivatePepNotifyPort {
    private final String notificationSqsEndpoint;
    private final SqsBraveTemplate sqsBraveTemplate;


    @Override
    public Try<Void> sendActivatePepNotification(ClientsV3Entity clientEntity) {
        return Try.run(() -> sqsBraveTemplate.convertAndSend(notificationSqsEndpoint,
                EventFactory.ofDefaults(buildActivatePepNotification(clientEntity))
                        .build()));
    }

    private  EmailNotificationMessage buildActivatePepNotification(ClientsV3Entity clientEntity) {
        return EmailNotificationMessage.builder()
                                .clientId(clientEntity.getIdClient())
                                .to(clientEntity.getEmailAddress())
                                .notificationType("PEP_REACTIVATION")
                                .attributes(buildParamsPepReactivationNotification(clientEntity).toJavaMap())
                                .build();
    }

}
