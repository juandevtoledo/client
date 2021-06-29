package com.lulobank.clients.sdk.operations.impl;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.lulobank.clients.sdk.operations.IClientInformationOperations;
import com.lulobank.clients.sdk.operations.dto.onboardingclients.ClientInformationByIdClient;
import com.lulobank.core.Response;
import com.lulobank.core.validations.ValidationResult;
import com.lulobank.utils.exception.ServiceException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RetrofitClientInformationOperationTest {
  @Rule public WireMockRule wireMockRule = new WireMockRule(8981);
  @Rule public ExpectedException exceptionRule = ExpectedException.none();
  private static final String ID_CLIENT = "05e1a61c-2fc1-4a6d-b7c3-11bb9ec4ff3d";
  private static final LocalDate BIRTH_DATE = LocalDate.parse("1999-01-01");
  private IClientInformationOperations testClass;

  public RetrofitClientInformationOperationTest() {
    testClass = new RetrofitClientInformationOperations("http://localhost:8981/");
  }

  @Test
  public void getClientInformationByIdSuccess() throws JsonProcessingException {

    ClientInformationByIdClient clientInformationByIdClient = new ClientInformationByIdClient();
    clientInformationByIdClient.setIdClient(ID_CLIENT);
    clientInformationByIdClient.setBirthDate(BIRTH_DATE);
    Response<ClientInformationByIdClient> response = new Response<>(clientInformationByIdClient);
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    wireMockRule.stubFor(
        get(urlMatching("/clients/idClient/".concat(ID_CLIENT)))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withBody(objectMapper.writeValueAsString(response))
                    .withHeader("Content-Type", "application/json")));

    Map<String, String> headers = new HashMap<>();
    ClientInformationByIdClient client =
        testClass.getAllClientInformationByIdClient(headers, ID_CLIENT);
    assertEquals("IdClient is Right", ID_CLIENT, client.getIdClient());
    assertEquals("BirthDate is Right", BIRTH_DATE, client.getBirthDate());
  }

  @Test()
  public void getClientInformationByIdClient_null_since_SecurityError()
      throws JsonProcessingException {
    wireMockRule.stubFor(
        get(urlMatching("/clients/idClient/".concat(ID_CLIENT)))
            .willReturn(
                aResponse().withStatus(401).withHeader("Content-Type", "application/json")));
    exceptionRule.expect(ServiceException.class);
    Map<String, String> headers = new HashMap<>();
    ClientInformationByIdClient client =
        testClass.getAllClientInformationByIdClient(headers, ID_CLIENT);
  }

  @Test()
  public void getClientInformationByIdClient_null_since_ClientNotFound()
      throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    ValidationResult validationResult = new ValidationResult("CLIENT_NOT_FOUND", "404");
    Response response = new Response(new ArrayList().add(validationResult));

    wireMockRule.stubFor(
        get(urlMatching("/clients/idClient/".concat(ID_CLIENT)))
            .willReturn(
                aResponse()
                    .withStatus(404)
                    .withBody(objectMapper.writeValueAsString(response))
                    .withHeader("Content-Type", "application/json")));
    exceptionRule.expect(ServiceException.class);
    Map<String, String> headers = new HashMap<>();
    ClientInformationByIdClient client =
        testClass.getAllClientInformationByIdClient(headers, ID_CLIENT);
  }
}
