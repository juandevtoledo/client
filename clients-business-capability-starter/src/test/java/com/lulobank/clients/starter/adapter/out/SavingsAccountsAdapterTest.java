package com.lulobank.clients.starter.adapter.out;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.http.Fault;
import com.lulobank.clients.services.application.port.out.savingsaccounts.error.SavingsAccountsError;
import com.lulobank.clients.services.application.port.out.savingsaccounts.error.SavingsAccountsErrorStatus;
import com.lulobank.clients.services.application.port.out.savingsaccounts.model.SavingAccount;
import com.lulobank.clients.starter.adapter.BaseUnitTest;
import com.lulobank.clients.starter.adapter.Sample;
import com.lulobank.clients.starter.adapter.out.savingsaccounts.SavingsAccountsAdapter;
import com.lulobank.clients.starter.adapter.out.savingsaccounts.SavingsAccountsClient;
import com.lulobank.clients.starter.adapter.out.savingsaccounts.dto.ResponseSavingAccountType;
import io.vavr.control.Either;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;

import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.lulobank.clients.starter.adapter.Constant.ID_CLIENT;
import static com.lulobank.clients.starter.adapter.Constant.ID_SAVINGS_ACCOUNT;
import static com.lulobank.clients.starter.adapter.out.savingsaccounts.SavingsAccountsClient.ACCOUNT_BY_CLIENT_ZENDESK;
import static com.lulobank.clients.starter.adapter.out.savingsaccounts.SavingsAccountsClient.BASE_PATH;
import static com.lulobank.clients.starter.adapter.out.savingsaccounts.SavingsAccountsClient.ID_CLIENT_PLACEHOLDER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SavingsAccountsAdapterTest extends BaseUnitTest {
    private SavingsAccountsAdapter testedClass;

    @Before
    public void init() {
        testedClass = new SavingsAccountsAdapter(new SavingsAccountsClient("http://localhost:8082"));
    }

    @Test
    public void shouldReturnSavingAccount() throws JsonProcessingException {
        ResponseSavingAccountType responseSavingAccountType = Sample.getResponseSavingAccountType();
        String responseStr = objectMapper.writeValueAsString(responseSavingAccountType);
        wireMockRule.stubFor(
                get(urlEqualTo("/" + BASE_PATH + ACCOUNT_BY_CLIENT_ZENDESK.replace(ID_CLIENT_PLACEHOLDER, ID_CLIENT)))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody(responseStr)));

        Map<String, String> headers = Sample.getAuthorizationHeader();
        Either<SavingsAccountsError, SavingAccount> response = testedClass.getSavingsAccountsByIdClient(headers,ID_CLIENT);

        assertTrue(response.isRight());
        assertEquals(ID_SAVINGS_ACCOUNT, response.get().getIdSavingAccount());
    }

    @Test
    public void shouldReturnErrorConnection() {
        wireMockRule.stubFor(
                get(urlEqualTo("/" + BASE_PATH + ACCOUNT_BY_CLIENT_ZENDESK.replace(ID_CLIENT_PLACEHOLDER, ID_CLIENT)))
                        .willReturn(aResponse()
                                .withStatus(500)
                                .withHeader("Content-Type", "application/json")));

        Map<String, String> headers = Sample.getAuthorizationHeader();
        Either<SavingsAccountsError, SavingAccount> response = testedClass.getSavingsAccountsByIdClient(headers,ID_CLIENT);

        assertTrue(response.isLeft());
        assertEquals(SavingsAccountsErrorStatus.CLI_110.name(), response.getLeft().getBusinessCode());
    }

    @Test
    public void shouldReturnNotFound()  {
        wireMockRule.stubFor(
                get(urlEqualTo("/" + BASE_PATH + ACCOUNT_BY_CLIENT_ZENDESK.replace(ID_CLIENT_PLACEHOLDER, ID_CLIENT)))
                        .willReturn(aResponse()
                                .withStatus(404)
                                .withHeader("Content-Type", "application/json")));

        Map<String, String> headers = Sample.getAuthorizationHeader();
        Either<SavingsAccountsError, SavingAccount> response = testedClass.getSavingsAccountsByIdClient(headers,ID_CLIENT);

        assertTrue(response.isLeft());
        assertEquals(SavingsAccountsErrorStatus.CLI_111.name(), response.getLeft().getBusinessCode());
    }

    @Test
    public void shouldReturnConnectionErrorException()  {
        wireMockRule.stubFor(
                get(urlEqualTo("/" + BASE_PATH + ACCOUNT_BY_CLIENT_ZENDESK.replace(ID_CLIENT_PLACEHOLDER, ID_CLIENT)))
                        .willReturn(aResponse().withFault(Fault.MALFORMED_RESPONSE_CHUNK)));

        Map<String, String> headers = Sample.getAuthorizationHeader();
        Either<SavingsAccountsError, SavingAccount> response = testedClass.getSavingsAccountsByIdClient(headers,ID_CLIENT);

        assertTrue(response.isLeft());
        assertEquals(SavingsAccountsErrorStatus.CLI_110.name(), response.getLeft().getBusinessCode());
    }


}
