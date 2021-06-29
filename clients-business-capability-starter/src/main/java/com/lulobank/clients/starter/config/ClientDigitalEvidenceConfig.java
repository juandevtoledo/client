package com.lulobank.clients.starter.config;

import com.lulobank.clients.services.ports.out.DigitalEvidenceService;
import com.lulobank.clients.services.usecase.ClientDigitalEvidenceUseCase;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientDigitalEvidenceConfig {

    @Bean
    public ClientDigitalEvidenceUseCase getClientDigitalEvidenceUseCase(DigitalEvidenceService digitalEvidenceService,
                                                                        ClientsV3Repository clientsRepository
    ) {
        return new ClientDigitalEvidenceUseCase(digitalEvidenceService, clientsRepository);
    }

}
