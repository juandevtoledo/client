package com.lulobank.clients.starter.adapter.config;

import com.lulobank.clients.services.application.port.in.ZendeskClientInfoPort;
import com.lulobank.clients.services.application.port.out.clientsdata.ClientsDataRepositoryPort;
import com.lulobank.clients.services.application.port.out.debitcards.DebitCardsPort;
import com.lulobank.clients.services.application.port.out.savingsaccounts.SavingsAccountsPort;
import com.lulobank.clients.services.application.usecase.zendeskclientinfo.ZendeskClientInfoUseCase;
import com.lulobank.clients.services.ports.out.MessageService;
import com.lulobank.clients.starter.adapter.out.debitcards.config.DebitCardsServiceConfig;
import com.lulobank.clients.starter.adapter.out.dynamodb.config.ClientsRepositoryConfig;
import com.lulobank.clients.v3.adapters.port.in.notification.NotificationDisabledPort;
import com.lulobank.clients.v3.adapters.port.out.messaging.dto.ClientAlertsProperties;
import com.lulobank.clients.v3.usecase.notification.NotificationDisabledUseCase;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ClientsRepositoryConfig.class, DebitCardsServiceConfig.class})
public class ClientsUseCaseConfig {

    @Bean
    @ConfigurationProperties(prefix = "notifications.client-alerts")
    public ClientAlertsProperties getClientAlertsProperties() {
        return new ClientAlertsProperties();
    }

    @Bean
    public ZendeskClientInfoPort zendeskClientInfoPort(ClientsDataRepositoryPort clientsDataRepositoryPort,
                                                       SavingsAccountsPort savingsAccountsPort,
                                                       DebitCardsPort debitCardsPort
    ){
        return new ZendeskClientInfoUseCase(clientsDataRepositoryPort,savingsAccountsPort,debitCardsPort);
    }

    @Bean
    public NotificationDisabledPort notificationDisabledPort(MessageService messageService,
                                                             ClientAlertsProperties clientAlertsProperties){
        return new NotificationDisabledUseCase(messageService, clientAlertsProperties);
    }

}
