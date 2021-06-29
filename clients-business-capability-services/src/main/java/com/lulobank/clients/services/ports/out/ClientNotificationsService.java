package com.lulobank.clients.services.ports.out;


import com.lulobank.clientalerts.sdk.dto.notifications.InitialClientNotifications;
import io.vavr.control.Try;

import java.util.Map;

public interface ClientNotificationsService {

    Try<Void> initialClientNotifications(Map<String, String> headers, InitialClientNotifications initialClientNotifications);

}
