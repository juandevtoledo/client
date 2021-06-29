package com.lulobank.clients.starter.v3.adapters.config;

import com.lulobank.clients.services.application.port.out.clientnotify.ClientNotifyService;
import com.lulobank.clients.services.application.usecase.loanautomaticpayment.NotifyAutomaticPaymentUseCase;
import com.lulobank.clients.starter.v3.adapters.out.sqs.config.SqsClientNotificationConfig;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(SqsClientNotificationConfig.class)
public class NotifyAutomaticPaymentUseCaseConfig {

    @Bean
    public NotifyAutomaticPaymentUseCase notifyAutomaticPaymentUseCase(ClientsV3Repository clientsV3Repository, ClientNotifyService clientNotifyService) {
        return new NotifyAutomaticPaymentUseCase(clientsV3Repository, clientNotifyService);
    }
}
