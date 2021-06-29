package com.lulobank.clients.starter.v3.adapters.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.lulobank.clients.starter.v3.adapters.out.dynamo.ClientsV3RepositoryConfig;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.usecase.productoffers.CreateClientProductOfferUseCase;

@Configuration
@Import(ClientsV3RepositoryConfig.class)
public class CreateProductOfferUseCaseConfig {

    @Bean
    public CreateClientProductOfferUseCase getCreateClientProductOfferUseCase(ClientsV3Repository clientsV3Repository) {
        return new CreateClientProductOfferUseCase(clientsV3Repository);
    }
}

