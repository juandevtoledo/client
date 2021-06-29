package com.lulobank.clients.starter.v3.adapters.config;

import com.lulobank.clients.services.application.port.out.clientsdata.ClientsDataRepositoryPort;
import com.lulobank.clients.starter.v3.adapters.in.createaddress.handler.ClientCreateAddressHandler;
import com.lulobank.clients.v3.usecase.createaddress.ClientCreateAddressUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientCreateAddressConfig {

    @Bean
    public ClientCreateAddressUseCase getClientCreateAddressUseCase(ClientsDataRepositoryPort clientsDataRepository){
        return new ClientCreateAddressUseCase(clientsDataRepository);
    }

    @Bean
    public ClientCreateAddressHandler getClientCreateAddressHandler(ClientCreateAddressUseCase clientCreateAddressUseCase){
        return new ClientCreateAddressHandler(clientCreateAddressUseCase);
    }
}
