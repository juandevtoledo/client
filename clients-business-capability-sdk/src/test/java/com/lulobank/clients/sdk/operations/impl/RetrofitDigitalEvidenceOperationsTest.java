package com.lulobank.clients.sdk.operations.impl;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.lulobank.clients.sdk.operations.exception.ClientsServiceException;
import io.vavr.control.Try;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.util.HashMap;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class RetrofitDigitalEvidenceOperationsTest {

    private static final String SERVICE_URLBASE = "http://localhost:8982/";
    private static final String POST_SAVE_DIGITAL_EVIDENCE = "/clients/18e65463-3144-4a69-9e7c-a8aab6c48b2d/digital-evidence";

    @Rule public WireMockRule wireMockRule = new WireMockRule(8982);

    @Test
    public void saveDigitalEvidenceWasConsumedSuccessfully() {
        RetrofitDigitalEvidenceOperations retrofit = new RetrofitDigitalEvidenceOperations(SERVICE_URLBASE);
        wireMockRule.stubFor(
                post(urlMatching(POST_SAVE_DIGITAL_EVIDENCE))
                        .willReturn(
                                aResponse()
                                        .withStatus(HttpStatus.OK.value())
                                        .withBody("")
                                        .withHeader("Content-Type", "application/json")));

        Try<Boolean> digitalEvidenceResponse = retrofit.createDigitalEvidence(new HashMap<>(), "18e65463-3144-4a69-9e7c-a8aab6c48b2d");

        assertTrue(digitalEvidenceResponse.isSuccess());
    }

    @Test(expected = ClientsServiceException.class)
    public void saveDigitalEvidenceReturnedInternalServerError() {
        RetrofitDigitalEvidenceOperations retrofit = new RetrofitDigitalEvidenceOperations(SERVICE_URLBASE);
        wireMockRule.stubFor(
                post(urlMatching(POST_SAVE_DIGITAL_EVIDENCE))
                        .willReturn(
                                aResponse()
                                        .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                        .withBody("")
                                        .withHeader("Content-Type", "application/json")));

        Try<Boolean> digitalEvidenceResponse = retrofit.createDigitalEvidence(new HashMap<>(), "18e65463-3144-4a69-9e7c-a8aab6c48b2d");

        assertTrue(digitalEvidenceResponse.isFailure());
        digitalEvidenceResponse.get();
    }

}