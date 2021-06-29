package com.lulobank.clients.starter.v3.adapters.out.sqs.config;

import co.com.lulobank.tracing.sqs.SqsBraveTemplate;
import com.lulobank.clients.starter.v3.adapters.out.sqs.ActivatePepNotifyAdapter;
import com.lulobank.clients.v3.adapters.port.out.notification.ActivatePepNotifyPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ActivatePepNotifyConfig {

    @Value("${cloud.aws.sqs.notification-events-v2.url}")
    private String notificationSqsEndpoint;

    @Bean
    public ActivatePepNotifyPort getActivatePepNotifyPort(SqsBraveTemplate sqsBraveTemplate){
        return new ActivatePepNotifyAdapter(notificationSqsEndpoint,sqsBraveTemplate);
    }
}
