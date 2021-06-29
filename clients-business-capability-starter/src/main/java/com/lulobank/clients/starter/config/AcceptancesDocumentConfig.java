package com.lulobank.clients.starter.config;

import com.lulobank.clients.services.ports.out.AcceptancesDocumentService;
import com.lulobank.clients.services.ports.repository.ClientsRepositoryV2;
import com.lulobank.clients.services.usecase.AcceptancesDocumentUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AcceptancesDocumentConfig {
    @Bean
    public AcceptancesDocumentUseCase getAcceptancesDocumentUseCase(AcceptancesDocumentService acceptancesDocumentService,
                                                                    ClientsRepositoryV2 clientsRepositoryV2){
        return new AcceptancesDocumentUseCase(acceptancesDocumentService, clientsRepositoryV2);
    }
}
