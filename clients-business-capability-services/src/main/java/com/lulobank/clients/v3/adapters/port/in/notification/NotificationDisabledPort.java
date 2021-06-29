package com.lulobank.clients.v3.adapters.port.in.notification;

import com.lulobank.clients.services.application.port.in.UseCase;
import com.lulobank.clients.services.domain.error.UseCaseResponseError;
import com.lulobank.clients.services.domain.notification.NotificationDisabledRequest;
import io.vavr.control.Either;

public interface NotificationDisabledPort extends UseCase<NotificationDisabledRequest,
        Either<UseCaseResponseError, Void>> {
}
