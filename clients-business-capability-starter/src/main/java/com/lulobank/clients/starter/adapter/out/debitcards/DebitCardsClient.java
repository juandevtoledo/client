package com.lulobank.clients.starter.adapter.out.debitcards;

import lombok.Getter;
import org.springframework.web.reactive.function.client.WebClient;

@Getter
public class DebitCardsClient {
    public static final String BASE_PATH = "cards";
    public static final String CARD_STATUS_BY_CLIENT = "/debit/status/clientInternal/{idClient}";
    public static final String CARD_BY_CLIENT = "/debit/clientInternal/{idClient}";
    public static final String ID_CLIENT_PLACEHOLDER = "{idClient}";

    private WebClient webClient;

    public DebitCardsClient(String baseUrl) {
        webClient = WebClient.builder()
                .baseUrl(baseUrl + BASE_PATH)
                .build();
    }
}
