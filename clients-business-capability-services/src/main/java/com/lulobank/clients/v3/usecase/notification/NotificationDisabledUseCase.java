package com.lulobank.clients.v3.usecase.notification;

import com.lulobank.clients.services.domain.error.UseCaseResponseError;
import com.lulobank.clients.services.domain.notification.NotificationDisabledRequest;
import com.lulobank.clients.services.ports.out.MessageService;
import com.lulobank.clients.v3.adapters.port.in.notification.NotificationDisabledPort;
import com.lulobank.clients.v3.adapters.port.out.messaging.dto.ClientAlertsProperties;
import com.lulobank.clients.v3.adapters.port.out.messaging.dto.NotificationCategory;
import com.lulobank.clients.v3.adapters.port.out.messaging.dto.NotificationDisabledDetails;
import com.lulobank.clients.v3.adapters.port.out.messaging.dto.NotificationDisabledTypeMessage;
import io.vavr.concurrent.Future;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

import static com.lulobank.clients.v3.error.UseCaseErrorStatus.CLI_180;
import static com.lulobank.clients.v3.error.UseCaseErrorStatus.DEFAULT_DETAIL;
import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.isIn;


@Slf4j
@RequiredArgsConstructor
public class NotificationDisabledUseCase implements NotificationDisabledPort {

    private final MessageService messageService;
    private final ClientAlertsProperties clientAlertsProperties;

    @Override
    public Either<UseCaseResponseError, Void> execute(NotificationDisabledRequest command) {
        return Try.of(()-> sendNotification(command))
                .map(Either::<UseCaseResponseError, Void>right)
                .getOrElse(()-> Either.left(new UseCaseResponseError(CLI_180.name(),
                        "500", DEFAULT_DETAIL)));
    }

    private Void sendNotification(NotificationDisabledRequest request){
        return Future.run(()-> notificationMessageFactory(request)
                .peek(messageService::sendNotificationDisabled))
                .onFailure(e->log.error("Error sending {} notification. Client: {}. Cause: {}, ", request.getNotificationType(),
                        request.getIdClient(), e.getMessage(), e)).get();
    }

    private Option<NotificationDisabledTypeMessage> notificationMessageFactory(NotificationDisabledRequest request){
        return Match(request.getNotificationType()).option(
                Case($(isIn(NotificationDisabledType.PUSH_NOTIFICATIONS_DISABLED)),
                        messageBuilderNotification(NotificationDisabledType.PUSH_NOTIFICATIONS_DISABLED.toString(),
                        request, clientAlertsProperties.getNotificationDisabled())));
    }

    private NotificationDisabledTypeMessage messageBuilderNotification(String notificationType,
                                                                       NotificationDisabledRequest request,
                                                                       NotificationDisabledDetails details){
        return NotificationDisabledTypeMessage.builder()
                .idClient(request.getIdClient())
                .operation(notificationType)
                .category(NotificationCategory.SECURITY)
                .title(details.getTitle())
                .description(details.getDescription())
                .dateNotification(LocalDateTime.now().toString())
                .build();
    }

}
