package com.lulobank.clients.v3.adapters.port.in.activateselfcertifiedpepclient;

import com.lulobank.clients.services.application.port.in.UseCase;
import com.lulobank.clients.services.domain.activateselfcertifiedpepclient.ActivatePepClientRequest;
import io.vavr.control.Try;

public interface ActivatePepClientPort extends UseCase<ActivatePepClientRequest, Try<Void>>  {
}
