package com.lulobank.clients.starter.outboundadapter.notifications;

import com.lulobank.clientalerts.sdk.dto.notifications.InitialClientNotifications;
import com.lulobank.clientalerts.sdk.operations.IClientNotificationsOperation;
import com.lulobank.clients.services.exception.ClientsNotificationException;
import com.lulobank.clients.services.ports.out.ClientNotificationsService;
import io.vavr.control.Try;

import java.util.Map;

public class ClientsNotificationAdapter implements ClientNotificationsService {

    public static final String PROBLEMS_NOTIFYING_CLIENT = "Problems notifying creation of client %s";

    private final IClientNotificationsOperation retrofitClientNotificationsOperations;

    public ClientsNotificationAdapter(IClientNotificationsOperation retrofitClientNotificationsOperations) {
        this.retrofitClientNotificationsOperations = retrofitClientNotificationsOperations;
    }

    @Override
    public Try<Void> initialClientNotifications(Map<String, String> headers, InitialClientNotifications initialClient) {

        return Try.run(() -> retrofitClientNotificationsOperations.initialClientNotifications(headers, initialClient))
                .onFailure(e -> {
                    throw new ClientsNotificationException(String.format(PROBLEMS_NOTIFYING_CLIENT, initialClient.getIdClient()), e);
                });
    }

}
