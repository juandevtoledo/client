package com.lulobank.clients.starter.v3.adapters.out.sqs.config;

import co.com.lulobank.tracing.sqs.SqsBraveTemplate;
import com.lulobank.clients.starter.v3.adapters.out.sqs.RiskEngineNotificationAdapter;
import com.lulobank.clients.v3.events.RiskEngineNotificationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RiskEngineNotificationAdapterConfig {

    @Value("${cloud.aws.sqs.riskengine-events}")
    private String notificationSqsEndpoint;

    @Bean
    public RiskEngineNotificationService getRiskEngineNotificationService(SqsBraveTemplate sqsBraveTemplate) {
        return new RiskEngineNotificationAdapter(notificationSqsEndpoint, sqsBraveTemplate);
    }

}
