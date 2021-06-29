package com.lulobank.clients.starter.v3.adapters.config;

import com.lulobank.clients.v3.adapters.port.in.activateselfcertifiedpepclient.ActivatePepClientPort;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.adapters.port.out.notification.ActivatePepNotifyPort;
import com.lulobank.clients.v3.usecase.activateselfcertifiedpepclient.ActivatePepClientUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ActivatePepClientConfig {
    @Bean
    public ActivatePepClientPort getActivateSelfCertifiedPEPClientPort(ClientsV3Repository clientsV3Repository,
                                                                       ActivatePepNotifyPort activatePepNotifyPort){
        return new ActivatePepClientUseCase(clientsV3Repository, activatePepNotifyPort);
    }
}
