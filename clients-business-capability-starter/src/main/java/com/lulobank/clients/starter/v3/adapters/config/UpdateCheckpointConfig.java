package com.lulobank.clients.starter.v3.adapters.config;

import com.lulobank.clients.services.application.port.out.clientsdata.ClientsDataRepositoryPort;
import com.lulobank.clients.starter.v3.adapters.in.updatecheckpoint.handler.UpdateCheckpointHandler;
import com.lulobank.clients.v3.usecase.updatecheckpoint.UpdateCheckpointUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UpdateCheckpointConfig {

    @Bean
    public UpdateCheckpointUseCase getUpdateCheckpointUseCase(ClientsDataRepositoryPort clientsDataRepository){
        return new UpdateCheckpointUseCase(clientsDataRepository);
    }

    @Bean
    public UpdateCheckpointHandler getUpdateCheckpointHandler(UpdateCheckpointUseCase updateCheckpointUseCase){
        return new UpdateCheckpointHandler(updateCheckpointUseCase);
    }
}
