package com.lulobank.clients.starter.v3.adapters.config;

import com.lulobank.clients.starter.v3.adapters.out.dynamo.ClientsV3RepositoryConfig;
import com.lulobank.clients.starter.v3.adapters.out.savingsaccountservice.SavingsAccountV3Adapter;
import com.lulobank.clients.starter.v3.adapters.out.savingsaccountservice.SavingsAccountV3AdapterConfig;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.usecase.ChangeProductSavingUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ClientsV3RepositoryConfig.class, SavingsAccountV3AdapterConfig.class})
public class ChangeProductSavingUseCaseConfig {


    @Bean
    public ChangeProductSavingUseCase changeProductSavingUseCase(ClientsV3Repository clientsV3Repository, SavingsAccountV3Adapter savingsAccountV3Adapter){
        return new ChangeProductSavingUseCase(clientsV3Repository,savingsAccountV3Adapter);
    }
}
