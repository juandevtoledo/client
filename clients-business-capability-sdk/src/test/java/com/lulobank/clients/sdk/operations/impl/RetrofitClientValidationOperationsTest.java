package com.lulobank.clients.sdk.operations.impl;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.lulobank.utils.exception.ServiceException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class RetrofitClientValidationOperationsTest {

  @Rule public WireMockRule wireMockRule = new WireMockRule(WireMockConfiguration.wireMockConfig().dynamicPort());

  @Rule public ExpectedException exceptionRule = ExpectedException.none();

  private String URL;

  @Before
  public void init(){
    URL = "http://localhost:"+wireMockRule.port()+"/clients/";
  }

  @Test
  public void validateEmailSuccess() {
    RetrofitClientValidationOperations retrofitClientValidationOperations =
        new RetrofitClientValidationOperations(URL);

    wireMockRule.stubFor(
        get(urlMatching(
                "/clients/validations/email/([\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w])"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withBody("")
                    .withHeader("Content-Type", "application/json")));

    Map<String, String> headers = new HashMap<>();
    // TODO: Set headers if needed e.g. headers.put("awesomeheader","12345");

    ResponseEntity<String> resEntity =
        retrofitClientValidationOperations.validateEmail(headers, "mail@mail.com");

    assertNull(resEntity.getBody());
  }

  @Test
  public void validateEmailServiceException() {
    RetrofitClientValidationOperations retrofitClientValidationOperations =
        new RetrofitClientValidationOperations(URL);

    wireMockRule.stubFor(
        get(urlMatching(
                "/clients/validations/email/([\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w])"))
            .willReturn(aResponse().withFault(Fault.EMPTY_RESPONSE)));

    exceptionRule.expect(ServiceException.class);
    exceptionRule.expectMessage("ERROR");

    Map<String, String> headers = new HashMap<>();
    // TODO: Set headers if needed e.g. headers.put("awesomeheader","12345");
    ResponseEntity<String> resEntity =
        retrofitClientValidationOperations.validateEmail(headers, "mail@mail.com");
  }

  @Test()
  public void validateEmailNotFound() {
    RetrofitClientValidationOperations retrofitClientValidationOperations =
        new RetrofitClientValidationOperations(URL);

    wireMockRule.stubFor(
        get(urlMatching(
                "/clients/validations/email/([\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w])"))
            .willReturn(
                aResponse()
                    .withStatus(404)
                    .withBody("")
                    .withHeader("Content-Type", "application/json")));

    exceptionRule.expect(ServiceException.class);
    exceptionRule.expectMessage("");

    Map<String, String> headers = new HashMap<>();
    // TODO: Set headers if needed e.g. headers.put("awesomeheader","12345");
    ResponseEntity<String> resEntity =
        retrofitClientValidationOperations.validateEmail(headers, "mail@mail.com");
  }

  @Test()
  public void validateEmailUnauthorized() {
    RetrofitClientValidationOperations retrofitClientValidationOperations =
        new RetrofitClientValidationOperations(URL);

    wireMockRule.stubFor(
        get(urlMatching(
                "/clients/validations/email/([\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w])"))
            .willReturn(
                aResponse()
                    .withStatus(401)
                    .withBody("")
                    .withHeader("Content-Type", "application/json")));

    exceptionRule.expect(ServiceException.class);
    exceptionRule.expectMessage("");
    ;

    Map<String, String> headers = new HashMap<>();
    // TODO: Set headers if needed e.g. headers.put("awesomeheader","12345");
    ResponseEntity<String> resEntity =
        retrofitClientValidationOperations.validateEmail(headers, "mail@mail.com");
  }

  @Test()
  public void validateEmailForbidden() {
    RetrofitClientValidationOperations retrofitClientValidationOperations =
        new RetrofitClientValidationOperations(URL);

    wireMockRule.stubFor(
        get(urlMatching(
                "/clients/validations/email/([\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w])"))
            .willReturn(
                aResponse()
                    .withStatus(403)
                    .withBody("")
                    .withHeader("Content-Type", "application/json")));

    exceptionRule.expect(ServiceException.class);
    exceptionRule.expectMessage("");
    ;

    Map<String, String> headers = new HashMap<>();
    // TODO: Set headers if needed e.g. headers.put("awesomeheader","12345");
    ResponseEntity<String> resEntity =
        retrofitClientValidationOperations.validateEmail(headers, "mail@mail.com");
  }

  @Test
  public void validatePhoneSuccess() {

    RetrofitClientValidationOperations retrofitClientValidationOperations =
        new RetrofitClientValidationOperations(URL);

    wireMockRule.stubFor(
        get(urlEqualTo("/clients/validations/phonenumber?country=57&number=3182062767"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withBody("")
                    .withHeader("Content-Type", "application/json")));

    Map<String, String> headers = new HashMap<>();
    // TODO: Set headers if needed e.g. headers.put("awesomeheader","12345");

    ResponseEntity<String> resEntity =
        retrofitClientValidationOperations.validatePhone(
            headers, 57, BigInteger.valueOf(3182062767L));

    assertNull(resEntity.getBody());
  }

  @Test
  public void validatePhoneServiceException() {
    RetrofitClientValidationOperations retrofitClientValidationOperations =
        new RetrofitClientValidationOperations(URL);

    wireMockRule.stubFor(
        get(urlEqualTo("/clients/validations/phonenumber?country=57&number=3182062767"))
            .willReturn(aResponse().withFault(Fault.EMPTY_RESPONSE)));

    exceptionRule.expect(ServiceException.class);
    exceptionRule.expectMessage("ERROR");

    Map<String, String> headers = new HashMap<>();
    // TODO: Set headers if needed e.g. headers.put("awesomeheader","12345");
    retrofitClientValidationOperations.validatePhone(headers, 57, new BigInteger("3182062767"));
  }

  @Test
  public void validatePhoneNotFound() {

    RetrofitClientValidationOperations retrofitClientValidationOperations =
        new RetrofitClientValidationOperations(URL);

    wireMockRule.stubFor(
        get(urlEqualTo("/clients/validations/phonenumber?country=57&number=3182062767"))
            .willReturn(
                aResponse()
                    .withStatus(404)
                    .withBody("")
                    .withHeader("Content-Type", "application/json")));

    exceptionRule.expect(ServiceException.class);
    exceptionRule.expectMessage("");
    ;

    Map<String, String> headers = new HashMap<>();
    // TODO: Set headers if needed e.g. headers.put("awesomeheader","12345");
    ResponseEntity<String> resEntity =
        retrofitClientValidationOperations.validatePhone(
            headers, 57, BigInteger.valueOf(3182062767L));
  }

  @Test
  public void validatePhoneUnauthorized() {

    RetrofitClientValidationOperations retrofitClientValidationOperations =
        new RetrofitClientValidationOperations(URL);

    wireMockRule.stubFor(
        get(urlEqualTo("/clients/validations/phonenumber?country=57&number=3182062767"))
            .willReturn(
                aResponse()
                    .withStatus(401)
                    .withBody("")
                    .withHeader("Content-Type", "application/json")));

    exceptionRule.expect(ServiceException.class);
    exceptionRule.expectMessage("");
    ;

    Map<String, String> headers = new HashMap<>();
    // TODO: Set headers if needed e.g. headers.put("awesomeheader","12345");
    ResponseEntity<String> resEntity =
        retrofitClientValidationOperations.validatePhone(
            headers, 57, BigInteger.valueOf(3182062767L));
  }

  @Test
  public void validatePhoneForbidden() {

    RetrofitClientValidationOperations retrofitClientValidationOperations =
        new RetrofitClientValidationOperations(URL);

    wireMockRule.stubFor(
        get(urlEqualTo("/clients/validations/phonenumber?country=57&number=3182062767"))
            .willReturn(
                aResponse()
                    .withStatus(403)
                    .withBody("")
                    .withHeader("Content-Type", "application/json")));

    exceptionRule.expect(ServiceException.class);
    exceptionRule.expectMessage("");
    ;

    Map<String, String> headers = new HashMap<>();
    // TODO: Set headers if needed e.g. headers.put("awesomeheader","12345");
    ResponseEntity<String> resEntity =
        retrofitClientValidationOperations.validatePhone(
            headers, 57, BigInteger.valueOf(3182062767L));
  }

  @Test
  public void validateIdCardSuccess() {
    RetrofitClientValidationOperations retrofitClientValidationOperations =
        new RetrofitClientValidationOperations(URL);

    wireMockRule.stubFor(
        get(urlEqualTo("/clients/validations/idcard/80075464"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withBody("")
                    .withHeader("Content-Type", "application/json")));

    Map<String, String> headers = new HashMap<>();
    // TODO: Set headers if needed e.g. headers.put("awesomeheader","12345");

    ResponseEntity<String> resEntity =
        retrofitClientValidationOperations.validateIdCard(headers, "80075464");

    assertNull(resEntity.getBody());
  }

  @Test
  public void validateIdCardServiceException() {
    RetrofitClientValidationOperations retrofitClientValidationOperations =
        new RetrofitClientValidationOperations(URL);

    wireMockRule.stubFor(
        get(urlEqualTo("/clients/validations/idcard/80075464"))
            .willReturn(aResponse().withFault(Fault.EMPTY_RESPONSE)));

    exceptionRule.expect(ServiceException.class);
    exceptionRule.expectMessage("ERROR");

    Map<String, String> headers = new HashMap<>();
    // TODO: Set headers if needed e.g. headers.put("awesomeheader","12345");
    retrofitClientValidationOperations.validateIdCard(headers, "80075464");
  }

  @Test
  public void validateIdCardServiceException2() {
    RetrofitClientValidationOperations retrofitClientValidationOperations =
        new RetrofitClientValidationOperations(URL);

    wireMockRule.stubFor(
        get(urlEqualTo("/clients/validations/idcard/80075464"))
            .willReturn(aResponse().withStatus(500).withBody("ERROR")));

    exceptionRule.expect(ServiceException.class);
    exceptionRule.expectMessage("ERROR");

    Map<String, String> headers = new HashMap<>();
    retrofitClientValidationOperations.validateIdCard(headers, "80075464");
  }

  @Test
  public void validateIdCardNotFound() {
    RetrofitClientValidationOperations retrofitClientValidationOperations =
        new RetrofitClientValidationOperations(URL);

    wireMockRule.stubFor(
        get(urlEqualTo("/clients/validations/idcard/80075464"))
            .willReturn(
                aResponse()
                    .withStatus(404)
                    .withBody("")
                    .withHeader("Content-Type", "application/json")));

    exceptionRule.expect(ServiceException.class);
    exceptionRule.expectMessage("");
    ;

    Map<String, String> headers = new HashMap<>();
    // TODO: Set headers if needed e.g. headers.put("awesomeheader","12345");
    retrofitClientValidationOperations.validateIdCard(headers, "80075464");
  }

  @Test
  public void validateIdCardUnauthorized() {
    RetrofitClientValidationOperations retrofitClientValidationOperations =
        new RetrofitClientValidationOperations(URL);

    wireMockRule.stubFor(
        get(urlEqualTo("/clients/validations/idcard/80075464"))
            .willReturn(
                aResponse()
                    .withStatus(401)
                    .withBody("")
                    .withHeader("Content-Type", "application/json")));

    exceptionRule.expect(ServiceException.class);
    exceptionRule.expectMessage("");

    Map<String, String> headers = new HashMap<>();
    // TODO: Set headers if needed e.g. headers.put("awesomeheader","12345");
    retrofitClientValidationOperations.validateIdCard(headers, "80075464");
  }

  @Test
  public void validateIdCardForbidden() {
    RetrofitClientValidationOperations retrofitClientValidationOperations =
        new RetrofitClientValidationOperations(URL);

    wireMockRule.stubFor(
        get(urlEqualTo("/clients/validations/idcard/80075464"))
            .willReturn(
                aResponse()
                    .withStatus(403)
                    .withBody("")
                    .withHeader("Content-Type", "application/json")));

    exceptionRule.expect(ServiceException.class);
    exceptionRule.expectMessage("");
    ;

    Map<String, String> headers = new HashMap<>();
    // TODO: Set headers if needed e.g. headers.put("awesomeheader","12345");
    retrofitClientValidationOperations.validateIdCard(headers, "80075464");
  }
}
