package com.lulobank.clients.starter.v3.adapters.out.sqs.config;

import co.com.lulobank.tracing.sqs.SqsBraveTemplate;
import com.lulobank.clients.services.application.port.out.clientnotify.ClientNotifyService;
import com.lulobank.clients.starter.v3.adapters.out.sqs.SqsClientNotificationAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SqsClientNotificationConfig {

    @Value("${cloud.aws.sqs.notification-events-v2.url}")
    private String notificationSqsEndpoint;

    @Value("${cloud.aws.sqs.notification-events-v2.max-number-of-messages}")
    private Integer maximumReceives;

    @Value("${cloud.aws.sqs.notification-events-v2.delay}")
    private Integer delay;

    @Bean
    public ClientNotifyService clientNotifyService(SqsBraveTemplate sqsBraveTemplate) {
        return new SqsClientNotificationAdapter(notificationSqsEndpoint, sqsBraveTemplate, maximumReceives, delay);
    }
}
