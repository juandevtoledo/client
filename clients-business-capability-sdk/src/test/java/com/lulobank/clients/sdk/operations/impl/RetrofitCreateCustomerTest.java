package com.lulobank.clients.sdk.operations.impl;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.lulobank.clients.sdk.operations.AcceptancesDocumentOperation;
import com.lulobank.clients.sdk.operations.CreateCustomerOperation;
import io.vavr.control.Try;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.junit.Assert.assertTrue;

public class RetrofitCreateCustomerTest {

    private final String URL_CREATE_CUSTOMER = "/clients/123-abcd/create-customer";
    private static final String SERVICE_URL = "http://localhost:8982/";

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8982);

    @Test
    public void shouldCreateCustomer() {
        CreateCustomerOperation retrofit =
                new RetrofitCreateCustomer(SERVICE_URL);
        wireMockRule.stubFor(
                put(urlMatching(URL_CREATE_CUSTOMER))
                        .willReturn(
                                aResponse()
                                        .withStatus(HttpStatus.CREATED.value())
                                        .withBody("")
                                        .withHeader("Content-Type", "application/json")));
        assertTrue(
                retrofit.createCustomer(new HashMap<>(), "123-abcd").get());
    }

    @Test
    public void shouldCreateCustomerInternalServerError() {
        CreateCustomerOperation retrofit =
                new RetrofitCreateCustomer(SERVICE_URL);
        wireMockRule.stubFor(
                put(urlMatching(URL_CREATE_CUSTOMER))
                        .willReturn(aResponse().withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())));
        Try<Boolean> response = retrofit.createCustomer(new HashMap<>(), "123-abcd");
        assertTrue(response.isFailure());
    }
}
