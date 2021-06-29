package com.lulobank.clients.starter.v3.adapters.config;

import com.lulobank.clients.services.application.port.out.clientnotify.BlacklistStateNotifyPort;
import com.lulobank.clients.v3.adapters.port.in.activateblacklistedclient.ActivateBlacklistedClientPort;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.usecase.activateblacklistedclient.ActivateBlacklistedClientUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ActivateBlacklistedClientConfig {

    @Bean
    public ActivateBlacklistedClientPort getActivateBlacklistedClientPort(ClientsV3Repository clientsV3Repository, BlacklistStateNotifyPort blacklistStateNotifyPort){
        return new ActivateBlacklistedClientUseCase(clientsV3Repository,blacklistStateNotifyPort);
    }
}
