package com.lulobank.clients.starter.config;

import com.lulobank.clients.services.outboundadapters.ClientsOutboundAdapter;
import com.lulobank.clients.services.ports.out.AcceptancesDocumentService;
import com.lulobank.clients.services.ports.out.AuthenticationService;
import com.lulobank.clients.services.ports.out.ClientNotificationsService;
import com.lulobank.clients.services.ports.out.IdentityProviderService;
import com.lulobank.clients.services.ports.out.ProfileService;
import com.lulobank.clients.services.usecase.InitialClientUseCase;
import com.lulobank.clients.starter.outboundadapter.authentication.AuthenticationServiceConfig;
import com.lulobank.clients.starter.outboundadapter.sqs.MessageAdapterConfig;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({MessageAdapterConfig.class, AuthenticationServiceConfig.class})
public class InitialClientConfig {


    @Bean
    public InitialClientUseCase getInitialClientUseCase(AuthenticationService authenticationService,
                                                        IdentityProviderService identityProviderService,
                                                        ClientsOutboundAdapter clientsOutboundAdapter,
                                                        ClientsV3Repository repositoryV2,
                                                        ClientNotificationsService clientNotificationsService,
                                                        AcceptancesDocumentService acceptancesDocumentService,
                                                        ProfileService profileService) {

        return new InitialClientUseCase(clientsOutboundAdapter,
                authenticationService,
                identityProviderService,
                repositoryV2,
                clientNotificationsService,
                acceptancesDocumentService,
                profileService);
    }

}
