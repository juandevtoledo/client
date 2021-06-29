package com.lulobank.clients.starter.adapter.out.debitcards.config;

import com.lulobank.clients.services.application.port.out.debitcards.DebitCardsPort;
import com.lulobank.clients.starter.adapter.out.debitcards.DebitCardsAdapter;
import com.lulobank.clients.starter.adapter.out.debitcards.DebitCardsClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DebitCardsServiceConfig {

    @Value("${services.cards.url}")
    private String serviceDomain;

    @Bean
    public DebitCardsClient debitCardsClient() {
        return new DebitCardsClient(serviceDomain);
    }

    @Bean
    public DebitCardsPort getDebitCardsPort(DebitCardsClient debitCardsClient) {
        return new DebitCardsAdapter(debitCardsClient);
    }
}
