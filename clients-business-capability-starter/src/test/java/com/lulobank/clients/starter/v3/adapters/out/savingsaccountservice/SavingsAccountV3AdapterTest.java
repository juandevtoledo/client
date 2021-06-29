package com.lulobank.clients.starter.v3.adapters.out.savingsaccountservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import com.lulobank.clients.starter.v3.adapters.out.savingsaccountservice.dto.SavingAccountCreated;
import com.lulobank.clients.v3.adapters.port.out.savingsaccount.error.SavingsAccountError;
import com.lulobank.clients.v3.adapters.port.out.savingsaccount.dto.SavingsAccountRequest;
import com.lulobank.clients.v3.adapters.port.out.savingsaccount.dto.SavingsAccountResponse;
import io.vavr.control.Either;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.HashMap;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.lulobank.clients.starter.v3.adapters.out.Constant.ACCOUNT_ID;
import static com.lulobank.clients.starter.v3.adapters.out.Constant.ID_CBS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;

public class SavingsAccountV3AdapterTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8982);
    private SavingsAccountV3Adapter testClass;
    private String url="http://localhost:8982";
    private ObjectMapper mapper = new ObjectMapper();
    private String idClient=UUID.randomUUID().toString();

    @Before
    public void setup(){
        idClient= UUID.randomUUID().toString();
        testClass=new SavingsAccountV3Adapter(url);
    }

    @Test
    public void createAccount() throws JsonProcessingException {
        SavingAccountCreated savingAccountCreated = savingAccountCreatedBuilder();
        String responseBody = mapper.writeValueAsString(savingAccountCreated);
        StubMapping stubMapping = wireMockRule.stubFor(
                post(urlEqualTo("/savingsaccounts/v3/client/"+idClient+"/create"))
                        .willReturn(aResponse()
                                .withStatus(201)
                                .withBody(responseBody)));
        SavingsAccountRequest savingsAccountRequest = new SavingsAccountRequest();
        savingsAccountRequest.setIdClient(idClient);
        Either<SavingsAccountError, SavingsAccountResponse> response= testClass.create(savingsAccountRequest,new HashMap<>());
        assertThat(response.isLeft(),is(false));
        assertThat(response.get().getAccountId(),is(ACCOUNT_ID));
        assertThat(response.get().getIdCbs(),is(ID_CBS));
    }

    @Test
    public void createAccountFailedSinceServiceError() throws JsonProcessingException {
        SavingAccountCreated savingAccountCreated = savingAccountCreatedBuilder();
        String responseBody = mapper.writeValueAsString(savingAccountCreated);
        StubMapping stubMapping = wireMockRule.stubFor(
                post(urlEqualTo("/savingsaccounts/v3/client/"+idClient+"/create"))
                        .willReturn(aResponse()
                                .withStatus(502)
                                .withBody(responseBody)));
        SavingsAccountRequest savingsAccountRequest = new SavingsAccountRequest();
        savingsAccountRequest.setIdClient(idClient);
        Either<SavingsAccountError, SavingsAccountResponse> response= testClass.create(savingsAccountRequest,new HashMap<>());
        assertThat(response.isLeft(),is(true));
    }

    @NotNull
    public SavingAccountCreated savingAccountCreatedBuilder() {
        SavingAccountCreated savingAccountCreated=new SavingAccountCreated();
        savingAccountCreated.setAccountId(ACCOUNT_ID);
        savingAccountCreated.setIdCbs(ID_CBS);
        return savingAccountCreated;
    }
}
