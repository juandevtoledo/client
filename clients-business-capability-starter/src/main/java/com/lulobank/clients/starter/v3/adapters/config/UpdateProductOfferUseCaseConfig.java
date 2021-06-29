package com.lulobank.clients.starter.v3.adapters.config;

import com.lulobank.clients.services.application.port.in.UpdateProductOffersPort;
import com.lulobank.clients.services.application.usecase.productoffers.UpdateProductOfferUseCase;
import com.lulobank.clients.starter.v3.adapters.out.dynamo.ClientsV3RepositoryConfig;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(ClientsV3RepositoryConfig.class)
public class UpdateProductOfferUseCaseConfig {

    @Bean
    public UpdateProductOffersPort updateProductOffersPort(ClientsV3Repository clientsV3Repository) {
        return new UpdateProductOfferUseCase(clientsV3Repository);
    }
}

