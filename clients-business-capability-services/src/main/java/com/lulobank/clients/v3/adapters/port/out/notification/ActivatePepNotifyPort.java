package com.lulobank.clients.v3.adapters.port.out.notification;

import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import io.vavr.control.Try;

public interface ActivatePepNotifyPort {

    Try<Void> sendActivatePepNotification(ClientsV3Entity clientEntity);

}
