package com.lulobank.clients.starter.v3.adapters.out.sqs.config;

import co.com.lulobank.tracing.sqs.SqsBraveTemplate;
import com.lulobank.clients.services.application.port.out.reporting.TransactionsMessagingPort;
import com.lulobank.clients.starter.v3.adapters.out.sqs.SqsTransactionsAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SqsTransactionsConfig {
    @Value("${cloud.aws.sqs.transactions-events}")
    private String transactionsSqsEndpoint;

    @Bean
    public TransactionsMessagingPort transactionsMessagingPort(SqsBraveTemplate sqsBraveTemplate) {
        return new SqsTransactionsAdapter(transactionsSqsEndpoint, sqsBraveTemplate);
    }
}
