package com.lulobank.clients.starter.adapter.out.reporting;

import lombok.Getter;
import org.springframework.web.reactive.function.client.WebClient;

@Getter
public class ReportingClient {

    public static final String BASE_PATH = "reporting";
    public static final String BLACKLISTED_DIGITAL_EVIDENCE = "/clients/{idClient}/digital-evidence";
    public static final String ID_CLIENT_PLACEHOLDER = "{idClient}";

    private WebClient webClient;

    public ReportingClient(String baseUrl) {
        webClient = WebClient.builder()
                .baseUrl(baseUrl + BASE_PATH)
                .build();
    }

}
