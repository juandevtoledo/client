package com.lulobank.clients.starter.v3.adapters.config;

import com.lulobank.clients.v3.usecase.pep.UpdatePepOnboardingUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.usecase.pep.GetPepUseCase;
import com.lulobank.clients.v3.usecase.pep.UpdatePepUseCase;

@Configuration
public class PepConfig {

    @Bean
    public UpdatePepUseCase getUpdatePepUseCase(ClientsV3Repository clientsV3Repository) {
        return new UpdatePepUseCase(clientsV3Repository);
    }

    @Bean
    public UpdatePepOnboardingUseCase getUpdatePepOnboardingUseCase(ClientsV3Repository clientsV3Repository) {
        return new UpdatePepOnboardingUseCase(clientsV3Repository);
    }

    @Bean
    public GetPepUseCase getGetPepUseCasee(ClientsV3Repository clientsV3Repository) {
        return new GetPepUseCase(clientsV3Repository);
    }
}
