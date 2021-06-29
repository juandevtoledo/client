package com.lulobank.clients.sdk.operations.impl;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.lulobank.clients.sdk.operations.dto.ClientInformationByIdCard;
import com.lulobank.clients.sdk.operations.dto.ClientInformationByIdClient;
import com.lulobank.clients.sdk.operations.dto.ClientInformationByIdClientContent;
import com.lulobank.clients.sdk.operations.dto.ClientInformationByPhone;
import com.lulobank.clients.sdk.operations.dto.ClientInformationByPhoneContent;
import com.lulobank.clients.sdk.operations.dto.ClientInformationByTypeResponse;
import com.lulobank.clients.sdk.operations.dto.ClientSuccessResult;
import com.lulobank.clients.sdk.operations.dto.DemographicInfoByIdClient;
import com.lulobank.clients.sdk.operations.dto.UpdateClientAddressRequest;
import com.lulobank.clients.sdk.operations.dto.VerifyEmailResponse;
import com.lulobank.clients.sdk.operations.exception.UpdateClientInformationException;
import com.lulobank.utils.exception.ServiceException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

@RunWith(MockitoJUnitRunner.class)
public class RetrofitClientOperationTest {

  private static final String ERROR_MESSAGE_FIND_CLIENT_BY_CARD_ID =
      "Error clients service getClientInformationByIdCardInternal";

  @Rule public WireMockRule wireMockRule = new WireMockRule(8981);

  @Rule public ExpectedException exceptionRule = ExpectedException.none();

  private static final String ID_CLIENT = "05e1a61c-2fc1-4a6d-b7c3-11bb9ec4ff3d";
  private static final String EMAIL = "email@test.com";
  private static final String PHONE_NUMBER = "573214477726";
  private static final String ID_CARD = "111222333";
  private static final String STATE_BLACKLIST = "NON_BLACKLISTED";
  private static final String RISK_BLACKLIST = "NO_RISK";
  private static final String BIOMETRIC_STATUS = "FINISHED";
  private static final String SEARCH_TYPE_EMAIL = "EMAIL";
  private static final String SEARCH_TYPE_PHONE = "PHONE_NUMBER";
  private static final String SEARCH_TYPE_ID_CLIENT = "ID_CLIENT";
  private static final String URL_RETROFIT = "http://localhost:8981/";
  private static final String URL_SERVICE_SEARCH_TYPE_PHONE =
      "/clients/searchType\\?searchType=PHONE_NUMBER&value=" + PHONE_NUMBER;
  private static final String URL_SERVICE_SEARCH_TYPE_ID_CLIENT =
      "/clients/searchType\\?searchType=ID_CLIENT&value=" + ID_CLIENT;
  private static final String URL_SERVICE_SEARCH_TYPE_EMAIL =
      "/clients/searchType\\?searchType=EMAIL&value=email%40test.com";

  private static final String URL_SERVICE_SEARCH_TYPE_INTERNAL_PHONE =
      "/clients/searchTypeInternal\\?searchType=PHONE_NUMBER&value=" + PHONE_NUMBER;
  private static final String URL_SERVICE_SEARCH_TYPE_INTERNAL_ID_CLIENT =
      "/clients/searchTypeInternal\\?searchType=ID_CLIENT&value=" + ID_CLIENT;
  private static final String URL_SERVICE_SEARCH_TYPE_INTERNAL_EMAIL =
      "/clients/searchTypeInternal\\?searchType=EMAIL&value=email%40test.com";

  private static final String CONTENT_TYPE = "Content-Type";
  private static final String APPLICATION_JSON = "application/json";
  private static final String ERROR_MESSAGE_FIND_CLIENT_BY_TYPE_SEARCH =
      "Error clients Service getClientByType";

  @Test
  public void validateGetClientInformationByIdSuccess() throws JsonProcessingException {
    RetrofitClientOperations retrofitClientOperations =
        new RetrofitClientOperations("http://localhost:8981/");

    ClientInformationByIdClient clientInformationByIdClient = new ClientInformationByIdClient();
    ClientInformationByIdClientContent clientInformationByIdClientContent =
        new ClientInformationByIdClientContent();
    clientInformationByIdClientContent.setIdClient("12345");
    clientInformationByIdClient.setContent(clientInformationByIdClientContent);
    ObjectMapper objectMapper = new ObjectMapper();

    wireMockRule.stubFor(
        get(urlMatching("/clients/idClient/123456"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withBody(objectMapper.writeValueAsString(clientInformationByIdClient))
                    .withHeader("Content-Type", "application/json")));

    Map<String, String> headers = new HashMap<>();
    // TODO: Set headers if needed e.g. headers.put("awesomeheader","12345");

    ClientInformationByIdClient resEntity =
        retrofitClientOperations.getClientByIdClient(new HashMap<>(), "123456");

    assertNotNull(resEntity);
  }

  @Test
  public void validateGetClientInformationByIdException() {
    RetrofitClientOperations retrofitClientOperations =
        new RetrofitClientOperations("http://localhost:8981/");

    wireMockRule.stubFor(
        get(urlMatching("/clients/idClient/123456"))
            .willReturn(aResponse().withFault(Fault.MALFORMED_RESPONSE_CHUNK)));

    exceptionRule.expect(ServiceException.class);
    exceptionRule.expectMessage("ERROR");
    ;

    Map<String, String> headers = new HashMap<>();
    // TODO: Set headers if needed e.g. headers.put("awesomeheader","12345");
    ClientInformationByIdClient resEntity =
        retrofitClientOperations.getClientByIdClient(new HashMap<>(), "123456");
  }

  @Test
  public void validateGetClientInformationByPhoneSuccess() throws JsonProcessingException {
    RetrofitClientOperations retrofitClientOperations =
        new RetrofitClientOperations("http://localhost:8981/");

    ClientInformationByPhone clientInformationByPhone = new ClientInformationByPhone();
    ClientInformationByPhoneContent clientInformationByPhoneContent =
        new ClientInformationByPhoneContent();
    clientInformationByPhoneContent.setIdClient("12345");
    clientInformationByPhone.setContent(clientInformationByPhoneContent);
    ObjectMapper objectMapper = new ObjectMapper();

    wireMockRule.stubFor(
        get(urlMatching("/clients/phonenumber\\?country=57&number=123456"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withBody(objectMapper.writeValueAsString(clientInformationByPhone))
                    .withHeader("Content-Type", "application/json")));

    Map<String, String> headers = new HashMap<>();
    // TODO: Set headers if needed e.g. headers.put("awesomeheader","12345");

    ClientInformationByPhone resEntity =
        retrofitClientOperations.getClientByPhoneNumber(new HashMap<>(), 57, "123456");

    assertNotNull(resEntity);
  }

  @Test
  public void validateGetClientInformationByPhoneInternalSuccess() throws JsonProcessingException {
    RetrofitClientOperations retrofitClientOperations =
        new RetrofitClientOperations("http://localhost:8981/");

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
        retrofitClientOperations.getClientByPhoneNumberInternal(new HashMap<>(), 57, "123456");

    assertNotNull(resEntity);
  }

  @Test
  public void validateGetClientInformationByPhoneException() {
    RetrofitClientOperations retrofitClientOperations =
        new RetrofitClientOperations("http://localhost:8981/");

    wireMockRule.stubFor(
        get(urlMatching("/clients/phonenumber\\?country=57&number=123456"))
            .willReturn(aResponse().withFault(Fault.MALFORMED_RESPONSE_CHUNK)));

    exceptionRule.expect(ServiceException.class);
    exceptionRule.expectMessage("ERROR");
    ;

    Map<String, String> headers = new HashMap<>();
    // TODO: Set headers if needed e.g. headers.put("awesomeheader","12345");
    ClientInformationByPhone resEntity =
        retrofitClientOperations.getClientByPhoneNumber(new HashMap<>(), 57, "123456");
  }

  @Test
  public void validateGetClientInformationByIdCardSuccess() throws JsonProcessingException {
    RetrofitClientOperations retrofitClientOperations =
        new RetrofitClientOperations("http://localhost:8981/");

    ClientInformationByIdCard clientInformationByIdCard = new ClientInformationByIdCard();
    clientInformationByIdCard.setIdCard("123456");
    ClientSuccessResult clientSuccessResult = new ClientSuccessResult(clientInformationByIdCard);

    ObjectMapper objectMapper = new ObjectMapper();

    wireMockRule.stubFor(
        get(urlMatching("/clients/idCard\\?idCard=123456"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withBody(objectMapper.writeValueAsString(clientSuccessResult))
                    .withHeader("Content-Type", "application/json")));

    Map<String, String> headers = new HashMap<>();
    // TODO: Set headers if needed e.g. headers.put("awesomeheader","12345");

    ClientInformationByIdCard resEntity =
        retrofitClientOperations.getClientInformationByIdCard(new HashMap<>(), "123456");

    assertNotNull(resEntity);
  }

  @Test
  public void validateGetClientInformationByIdCardException() {
    RetrofitClientOperations retrofitClientOperations =
        new RetrofitClientOperations("http://localhost:8981/");

    wireMockRule.stubFor(
        get(urlMatching("/clients/idCard\\?idCard=123456"))
            .willReturn(aResponse().withFault(Fault.MALFORMED_RESPONSE_CHUNK)));

    exceptionRule.expect(ServiceException.class);
    exceptionRule.expectMessage("ERROR");
    ;

    Map<String, String> headers = new HashMap<>();
    // TODO: Set headers if needed e.g. headers.put("awesomeheader","12345");
    ClientInformationByIdCard resEntity =
        retrofitClientOperations.getClientInformationByIdCard(new HashMap<>(), "123456");
  }

  @Test
  public void validateGetClientInformationByIdCardInternalSuccess() throws JsonProcessingException {
    RetrofitClientOperations retrofitClientOperations =
        new RetrofitClientOperations("http://localhost:8981/");

    ClientInformationByIdCard clientInformationByIdCard = new ClientInformationByIdCard();
    clientInformationByIdCard.setIdCard("123456");
    ClientSuccessResult clientSuccessResult = new ClientSuccessResult(clientInformationByIdCard);

    ObjectMapper objectMapper = new ObjectMapper();

    wireMockRule.stubFor(
        get(urlMatching("/clients/idCardInternal\\?idCard=123456"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withBody(objectMapper.writeValueAsString(clientSuccessResult))
                    .withHeader("Content-Type", "application/json")));

    Map<String, String> headers = new HashMap<>();
    // TODO: Set headers if needed e.g. headers.put("awesomeheader","12345");

    ClientInformationByIdCard resEntity =
        retrofitClientOperations.getClientInformationByIdCardInternal(new HashMap<>(), "123456");

    assertNotNull(resEntity);
  }

  @Test
  public void validateGetClientByTypeEmailSearchSuccess() throws JsonProcessingException {
    RetrofitClientOperations retrofitClientOperations = new RetrofitClientOperations(URL_RETROFIT);
    ClientInformationByTypeResponse clientInformationByTypeResponse =
        new ClientInformationByTypeResponse();

    clientInformationByTypeResponse.setIdClient(ID_CLIENT);
    clientInformationByTypeResponse.setEmailAddress(EMAIL);
    clientInformationByTypeResponse.setIdCard(ID_CARD);
    ClientSuccessResult clientSuccessResult =
        new ClientSuccessResult(clientInformationByTypeResponse);
    ObjectMapper objectMapper = new ObjectMapper();
    wireMockRule.stubFor(
        get(urlMatching(URL_SERVICE_SEARCH_TYPE_EMAIL))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.OK.value())
                    .withBody(objectMapper.writeValueAsString(clientSuccessResult))
                    .withHeader(CONTENT_TYPE, APPLICATION_JSON)));
    ClientInformationByTypeResponse resEntity =
        retrofitClientOperations.getClientByType(new HashMap<>(), SEARCH_TYPE_EMAIL, EMAIL);
    assertNotNull(resEntity);
    assertEquals(ID_CLIENT, resEntity.getIdClient());
    assertEquals(EMAIL, resEntity.getEmailAddress());
    assertEquals(ID_CARD, resEntity.getIdCard());
  }

  @Test
  public void validateGetClientByTypeInternalEmailSearchSuccess() throws JsonProcessingException {
    RetrofitClientOperations retrofitClientOperations = new RetrofitClientOperations(URL_RETROFIT);
    ClientInformationByTypeResponse clientInformationByTypeResponse =
        new ClientInformationByTypeResponse();

    clientInformationByTypeResponse.setIdClient(ID_CLIENT);
    clientInformationByTypeResponse.setEmailAddress(EMAIL);
    clientInformationByTypeResponse.setIdCard(ID_CARD);
    clientInformationByTypeResponse.setBiometricStatus(BIOMETRIC_STATUS);
    ClientSuccessResult clientSuccessResult =
        new ClientSuccessResult(clientInformationByTypeResponse);
    ObjectMapper objectMapper = new ObjectMapper();
    wireMockRule.stubFor(
        get(urlMatching(URL_SERVICE_SEARCH_TYPE_INTERNAL_EMAIL))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.OK.value())
                    .withBody(objectMapper.writeValueAsString(clientSuccessResult))
                    .withHeader(CONTENT_TYPE, APPLICATION_JSON)));
    ClientInformationByTypeResponse resEntity =
        retrofitClientOperations.getClientByTypeInternal(new HashMap<>(), SEARCH_TYPE_EMAIL, EMAIL);
    assertNotNull(resEntity);
    assertEquals(ID_CLIENT, resEntity.getIdClient());
    assertEquals(EMAIL, resEntity.getEmailAddress());
    assertEquals(ID_CARD, resEntity.getIdCard());
    assertEquals(BIOMETRIC_STATUS, resEntity.getBiometricStatus());
  }

  @Test
  public void validateGetClientByTypePhoneSearchSuccess() throws JsonProcessingException {
    RetrofitClientOperations retrofitClientOperations = new RetrofitClientOperations(URL_RETROFIT);
    ClientInformationByTypeResponse clientInformationByTypeResponse =
        new ClientInformationByTypeResponse();
    clientInformationByTypeResponse.setIdClient(ID_CLIENT);
    clientInformationByTypeResponse.setEmailAddress(EMAIL);
    clientInformationByTypeResponse.setIdCard(ID_CARD);
    ClientSuccessResult clientSuccessResult =
        new ClientSuccessResult(clientInformationByTypeResponse);
    ObjectMapper objectMapper = new ObjectMapper();
    wireMockRule.stubFor(
        get(urlMatching(URL_SERVICE_SEARCH_TYPE_PHONE))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.OK.value())
                    .withBody(objectMapper.writeValueAsString(clientSuccessResult))
                    .withHeader(CONTENT_TYPE, APPLICATION_JSON)));
    ClientInformationByTypeResponse resEntity =
        retrofitClientOperations.getClientByType(new HashMap<>(), SEARCH_TYPE_PHONE, PHONE_NUMBER);
    assertNotNull(resEntity);
    assertEquals(ID_CLIENT, resEntity.getIdClient());
    assertEquals(EMAIL, resEntity.getEmailAddress());
    assertEquals(ID_CARD, resEntity.getIdCard());
  }

  @Test
  public void validateGetClientByTypeInternalPhoneSearchSuccess() throws JsonProcessingException {
    RetrofitClientOperations retrofitClientOperations = new RetrofitClientOperations(URL_RETROFIT);
    ClientInformationByTypeResponse clientInformationByTypeResponse =
        new ClientInformationByTypeResponse();
    clientInformationByTypeResponse.setIdClient(ID_CLIENT);
    clientInformationByTypeResponse.setEmailAddress(EMAIL);
    clientInformationByTypeResponse.setIdCard(ID_CARD);
    ClientSuccessResult clientSuccessResult =
        new ClientSuccessResult(clientInformationByTypeResponse);
    ObjectMapper objectMapper = new ObjectMapper();
    wireMockRule.stubFor(
        get(urlMatching(URL_SERVICE_SEARCH_TYPE_INTERNAL_PHONE))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.OK.value())
                    .withBody(objectMapper.writeValueAsString(clientSuccessResult))
                    .withHeader(CONTENT_TYPE, APPLICATION_JSON)));
    ClientInformationByTypeResponse resEntity =
        retrofitClientOperations.getClientByTypeInternal(
            new HashMap<>(), SEARCH_TYPE_PHONE, PHONE_NUMBER);
    assertNotNull(resEntity);
    assertEquals(ID_CLIENT, resEntity.getIdClient());
    assertEquals(EMAIL, resEntity.getEmailAddress());
    assertEquals(ID_CARD, resEntity.getIdCard());
  }

  @Test
  public void validateGetClientByTypeIdClientSearchSuccess() throws JsonProcessingException {
    RetrofitClientOperations retrofitClientOperations = new RetrofitClientOperations(URL_RETROFIT);
    ClientInformationByTypeResponse clientInformationByTypeResponse =
        new ClientInformationByTypeResponse();
    clientInformationByTypeResponse.setIdClient(ID_CLIENT);
    clientInformationByTypeResponse.setEmailAddress(EMAIL);
    clientInformationByTypeResponse.setIdCard(ID_CARD);

    ClientSuccessResult clientSuccessResult =
        new ClientSuccessResult(clientInformationByTypeResponse);
    ObjectMapper objectMapper = new ObjectMapper();
    wireMockRule.stubFor(
        get(urlMatching(URL_SERVICE_SEARCH_TYPE_ID_CLIENT))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.OK.value())
                    .withBody(objectMapper.writeValueAsString(clientSuccessResult))
                    .withHeader(CONTENT_TYPE, APPLICATION_JSON)));
    ClientInformationByTypeResponse resEntity =
        retrofitClientOperations.getClientByType(new HashMap<>(), SEARCH_TYPE_ID_CLIENT, ID_CLIENT);
    assertNotNull(resEntity);
    assertEquals(ID_CLIENT, resEntity.getIdClient());
    assertEquals(EMAIL, resEntity.getEmailAddress());
    assertEquals(ID_CARD, resEntity.getIdCard());
  }

  @Test
  public void validateGetClientByTypeInternalIdClientSearchSuccess()
      throws JsonProcessingException {
    RetrofitClientOperations retrofitClientOperations = new RetrofitClientOperations(URL_RETROFIT);
    ClientInformationByTypeResponse clientInformationByTypeResponse =
        new ClientInformationByTypeResponse();
    clientInformationByTypeResponse.setIdClient(ID_CLIENT);
    clientInformationByTypeResponse.setEmailAddress(EMAIL);
    clientInformationByTypeResponse.setIdCard(ID_CARD);
    clientInformationByTypeResponse.setRiskLevel(RISK_BLACKLIST);
    clientInformationByTypeResponse.setBlacklistState(STATE_BLACKLIST);

    ClientSuccessResult clientSuccessResult =
        new ClientSuccessResult(clientInformationByTypeResponse);
    ObjectMapper objectMapper = new ObjectMapper();
    wireMockRule.stubFor(
        get(urlMatching(URL_SERVICE_SEARCH_TYPE_INTERNAL_ID_CLIENT))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.OK.value())
                    .withBody(objectMapper.writeValueAsString(clientSuccessResult))
                    .withHeader(CONTENT_TYPE, APPLICATION_JSON)));
    ClientInformationByTypeResponse resEntity =
        retrofitClientOperations.getClientByTypeInternal(
            new HashMap<>(), SEARCH_TYPE_ID_CLIENT, ID_CLIENT);
    assertNotNull(resEntity);
    assertEquals(ID_CLIENT, resEntity.getIdClient());
    assertEquals(EMAIL, resEntity.getEmailAddress());
    assertEquals(ID_CARD, resEntity.getIdCard());
    assertEquals(RISK_BLACKLIST, resEntity.getRiskLevel());
    assertEquals(STATE_BLACKLIST, resEntity.getBlacklistState());
  }

  @Test
  public void validateGetClientByTypeException() {
    RetrofitClientOperations retrofitClientOperations = new RetrofitClientOperations(URL_RETROFIT);
    wireMockRule.stubFor(
        get(urlMatching(URL_SERVICE_SEARCH_TYPE_EMAIL))
            .willReturn(aResponse().withFault(Fault.MALFORMED_RESPONSE_CHUNK)));
    exceptionRule.expect(ServiceException.class);
    exceptionRule.expectMessage(ERROR_MESSAGE_FIND_CLIENT_BY_TYPE_SEARCH);
    ClientInformationByTypeResponse resEntity =
        retrofitClientOperations.getClientByType(new HashMap<>(), SEARCH_TYPE_EMAIL, EMAIL);
  }

  @Test
  public void validateGetClientByTypeInternalException() {
    RetrofitClientOperations retrofitClientOperations = new RetrofitClientOperations(URL_RETROFIT);
    wireMockRule.stubFor(
        get(urlMatching(URL_SERVICE_SEARCH_TYPE_INTERNAL_EMAIL))
            .willReturn(aResponse().withFault(Fault.MALFORMED_RESPONSE_CHUNK)));
    exceptionRule.expect(ServiceException.class);
    exceptionRule.expectMessage(ERROR_MESSAGE_FIND_CLIENT_BY_TYPE_SEARCH);
    ClientInformationByTypeResponse resEntity =
        retrofitClientOperations.getClientByTypeInternal(new HashMap<>(), SEARCH_TYPE_EMAIL, EMAIL);
  }

  @Test
  public void validateGetClientByTypeInternalErrorException() {
    RetrofitClientOperations retrofitClientOperations = new RetrofitClientOperations(URL_RETROFIT);
    wireMockRule.stubFor(
        get(urlMatching(URL_SERVICE_SEARCH_TYPE_PHONE))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .withHeader(CONTENT_TYPE, APPLICATION_JSON)));
    exceptionRule.expect(ServiceException.class);
    exceptionRule.expectMessage(StringUtils.EMPTY);
    ClientInformationByTypeResponse resEntity =
        retrofitClientOperations.getClientByType(new HashMap<>(), SEARCH_TYPE_PHONE, PHONE_NUMBER);
  }

  @Test
  public void validateGetClientInformationByIdCardInternalException() {
    RetrofitClientOperations retrofitClientOperations =
        new RetrofitClientOperations("http://localhost:8981/");

    wireMockRule.stubFor(
        get(urlMatching("/clients/idCardInternal\\?idCard=123456"))
            .willReturn(aResponse().withFault(Fault.MALFORMED_RESPONSE_CHUNK)));

    exceptionRule.expect(ServiceException.class);
    exceptionRule.expectMessage(ERROR_MESSAGE_FIND_CLIENT_BY_CARD_ID);
    ;

    Map<String, String> headers = new HashMap<>();
    // TODO: Set headers if needed e.g. headers.put("awesomeheader","12345");
    ClientInformationByIdCard resEntity =
        retrofitClientOperations.getClientInformationByIdCardInternal(new HashMap<>(), "123456");
  }

  @Test
  public void validateGetClientInformationByIdCardInternalStatusCodeException()
      throws JsonProcessingException {
    RetrofitClientOperations retrofitClientOperations =
        new RetrofitClientOperations("http://localhost:8981/");

    wireMockRule.stubFor(
        get(urlMatching("/clients/idCardInternal\\?idCard=123456"))
            .willReturn(
                aResponse().withStatus(500).withHeader("Content-Type", "application/json")));

    exceptionRule.expect(ServiceException.class);
    exceptionRule.expectMessage(StringUtils.EMPTY);

    Map<String, String> headers = new HashMap<>();
    // TODO: Set headers if needed e.g. headers.put("awesomeheader","12345");
    ClientInformationByIdCard resEntity =
        retrofitClientOperations.getClientInformationByIdCardInternal(new HashMap<>(), "123456");
  }

  @Test
  public void validateVerifyEmailClientInformationSuccess() throws JsonProcessingException {
    RetrofitClientOperations retrofitClientOperations =
        new RetrofitClientOperations("http://localhost:8981/");
    VerifyEmailResponse verifyEmailResponse =
        new VerifyEmailResponse("lulobanky24@yopmail.com", true);
    ObjectMapper objectMapper = new ObjectMapper();

    wireMockRule.stubFor(
        get(urlMatching("/clients/profile/email/verify/lulobanky24@yopmail.com"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withBody(objectMapper.writeValueAsString(verifyEmailResponse))
                    .withHeader("Content-Type", "application/json")));

    Map<String, String> headers = new HashMap<>();

    VerifyEmailResponse resEntity =
        retrofitClientOperations.verifyEmailClientInformation(
            new HashMap<>(), "lulobanky24@yopmail.com");
    assertNotNull(resEntity);
  }

  @Test
  public void validateUpdateClientInformationSuccess() throws JsonProcessingException {

    String stubUrl = "/clients/profile/update";
    RetrofitClientOperations retrofitClientOperations =
        new RetrofitClientOperations("http://localhost:8981/");
    UpdateClientAddressRequest updateClientRequest =
        getUpdateClientRequest(
            "18e65463-3144-4a69-9e7c-a8aab6c48b2d", "Calle " + "25 # 67 22", "Bogota", "Bogota");

    wireMockRule.stubFor(
        post(urlMatching(stubUrl))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.OK.value())
                    .withBody("")
                    .withHeader("Content-Type", "application/json")));

    assertTrue(
        retrofitClientOperations.updateClientInformation(new HashMap<>(), updateClientRequest));
  }

  @Test
  public void validateUpdateClientInformationV2Success() throws JsonProcessingException {

    String stubUrl = "/clients/18e65463-3144-4a69-9e7c-a8aab6c48b2d/profile";
    RetrofitClientOperations retrofitClientOperations =
            new RetrofitClientOperations("http://localhost:8981/");
    UpdateClientAddressRequest updateClientRequest =
            getUpdateClientRequest(
                    "18e65463-3144-4a69-9e7c-a8aab6c48b2d", "Calle " + "25 # 67 22", "Bogota", "Bogota");

    wireMockRule.stubFor(
            put(urlMatching(stubUrl))
                    .willReturn(
                            aResponse()
                                    .withStatus(HttpStatus.OK.value())
                                    .withBody("")
                                    .withHeader("Content-Type", "application/json")));

    assertTrue(
            retrofitClientOperations.updateClientInformationV2(new HashMap<>(), updateClientRequest,"18e65463-3144-4a69-9e7c-a8aab6c48b2d").get());
  }
  @Test
  public void validateUpdateClientInformationV2Exception() throws JsonProcessingException {

    String stubUrl = "/clients/18e65463-3144-4a69-9e7c-a8aab6c48b2d/profile";
    RetrofitClientOperations retrofitOtpOperations =
            new RetrofitClientOperations("http://localhost:8981");
    UpdateClientAddressRequest updateClientRequest =
            getUpdateClientRequest(
                    "18e65463-3144-4a69-9e7c-a8aab6c48b2d", "Calle 25 # 67 22", "Bogotá", "Bogotá");

    wireMockRule.stubFor(
            put(urlMatching(stubUrl))
                    .willReturn(
                            aResponse()
                                    .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                    .withBody("")
                                    .withHeader("Content-Type", "application/json")));
    exceptionRule.expect(UpdateClientInformationException.class);
    exceptionRule.expectMessage("");

    retrofitOtpOperations.updateClientInformationV2(new HashMap<>(), updateClientRequest,"18e65463-3144-4a69-9e7c-a8aab6c48b2d").get();
  }

  @Test
  public void validateUpdateClientInformationException() throws JsonProcessingException {

    String stubUrl = "/clients/profile/update";
    RetrofitClientOperations retrofitOtpOperations =
            new RetrofitClientOperations("http://localhost:8981");
    UpdateClientAddressRequest updateClientRequest =
            getUpdateClientRequest(
                    "18e65463-3144-4a69-9e7c-a8aab6c48b2d", "Calle 25 # 67 22", "Bogotá", "Bogotá");

    wireMockRule.stubFor(
            post(urlMatching(stubUrl))
                    .willReturn(
                            aResponse()
                                    .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                    .withBody("")
                                    .withHeader("Content-Type", "application/json")));
    exceptionRule.expect(ServiceException.class);
    exceptionRule.expectMessage("");

    retrofitOtpOperations.updateClientInformation(new HashMap<>(), updateClientRequest);
  }

  @Test
  public void validateGetDemographicInfoByIdClientSuccess() throws JsonProcessingException {
    RetrofitClientOperations retrofitClientOperations =
        new RetrofitClientOperations("http://localhost:8981/");
    DemographicInfoByIdClient demographicInfoByIdClient = new DemographicInfoByIdClient();
    demographicInfoByIdClient.setIdClient("123456");
    demographicInfoByIdClient.setName("NameClientDemographic");
    demographicInfoByIdClient.setLastName("LastNameClientDemographic");
    demographicInfoByIdClient.setAddress("");
    demographicInfoByIdClient.setDepartment("Bogota");
    demographicInfoByIdClient.setCity("Bogota");
    ObjectMapper objectMapper = new ObjectMapper();

    wireMockRule.stubFor(
        get(urlMatching("/clients/demographic/123456"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.OK.value())
                    .withBody(objectMapper.writeValueAsString(demographicInfoByIdClient))
                    .withHeader("Content-Type", "application/json")));

    DemographicInfoByIdClient resEntity =
        retrofitClientOperations.getDemographicInfoByClient(
            new HashMap<>(), demographicInfoByIdClient.getIdClient());
  }

  @Test
  public void validateGetDemographicInfoByIdClientException() {
    RetrofitClientOperations retrofitClientOperations =
        new RetrofitClientOperations("http://localhost:8981/");

    wireMockRule.stubFor(
        get(urlMatching("/clients/demographic/123456"))
            .willReturn(aResponse().withFault(Fault.MALFORMED_RESPONSE_CHUNK)));

    exceptionRule.expect(ServiceException.class);
    exceptionRule.expectMessage("ERROR");

    DemographicInfoByIdClient resEntity =
        retrofitClientOperations.getDemographicInfoByClient(new HashMap<>(), "123456");
  }

  private UpdateClientAddressRequest getUpdateClientRequest(
      String idClient, String address, String department, String city) {
    UpdateClientAddressRequest updateClientRequest = new UpdateClientAddressRequest();
    updateClientRequest.setIdClient(idClient);
    updateClientRequest.setAddress(address);
    updateClientRequest.setDepartment(department);
    updateClientRequest.setCity(city);
    return updateClientRequest;
  }
}
