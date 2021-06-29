package com.lulobank.clients.starter.adapter.out.dynamodb.config;

import com.lulobank.clients.services.application.port.out.clientsdata.ClientsDataRepositoryPort;
import com.lulobank.clients.starter.adapter.out.dynamodb.ClientsRepositoryAdapter;
import com.lulobank.clients.starter.v3.adapters.out.dynamo.ClientsDataRepository;
import com.lulobank.tracing.DatabaseBrave;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientsRepositoryConfig {
    @Bean
    public ClientsDataRepositoryPort getTransactionsHistoryRepositoryService(
            ClientsDataRepository clientsDataRepository, DatabaseBrave databaseBrave) {
        return new ClientsRepositoryAdapter(clientsDataRepository,databaseBrave);
    }
}
