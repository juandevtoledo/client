package com.lulobank.clients.sdk.operations.impl;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.lulobank.clients.sdk.operations.AcceptancesDocumentOperation;
import io.vavr.control.Try;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.junit.Assert.assertTrue;

public class RetrofitAcceptancesDocumentTest {

    private final String URL_APP_ACCEPTANCES = "/clients/123-abcd/app-acceptances";
    private static final String SERVICE_URL = "http://localhost:8982/";

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8982);

    @Test
    public void shouldAcceptancesDocumentOk() {
        AcceptancesDocumentOperation retrofit =
                new RetrofitAcceptancesDocument(SERVICE_URL);
        wireMockRule.stubFor(
                put(urlMatching(URL_APP_ACCEPTANCES))
                        .willReturn(
                                aResponse()
                                        .withStatus(HttpStatus.OK.value())
                                        .withBody("")
                                        .withHeader("Content-Type", "application/json")));
        assertTrue(
                retrofit.createAcceptancesDocument(new HashMap<>(), "123-abcd").get());
    }

    @Test
    public void shouldAcceptancesDocumentInternalServerError() {
        AcceptancesDocumentOperation retrofit =
                new RetrofitAcceptancesDocument(SERVICE_URL);
        wireMockRule.stubFor(
                put(urlMatching(URL_APP_ACCEPTANCES))
                        .willReturn(aResponse().withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())));
        Try<Boolean> response = retrofit.createAcceptancesDocument(new HashMap<>(), "123-abcd");
        assertTrue(response.isFailure());
    }
}
