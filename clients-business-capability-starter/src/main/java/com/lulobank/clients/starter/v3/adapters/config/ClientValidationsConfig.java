package com.lulobank.clients.starter.v3.adapters.config;

import com.lulobank.clients.services.ports.out.CustomerService;
import com.lulobank.clients.starter.v3.handler.ClientValidationsHandler;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.usecase.validations.EmailValidationsUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientValidationsConfig {

    @Bean
    public ClientValidationsHandler getClientValidationsHandler(EmailValidationsUseCase emailValidationsUseCase){
        return new ClientValidationsHandler(emailValidationsUseCase);
    }

    @Bean
    public EmailValidationsUseCase getEmailValidationsUseCase(ClientsV3Repository repository, CustomerService customerService){
        return new EmailValidationsUseCase(repository,customerService);
    }
}
