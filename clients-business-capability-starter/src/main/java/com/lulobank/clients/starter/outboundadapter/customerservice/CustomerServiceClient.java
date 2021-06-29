package com.lulobank.clients.starter.outboundadapter.customerservice;

import lombok.Getter;
import org.springframework.web.reactive.function.client.WebClient;

@Getter
public class CustomerServiceClient {
    public static final String BASE_PATH = "customer-service";
    public static final String CREATE_USER_CUSTOMER = "/api/v1/customers";

    private WebClient webClient;

    public CustomerServiceClient(String baseUrl) {
        webClient = WebClient.builder()
                .baseUrl(baseUrl + BASE_PATH)
                .build();
    }
}
