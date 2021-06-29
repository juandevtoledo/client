package com.lulobank.clients.starter.outboundadapter.notifications;

import com.lulobank.clientalerts.sdk.operations.impl.RetrofitClientNotificationsOperations;
import com.lulobank.clients.services.ports.out.ClientNotificationsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientsNotificationConfig {

    @Bean
    public ClientNotificationsService getClientNotificationsService(RetrofitClientNotificationsOperations retrofitClientNotificationsOperations) {
        return new ClientsNotificationAdapter(retrofitClientNotificationsOperations);
    }



}
