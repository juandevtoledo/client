package com.lulobank.clients.starter.outboundadapter.transactions;

import com.lulobank.clients.services.application.port.out.transactions.savingsaccounts.TransactionsPort;
import com.lulobank.clients.starter.adapter.out.transactions.TransactionsAdapter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class TransactionsServiceConfig {
    @Value("${services.transactions.url}")
    private String baseUrl;
    public static final String BASE_PATH = "transactions";

    @Bean("TransactionsWebClient")
    public WebClient getTransactionsWebClient() {
        return WebClient.builder()
                .baseUrl(baseUrl + BASE_PATH)
                .build();
    }

    @Bean
    public TransactionsPort getTransactionsPort(@Qualifier("TransactionsWebClient") WebClient webClient) {
        return new TransactionsAdapter(webClient);
    }
}
