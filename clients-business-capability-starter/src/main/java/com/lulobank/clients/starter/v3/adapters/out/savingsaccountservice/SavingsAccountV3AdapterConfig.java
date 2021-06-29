package com.lulobank.clients.starter.v3.adapters.out.savingsaccountservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SavingsAccountV3AdapterConfig {

    @Value("${services.savings.url}")
    private String serviceDomain;

    @Bean
    public SavingsAccountV3Adapter savingsAccountV3Adapter(){
        return new SavingsAccountV3Adapter(serviceDomain);
    }
}
