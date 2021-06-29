package com.lulobank.clients.starter.adapter.out;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.http.Fault;
import com.lulobank.clients.services.application.port.out.debitcards.error.DebitCardsError;
import com.lulobank.clients.services.application.port.out.debitcards.error.DebitCardsErrorStatus;
import com.lulobank.clients.services.application.port.out.debitcards.model.CardStatus;
import com.lulobank.clients.services.application.port.out.debitcards.model.DebitCard;
import com.lulobank.clients.starter.adapter.BaseUnitTest;
import com.lulobank.clients.starter.adapter.Sample;
import com.lulobank.clients.starter.adapter.out.debitcards.DebitCardsAdapter;
import com.lulobank.clients.starter.adapter.out.debitcards.DebitCardsClient;
import com.lulobank.clients.starter.adapter.out.debitcards.dto.ResponseDebitCardInformation;
import com.lulobank.clients.starter.adapter.out.debitcards.dto.ResponseDebitCardStatus;
import io.vavr.control.Either;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.lulobank.clients.starter.adapter.Constant.CREATION_DATE_CARD;
import static com.lulobank.clients.starter.adapter.Constant.ID_CARD;
import static com.lulobank.clients.starter.adapter.Constant.ID_CLIENT;
import static com.lulobank.clients.starter.adapter.out.debitcards.DebitCardsClient.BASE_PATH;
import static com.lulobank.clients.starter.adapter.out.debitcards.DebitCardsClient.CARD_BY_CLIENT;
import static com.lulobank.clients.starter.adapter.out.debitcards.DebitCardsClient.CARD_STATUS_BY_CLIENT;
import static com.lulobank.clients.starter.adapter.out.debitcards.DebitCardsClient.ID_CLIENT_PLACEHOLDER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DebitCardsAdapterTest extends BaseUnitTest {
    private DebitCardsAdapter testedClass;

    @Before
    public void init() {
        testedClass = new DebitCardsAdapter(new DebitCardsClient("http://localhost:8082"));
    }


    @Test
    public void shouldReturnDebitCardInfo() throws JsonProcessingException {
        ResponseDebitCardInformation responseDebitCardInformation = Sample.getResponseDebitCardInformation();
        String responseStr = objectMapper.writeValueAsString(responseDebitCardInformation);
        wireMockRule.stubFor(
                get(urlEqualTo("/" + BASE_PATH + CARD_BY_CLIENT.replace(ID_CLIENT_PLACEHOLDER, ID_CLIENT)))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody(responseStr)));

        Map<String, String> headers = Sample.getAuthorizationHeader();
        Either<DebitCardsError, DebitCard>  response = testedClass.getDebitCardByIdClient(headers,ID_CLIENT);

        assertTrue(response.isRight());
        assertEquals(ID_CARD, response.get().getCardNumberMask());
    }

    @Test
    public void shouldReturnErrorConnection() {
        wireMockRule.stubFor(
                get(urlEqualTo("/" + BASE_PATH + CARD_BY_CLIENT.replace(ID_CLIENT_PLACEHOLDER, ID_CLIENT)))
                        .willReturn(aResponse()
                                .withStatus(500)
                                .withHeader("Content-Type", "application/json")));

        Map<String, String> headers = Sample.getAuthorizationHeader();
        Either<DebitCardsError, DebitCard>  response = testedClass.getDebitCardByIdClient(headers,ID_CLIENT);

        assertTrue(response.isLeft());
        assertEquals(DebitCardsErrorStatus.CLI_115.name(), response.getLeft().getBusinessCode());
    }

    @Test
    public void shouldReturnNotFound()  {
        wireMockRule.stubFor(
                get(urlEqualTo("/" + BASE_PATH + CARD_BY_CLIENT.replace(ID_CLIENT_PLACEHOLDER, ID_CLIENT)))
                        .willReturn(aResponse()
                                .withStatus(404)
                                .withHeader("Content-Type", "application/json")));

        Map<String, String> headers = Sample.getAuthorizationHeader();
        Either<DebitCardsError, DebitCard>  response = testedClass.getDebitCardByIdClient(headers,ID_CLIENT);

        assertTrue(response.isLeft());
        assertEquals(DebitCardsErrorStatus.CLI_116.name(), response.getLeft().getBusinessCode());
    }

    @Test
    public void shouldReturnConnectionErrorException()  {
        wireMockRule.stubFor(
                get(urlEqualTo("/" + BASE_PATH + CARD_BY_CLIENT.replace(ID_CLIENT_PLACEHOLDER, ID_CLIENT)))
                        .willReturn(aResponse().withFault(Fault.MALFORMED_RESPONSE_CHUNK)));

        Map<String, String> headers = Sample.getAuthorizationHeader();
        Either<DebitCardsError, DebitCard>  response = testedClass.getDebitCardByIdClient(headers,ID_CLIENT);

        assertTrue(response.isLeft());
        assertEquals(DebitCardsErrorStatus.CLI_115.name(), response.getLeft().getBusinessCode());
    }

    @Test
    public void shouldReturnDebitCardStatusInfo() throws JsonProcessingException {
        ResponseDebitCardStatus responseDebitCardInformation = Sample.getResponseDebitCardStatus();
        String responseStr = objectMapper.writeValueAsString(responseDebitCardInformation);
        wireMockRule.stubFor(
                get(urlEqualTo("/" + BASE_PATH + CARD_STATUS_BY_CLIENT.replace(ID_CLIENT_PLACEHOLDER, ID_CLIENT)))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody(responseStr)));

        Map<String, String> headers = Sample.getAuthorizationHeader();
        Either<DebitCardsError, CardStatus>  response = testedClass.getDebitCardStatusByIdClient(headers,ID_CLIENT);

        assertTrue(response.isRight());
        assertEquals(CREATION_DATE_CARD, response.get().getStatusDate());
    }

}
