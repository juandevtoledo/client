package com.lulobank.clients.starter.v3.adapters.config;

import com.lulobank.clients.services.ports.repository.ClientsRepositoryV2;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.usecase.phone.UpdatePhoneNumberUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UpdatePhoneNumberConfig {

    @Bean
    public UpdatePhoneNumberUseCase updatePhoneNumberUseCase(ClientsV3Repository clientsV3Repository,
                                                             ClientsRepositoryV2 clientsRepositoryV2){
        return new UpdatePhoneNumberUseCase(clientsV3Repository, clientsRepositoryV2);
    }
}
