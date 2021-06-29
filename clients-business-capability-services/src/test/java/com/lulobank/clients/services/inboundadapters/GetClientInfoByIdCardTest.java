package com.lulobank.clients.services.inboundadapters;

import static com.lulobank.core.utils.ValidatorUtils.getListValidations;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.amazonaws.SdkClientException;
import com.lulobank.clients.sdk.operations.dto.ClientInformationByIdCard;
import com.lulobank.clients.sdk.operations.dto.ClientResult;
import com.lulobank.clients.sdk.operations.dto.GetClientInformationByIdCard;
import com.lulobank.clients.services.features.infoclient.GetClientInfoByIdCardHandler;
import com.lulobank.clients.services.features.infoclient.validators.GetClientInfoByIdCardValidator;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.core.Response;
import com.lulobank.core.crosscuttingconcerns.ValidatorDecoratorHandler;
import com.lulobank.core.validations.ValidationResult;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetClientInfoByIdCardTest extends AbstractBaseUnitTest {

  @Mock private ValidatorDecoratorHandler getClientInfoByIdCardHandler;
  @InjectMocks private ClientAdapter testAdapter;
  private GetClientInfoByIdCardHandler testHandler;
  private static final String ID_CARD = "123456789";
  private static final String ID_CBS = "123456789";
  private static final String ID_CLIENT = "49cfe521-8b0d-45c0-b35f-80735242e8d3";
  private static final String EMAIL = "mail@mail.com";
  private static final String NAME = "name";
  private static final String LAST_NAME = "lastName";
  private static final String PHONE_NUMBER = "3136732415";
  private static final Integer PREFIX = 57;
  private GetClientInfoByIdCardValidator testValidator;
  private ClientEntity clientEntity;

  private ClientInformationByIdCard clientInformationByIdCard;

  @Override
  protected void init() {

    testHandler = new GetClientInfoByIdCardHandler(clientsOutboundAdapter);
    testValidator = new GetClientInfoByIdCardValidator();
    clientInformationByIdCard = new ClientInformationByIdCard();
    clientInformationByIdCard.setIdCard(ID_CARD);
    clientInformationByIdCard.setIdCbs(ID_CBS);
    clientInformationByIdCard.setIdClient(ID_CLIENT);
    clientInformationByIdCard.setEmailAddress(EMAIL);
    clientInformationByIdCard.setName(NAME);
    clientInformationByIdCard.setLastName(LAST_NAME);
    clientInformationByIdCard.setPhoneNumber(PHONE_NUMBER);
    clientInformationByIdCard.setPhonePrefix(PREFIX);

    clientEntity = new ClientEntity();
    clientEntity.setIdClient(ID_CLIENT);
    clientEntity.setIdCard(ID_CARD);
    clientEntity.setIdCbs(ID_CBS);
    clientEntity.setEmailAddress(EMAIL);
    clientEntity.setName(NAME);
    clientEntity.setLastName(LAST_NAME);
    clientEntity.setPhoneNumber(PHONE_NUMBER);
    clientEntity.setPhonePrefix(PREFIX);
  }

  @Test
  public void shouldReturnBadRequestClassAdapter() {
    Response response =
        new Response<>(
            getListValidations("FAILED", String.valueOf(HttpStatus.BAD_REQUEST.value())));
    when(getClientInfoByIdCardHandler.handle(any())).thenReturn(response);
    ResponseEntity<ClientResult> responseEntity =
        testAdapter.getClientByIdCard(new HttpHeaders(), StringUtils.EMPTY);
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
  }

  @Test
  public void shouldReturnInternalServerErrorClassAdapter() {
    Response response =
        new Response<>(
            getListValidations("FAILED", String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value())));
    when(getClientInfoByIdCardHandler.handle(any())).thenReturn(response);
    ResponseEntity<ClientResult> responseEntity =
        testAdapter.getClientByIdCard(new HttpHeaders(), StringUtils.EMPTY);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
  }

  @Test
  public void shouldReturnOkClassAdapter() {
    Response response = new Response<>(clientInformationByIdCard);
    when(getClientInfoByIdCardHandler.handle(any())).thenReturn(response);
    ResponseEntity<ClientResult> responseEntity =
        testAdapter.getClientByIdCard(new HttpHeaders(), StringUtils.EMPTY);
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
  }

  @Test
  public void shouldReturnOkClassAdapterInternalCardId() {
    Response response = new Response<>(clientInformationByIdCard);
    when(getClientInfoByIdCardHandler.handle(any())).thenReturn(response);
    ResponseEntity<ClientResult> responseEntity =
        testAdapter.getClientByIdCardInternal(new HttpHeaders(), StringUtils.EMPTY);
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
  }

  @Test
  public void clientInfoByIdCardHandlerShouldReturnNotFound() {
    when(clientsOutboundAdapter.getClientsRepository().findByIdCard(any(String.class)))
        .thenReturn(null);
    Response response = testHandler.handle(new GetClientInformationByIdCard(ID_CARD));
    ValidationResult validationResult =
        (ValidationResult) response.getErrors().stream().findFirst().get();
    assertEquals(String.valueOf(NOT_FOUND.value()), validationResult.getValue());
  }

  @Test
  public void clientInfoByIdCardHandlerShouldReturnClientInfo() {
    when(clientsOutboundAdapter.getClientsRepository().findByIdCard(any(String.class)))
        .thenReturn(clientEntity);
    Response response = testHandler.handle(new GetClientInformationByIdCard(ID_CARD));
    assertEquals(ClientInformationByIdCard.class, response.getContent().getClass());
    ClientInformationByIdCard clientInformationByIdCard =
        (ClientInformationByIdCard) response.getContent();
    assertEquals(ID_CLIENT, clientInformationByIdCard.getIdClient());
    assertEquals(EMAIL, clientInformationByIdCard.getEmailAddress());
    assertEquals(PHONE_NUMBER, clientInformationByIdCard.getPhoneNumber());
    assertEquals(ID_CARD, clientInformationByIdCard.getIdCard());
  }

  @Test
  public void clientInfoByIdCardHandlerShouldReturnInternalServerError() {
    when(clientsOutboundAdapter.getClientsRepository().findByIdCard(any(String.class)))
        .thenThrow(SdkClientException.class);
    Response response = testHandler.handle(new GetClientInformationByIdCard(ID_CARD));
    ValidationResult validationResult =
        (ValidationResult) response.getErrors().stream().findFirst().get();
    assertEquals(String.valueOf(INTERNAL_SERVER_ERROR.value()), validationResult.getValue());
  }

  @Test
  public void requestIsOk() {
    ValidationResult validationResult =
        testValidator.validate(new GetClientInformationByIdCard(ID_CARD));
    assertTrue(Objects.isNull(validationResult));
  }

  @Test
  public void badRequestSinceIdClientIsNull() {
    ValidationResult validationResult =
        testValidator.validate(new GetClientInformationByIdCard(StringUtils.EMPTY));
    assertEquals(String.valueOf(BAD_REQUEST.value()), validationResult.getValue());
  }
}
