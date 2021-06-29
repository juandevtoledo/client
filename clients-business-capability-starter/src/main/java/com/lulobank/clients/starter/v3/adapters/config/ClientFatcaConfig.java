package com.lulobank.clients.starter.v3.adapters.config;

import com.lulobank.clients.starter.v3.adapters.out.dynamo.ClientsV3RepositoryConfig;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.adapters.port.out.notification.report.CreateReportNotification;
import com.lulobank.clients.v3.usecase.fatca.ClientFatcaOnboardingUseCase;
import com.lulobank.clients.v3.usecase.fatca.ClientFatcaUseCase;
import com.lulobank.clients.v3.usecase.GetClientFatcaUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(ClientsV3RepositoryConfig.class)
public class ClientFatcaConfig {

    @Bean
    public ClientFatcaUseCase clientFatcaUseCase(ClientsV3Repository clientsV3Repository) {
        return new ClientFatcaUseCase(clientsV3Repository);
    }

    @Bean
    public GetClientFatcaUseCase getClientFatcaUseCase(ClientsV3Repository clientsV3Repository) {
        return new GetClientFatcaUseCase(clientsV3Repository);
    }

    @Bean
    public ClientFatcaOnboardingUseCase getClientFatcaOnboardingUseCase(ClientsV3Repository clientsV3Repository,
                                                                        CreateReportNotification createReportNotification){
        return new ClientFatcaOnboardingUseCase(clientsV3Repository, createReportNotification);
    }
}

