package com.lulobank.clients.starter.v3.adapters.out.sqs.config;

import co.com.lulobank.tracing.sqs.SqsBraveTemplate;
import com.lulobank.clients.services.application.port.out.clientnotify.BlacklistStateNotifyPort;
import com.lulobank.clients.starter.v3.adapters.out.sqs.BlacklistStateNotifyAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BlacklistStateNotifyConfig {

    @Value("${cloud.aws.sqs.notification-events-v2.url}")
    private String notificationSqsEndpoint;

    @Bean
    public BlacklistStateNotifyPort blacklistStateNotifyPort(SqsBraveTemplate sqsBraveTemplate){
        return new BlacklistStateNotifyAdapter(notificationSqsEndpoint,sqsBraveTemplate);
    }
}
