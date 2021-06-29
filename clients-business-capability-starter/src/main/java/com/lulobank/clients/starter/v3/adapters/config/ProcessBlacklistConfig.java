package com.lulobank.clients.starter.v3.adapters.config;

import com.lulobank.clients.services.application.port.out.clientnotify.ClientNotifyService;
import com.lulobank.clients.services.application.port.out.reporting.ReportingMessagingPort;
import com.lulobank.clients.services.application.port.out.reporting.TransactionsMessagingPort;
import com.lulobank.clients.services.features.clientverificationresult.BlacklistedProcessService;
import com.lulobank.clients.services.features.clientverificationresult.NonBlacklistedProcessService;
import com.lulobank.clients.services.outboundadapters.ClientsOutboundAdapter;
import com.lulobank.clients.services.ports.out.CustomerService;
import com.lulobank.clients.services.ports.out.CustomerServiceV2;
import com.lulobank.clients.services.ports.out.DigitalEvidenceService;
import com.lulobank.clients.starter.v3.adapters.out.sqs.riskengine.config.RiskEngineServiceConfig;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.adapters.port.out.riskengine.RiskEngineService;
import com.lulobank.clients.v3.adapters.port.out.savingsaccount.SavingsAccountV3Service;
import com.lulobank.clients.v3.usecase.accountsettlement.AccountSettlementUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(RiskEngineServiceConfig.class)
public class ProcessBlacklistConfig {

    @Bean
    public NonBlacklistedProcessService getNonBlacklistedProcessService(
                                                                        ClientsOutboundAdapter clientsOutboundAdapter,
                                                                        ClientsV3Repository clientsV3Repository,
                                                                        RiskEngineService riskEngineNotificationService
    ) {
        return new NonBlacklistedProcessService(clientsOutboundAdapter, clientsV3Repository, riskEngineNotificationService);
    }

    @Bean
    public BlacklistedProcessService getBlacklistedProcessService(ClientsOutboundAdapter clientsOutboundAdapter,
                                                                  ClientNotifyService clientNotifyService,
                                                                  ClientsV3Repository clientsV3Repository,
                                                                  CustomerService customerService
    ) {
        return new BlacklistedProcessService(clientsOutboundAdapter, clientNotifyService,
                clientsV3Repository, customerService);
    }

    @Bean
    public AccountSettlementUseCase getAccountSettlementUseCase(ClientsOutboundAdapter clientsOutboundAdapter,
                                                                CustomerServiceV2 customerServiceV2,
                                                                ClientsV3Repository clientsV3Repository,
                                                                TransactionsMessagingPort transactionsMessagingService,
                                                                SavingsAccountV3Service savingsAccountV3Service,
                                                                DigitalEvidenceService digitalEvidenceService,
                                                                ReportingMessagingPort reportingMessagingPort) {
        return new AccountSettlementUseCase(clientsOutboundAdapter,
                customerServiceV2, clientsV3Repository, transactionsMessagingService, savingsAccountV3Service, digitalEvidenceService, reportingMessagingPort);
    }

}
