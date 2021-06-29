package com.lulobank.clients.services.application.port.in;

import com.lulobank.clients.services.domain.findclientbyidbsc.NotifyAutomaticPaymentRequest;
import io.vavr.control.Try;

public interface NotifyAutomaticPaymentPort extends UseCase<NotifyAutomaticPaymentRequest, Try<Void>> {
}
