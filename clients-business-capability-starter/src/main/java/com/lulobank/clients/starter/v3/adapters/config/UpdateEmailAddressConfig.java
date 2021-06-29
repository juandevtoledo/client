package com.lulobank.clients.starter.v3.adapters.config;

import com.lulobank.clients.starter.v3.handler.UpdateEmailAddressHandler;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.usecase.UpdateEmailAddressUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UpdateEmailAddressConfig {

    @Bean
    public UpdateEmailAddressUseCase getUpdateEmailAddressUseCase(ClientsV3Repository clientsV3Repository) {
        return new UpdateEmailAddressUseCase(clientsV3Repository);
    }

    @Bean
    public UpdateEmailAddressHandler getUpdateEmailAddressHandler(UpdateEmailAddressUseCase updateEmailAddressUseCase) {
        return new UpdateEmailAddressHandler(updateEmailAddressUseCase);
    }

}
