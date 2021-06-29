package com.lulobank.clients.starter.adapter.out;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lulobank.clients.services.application.port.out.transactions.savingsaccounts.error.TransactionsError;
import com.lulobank.clients.services.application.port.out.transactions.savingsaccounts.error.TransactionsErrorStatus;
import com.lulobank.clients.starter.adapter.BaseUnitTest;
import com.lulobank.clients.starter.adapter.Sample;
import com.lulobank.clients.starter.adapter.out.transactions.TransactionsAdapter;
import com.lulobank.clients.starter.adapter.out.transactions.dto.PendingTransfersDto;
import io.vavr.control.Either;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.lulobank.clients.starter.adapter.Constant.ID_CLIENT;
import static com.lulobank.clients.starter.adapter.out.transactions.TransactionsAdapter.ID_CLIENT_PLACEHOLDER;
import static com.lulobank.clients.starter.adapter.out.transactions.TransactionsAdapter.PENDING_TRANSACTIONS;
import static com.lulobank.clients.starter.outboundadapter.transactions.TransactionsServiceConfig.BASE_PATH;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TransactionsAdapterTest extends BaseUnitTest {
    private TransactionsAdapter testedClass;


    @Before
    public void init() {
        WebClient webClient = WebClient.builder().baseUrl("http://localhost:8082" + BASE_PATH).build();
        testedClass = new TransactionsAdapter(webClient);
    }

    @Test
    public void shouldReturnPendingTransactions() throws JsonProcessingException {
        PendingTransfersDto pendingTransfersDto = Sample.getResponsePendingTransactions();
        String responseStr = objectMapper.writeValueAsString(pendingTransfersDto);
        wireMockRule.stubFor(
                get(urlEqualTo("/" + BASE_PATH + PENDING_TRANSACTIONS.replace(ID_CLIENT_PLACEHOLDER, ID_CLIENT)))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody(responseStr)));

        Map<String, String> headers = Sample.getAuthorizationHeader();
        Either<TransactionsError, Boolean> response = testedClass.hasPendingTransactions(headers, ID_CLIENT);

        assertTrue(response.isRight());
        assertEquals(Boolean.TRUE, response.get());
    }

    @Test
    public void shouldReturnNotPendingTransactions() throws JsonProcessingException {
        PendingTransfersDto pendingTransfersDto = Sample.getResponseNotPendingTransactions();
        String responseStr = objectMapper.writeValueAsString(pendingTransfersDto);
        wireMockRule.stubFor(
                get(urlEqualTo("/" + BASE_PATH + PENDING_TRANSACTIONS.replace(ID_CLIENT_PLACEHOLDER, ID_CLIENT)))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody(responseStr)));

        Map<String, String> headers = Sample.getAuthorizationHeader();
        Either<TransactionsError, Boolean> response = testedClass.hasPendingTransactions(headers, ID_CLIENT);

        assertTrue(response.isRight());
        assertEquals(Boolean.FALSE, response.get());
    }

    @Test
    public void shouldReturnErrorConnection() {
        wireMockRule.stubFor(
                get(urlEqualTo("/" + BASE_PATH + PENDING_TRANSACTIONS.replace(ID_CLIENT_PLACEHOLDER, ID_CLIENT)))
                        .willReturn(aResponse()
                                .withStatus(500)
                                .withHeader("Content-Type", "application/json")));

        Map<String, String> headers = Sample.getAuthorizationHeader();
        Either<TransactionsError, Boolean> response = testedClass.hasPendingTransactions(headers, ID_CLIENT);

        assertTrue(response.isLeft());
        assertEquals(TransactionsErrorStatus.CLI_110.name(), response.getLeft().getBusinessCode());
    }
}
