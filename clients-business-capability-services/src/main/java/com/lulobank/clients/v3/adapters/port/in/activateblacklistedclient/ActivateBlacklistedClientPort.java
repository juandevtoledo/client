package com.lulobank.clients.v3.adapters.port.in.activateblacklistedclient;

import com.lulobank.clients.services.application.port.in.UseCase;
import com.lulobank.clients.services.domain.activateblacklistedclient.ActivateBlacklistedClientRequest;
import io.vavr.control.Try;

public interface ActivateBlacklistedClientPort extends UseCase<ActivateBlacklistedClientRequest, Try<Void>> {
}
