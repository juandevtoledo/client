package com.lulobank.clients.sdk.operations.impl;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.junit.Assert.assertNotNull;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.lulobank.clients.sdk.operations.dto.ClientInformationByIdCard;
import com.lulobank.clients.sdk.operations.dto.ClientInformationByPhone;
import com.lulobank.clients.sdk.operations.dto.ClientInformationByPhoneContent;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RetrofitClientOperationOAuth2Test {
  @Rule public WireMockRule wireMockRule = new WireMockRule(8981);
  @Rule public WireMockRule wireMockRuleAuthServer = new WireMockRule(9999);
  @Rule public ExpectedException exceptionRule = ExpectedException.none();

  @Before
  public void setUp() throws Exception {
    CognitoResponse cognitoResponse =
        new CognitoResponse(
            "eyJraWQiOiJFc1BWR2pEU0FKNkJGRGFHVjArRlZ2aEtWU0ZPMVdtRUNTZnBKV2ZJWnFVPSIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiJicHUyZ3JocG9mam5qZDljc2k3Z204YTV2IiwidG9rZW5fdXNlIjoiYWNjZXNzIiwic2NvcGUiOiJpbnRlcm5hbF9hcGlcL2ludGVybmFsX3RyYW5zYWN0aW9ucyIsImF1dGhfdGltZSI6MTU3OTE5MjMzNiwiaXNzIjoiaHR0cHM6XC9cL2NvZ25pdG8taWRwLnVzLWVhc3QtMS5hbWF6b25hd3MuY29tXC91cy1lYXN0LTFfeFhhNVExeEhvIiwiZXhwIjoxNTc5MTk1OTM2LCJpYXQiOjE1NzkxOTIzMzYsInZlcnNpb24iOjIsImp0aSI6IjBlNDk4NzkxLWZkMDMtNDAxMC04NTY1LWU1OGE4NzYyM2U1ZSIsImNsaWVudF9pZCI6ImJwdTJncmhwb2ZqbmpkOWNzaTdnbThhNXYifQ.QztDEtnJRanOCoOyyOKopnXtt-paA_Cj7s6fMLOT_6DCQew9bnt0FFdo35nRrXTpumI-RPT23_YLoWXkvG4lSF2snGkSkZvqrpeOC6pVoU83GTy_fSDQ6Ks5G5S8BEBp8ccc2E_3dWJo-WeQTWV4Jsqp2SM61GNulTomRATsn1eUiMt03f1yTQ27b_n2MClceaXOf3GVs9YZoenWgN587su5LXrItTu-2Mv2evGLikRxWnFLG-yg6juidF0A2SAdjwqZbBdllEaUn5D9BJ1Vl5b2w7NcFE6T7kW9hKlbhc_6RP_31mFY_OainK0egdT-BB_lqZVrvblDpRvxMWYq8Q",
            3600,
            "Bearer");
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

    wireMockRuleAuthServer.stubFor(
        post(urlMatching("/token"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withBody(objectMapper.writeValueAsString(cognitoResponse))
                    .withHeader("Content-Type", "application/json")));
  }

  @Test
  public void validateGetClientInformationByPhoneInternalSuccess() throws JsonProcessingException {
    RetrofitClientsOperationsOAuth2 retrofitClientsOperationsOAuth2 =
        new RetrofitClientsOperationsOAuth2(
            "http://localhost:8981",
            "cliendId",
            "clientSecret",
            "http://localhost:9999/",
            new TokenManager());

    ClientInformationByPhone clientInformationByPhone = new ClientInformationByPhone();
    ClientInformationByPhoneContent clientInformationByPhoneContent =
        new ClientInformationByPhoneContent();
    clientInformationByPhoneContent.setIdClient("12345");
    clientInformationByPhone.setContent(clientInformationByPhoneContent);
    ObjectMapper objectMapper = new ObjectMapper();

    wireMockRule.stubFor(
        get(urlMatching("/clients/internalPhonenumber\\?country=57&number=123456"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withBody(objectMapper.writeValueAsString(clientInformationByPhone))
                    .withHeader("Content-Type", "application/json")));

    Map<String, String> headers = new HashMap<>();
    // TODO: Set headers if needed e.g. headers.put("awesomeheader","12345");

    ClientInformationByPhone resEntity =
        retrofitClientsOperationsOAuth2.getClientByPhoneNumberInternal(
            new HashMap<>(), 57, "123456");

    assertNotNull(resEntity);
  }

  @Test
  public void validateGetClientInformationByIdCardInternalSuccess() throws JsonProcessingException {
    RetrofitClientsOperationsOAuth2 retrofitClientsOperationsOAuth2 =
        new RetrofitClientsOperationsOAuth2(
            "http://localhost:8981",
            "cliendId",
            "clientSecret",
            "http://localhost:9999/",
            new TokenManager());

    ClientInformationByPhone clientInformationByPhone = new ClientInformationByPhone();
    ClientInformationByPhoneContent clientInformationByPhoneContent =
        new ClientInformationByPhoneContent();
    clientInformationByPhoneContent.setIdClient("12345");
    clientInformationByPhone.setContent(clientInformationByPhoneContent);
    ObjectMapper objectMapper = new ObjectMapper();

    wireMockRule.stubFor(
        get(urlMatching("/clients/idCardInternal\\?idCard=123456"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withBody(objectMapper.writeValueAsString(clientInformationByPhone))
                    .withHeader("Content-Type", "application/json")));

    Map<String, String> headers = new HashMap<>();
    // TODO: Set headers if needed e.g. headers.put("awesomeheader","12345");

    ClientInformationByIdCard resEntity =
        retrofitClientsOperationsOAuth2.getClientInformationByIdCardInternal(
            new HashMap<>(), "123456");

    assertNotNull(resEntity);
  }
}
