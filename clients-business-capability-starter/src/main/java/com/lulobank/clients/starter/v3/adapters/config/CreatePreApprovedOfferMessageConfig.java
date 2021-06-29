package com.lulobank.clients.starter.v3.adapters.config;

import com.lulobank.clients.v3.adapters.port.in.CreatePreApprovedOffer.CreatePreApprovedOfferPort;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.usecase.riskEngineResultEventv2.CreatePreApprovedOfferUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CreatePreApprovedOfferMessageConfig {

    @Bean
    public CreatePreApprovedOfferPort getCreatePreApprovedOfferHandler(ClientsV3Repository clientsV3Repository) {
        return new CreatePreApprovedOfferUseCase(clientsV3Repository);
    }
}
