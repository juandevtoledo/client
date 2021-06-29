package com.lulobank.clients.services.application.port.out.clientnotify;

import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import io.vavr.control.Try;

public interface BlacklistStateNotifyPort {

    Try<Void> sendBlacklistStateNotification(ClientsV3Entity clientEntity);
}