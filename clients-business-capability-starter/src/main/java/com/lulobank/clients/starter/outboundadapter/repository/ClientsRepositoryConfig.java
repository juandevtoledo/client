package com.lulobank.clients.starter.outboundadapter.repository;

import com.lulobank.clients.services.outboundadapters.repository.ClientsRepository;
import com.lulobank.clients.services.ports.repository.ClientsRepositoryV2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientsRepositoryConfig {

    @Bean
    public ClientsRepositoryV2 getClientRepositoryV2(ClientsRepository clientsRepository) {
        return new ClientsRepositoryAdapter(clientsRepository);
    }

}
