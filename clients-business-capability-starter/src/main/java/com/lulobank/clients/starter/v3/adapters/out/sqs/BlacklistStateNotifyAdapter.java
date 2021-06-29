package com.lulobank.clients.starter.v3.adapters.out.sqs;

import co.com.lulobank.tracing.sqs.SqsBraveTemplate;
import com.lulobank.clients.services.application.port.out.clientnotify.BlacklistStateNotifyPort;
import com.lulobank.clients.services.domain.StateBlackList;
import com.lulobank.clients.starter.v3.adapters.out.sqs.dto.EmailNotificationMessage;
import com.lulobank.clients.starter.v3.adapters.out.sqs.error.SqsClientNotificationError;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.events.api.EventFactory;
import io.vavr.API;
import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.CustomLog;

import static com.lulobank.clients.services.domain.BlacklistNotificationType.BLACKLISTED_HIGH_RISK;
import static com.lulobank.clients.services.domain.BlacklistNotificationType.CLIENT_WHITELISTED;
import static com.lulobank.clients.starter.v3.adapters.out.sqs.Common.CommonSqsNotification.buildParamsBlacklistNotification;
import static com.lulobank.clients.starter.v3.adapters.out.sqs.error.SqsClientNotificationError.invalidBlacklistState;
import static io.vavr.API.$;
import static io.vavr.API.Case;

@CustomLog
@AllArgsConstructor
public class BlacklistStateNotifyAdapter implements BlacklistStateNotifyPort {
    private final String notificationSqsEndpoint;
    private final SqsBraveTemplate sqsBraveTemplate;

    @Override
    public Try<Void> sendBlacklistStateNotification(ClientsV3Entity clientEntity) {
        return Try.run(() -> sqsBraveTemplate.convertAndSend(notificationSqsEndpoint,
                EventFactory.ofDefaults(buildBlacklistStateNotification(clientEntity).get())
                        .build()));
    }

    private Either<SqsClientNotificationError,EmailNotificationMessage> buildBlacklistStateNotification(ClientsV3Entity clientEntity) {
        return getNotificationType(clientEntity)
                .map( notificationType ->
                        EmailNotificationMessage.builder()
                                .clientId(clientEntity.getIdClient())
                                .to(clientEntity.getEmailAddress())
                                .notificationType(notificationType)
                                .attributes(buildParamsBlacklistNotification(clientEntity).toJavaMap())
                                .build())
                .peekLeft( error -> log.error(error.getMessage()));
    }

    private Either<SqsClientNotificationError,String> getNotificationType(ClientsV3Entity clientEntity) {
        return API.Match(clientEntity.getBlackListState()).of(
                Case($(StateBlackList.WHITELISTED.name()),Either.right(CLIENT_WHITELISTED.name())),
                Case($(StateBlackList.BLACKLIST_COMPLIANCE.name()),Either.right(BLACKLISTED_HIGH_RISK.name())),
                Case($(), Either.left(invalidBlacklistState(clientEntity.getBlackListState())))
        );
    }

}
