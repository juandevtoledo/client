package com.lulobank.clients.starter.adapter.out.savingsaccounts;

import lombok.Getter;
import org.springframework.web.reactive.function.client.WebClient;

@Getter
public class SavingsAccountsClient {
    public static final String BASE_PATH = "savingsaccounts";
    public static final String ACCOUNT_BY_CLIENT_ZENDESK = "/accountInternal/client/{idClient}";
    public static final String ID_CLIENT_PLACEHOLDER = "{idClient}";

    private WebClient webClient;

    public SavingsAccountsClient(String baseUrl) {
        webClient = WebClient.builder()
            .baseUrl(baseUrl + BASE_PATH)
            .build();
    }
}
