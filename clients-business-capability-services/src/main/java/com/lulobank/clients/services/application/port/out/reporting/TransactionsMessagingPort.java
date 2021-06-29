package com.lulobank.clients.services.application.port.out.reporting;

import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import io.vavr.control.Try;

public interface TransactionsMessagingPort {

    Try<Void> checkReferralHold(ClientsV3Entity clientsV3Entity);
}
