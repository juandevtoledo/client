package com.lulobank.clients.services.inboundadapters;

import static com.lulobank.clients.services.utils.LoanRequestedStatus.IN_PROGRESS;
import static com.lulobank.clients.services.utils.ProductTypeEnum.CREDIT_ACCOUNT;
import static com.lulobank.clients.services.utils.StatusClientVerificationFirebaseEnum.CREATED;
import static com.lulobank.core.utils.ValidatorUtils.getListValidations;
import static java.lang.Boolean.TRUE;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_GATEWAY;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.amazonaws.SdkClientException;
import com.lulobank.clients.sdk.operations.dto.resetidentitybiometric.ClientToReset;
import com.lulobank.clients.services.exception.ClientNotFoundException;
import com.lulobank.clients.services.features.resetidentitybiometric.ResetBiometricIdentityHandler;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.outboundadapters.model.IdentityBiometric;
import com.lulobank.clients.services.outboundadapters.model.OnBoardingStatus;
import com.lulobank.clients.services.utils.ClientVerificationFirebase;
import com.lulobank.core.Response;
import com.lulobank.core.crosscuttingconcerns.ValidatorDecoratorHandler;
import com.lulobank.core.validations.ValidationResult;
import java.util.Map;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResetBiometricIdentityTest extends AbstractBaseUnitTest {

  @Mock private ValidatorDecoratorHandler resetBiometricIdentityHandler;
  private ClientOnboardingAdapter testAdapter;
  private ResetBiometricIdentityHandler testHandler;
  private ClientToReset clientToReset;
  private static final String ID_CLIENT = "ea5948f0-e855-4f34-93bf-f44df8baa23a";

  @Override
  protected void init() {
    testAdapter =
        new ClientOnboardingAdapter(null, null, null, null, resetBiometricIdentityHandler);
    clientToReset = new ClientToReset();
    clientToReset.setIdClient(ID_CLIENT);
    testHandler = new ResetBiometricIdentityHandler(clientsOutboundAdapter);
  }

  @Test
  public void resetBiometricTestAccepted() {
    when(resetBiometricIdentityHandler.handle(any())).thenReturn(new Response(TRUE));
    ResponseEntity<Response> responseEntity =
        testAdapter.resetBiometricIdentity(new HttpHeaders(), clientToReset);
    assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
  }

  @Test
  public void resetBiometricTestResponseFailed() {
    Response response =
        new Response<>(getListValidations("FAILED", String.valueOf(INTERNAL_SERVER_ERROR.value())));
    when(resetBiometricIdentityHandler.handle(any())).thenReturn(response);
    ResponseEntity<Response> responseEntity =
        testAdapter.resetBiometricIdentity(new HttpHeaders(), clientToReset);
    assertEquals(INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
  }

  @Test
  public void handlerBiometricTestUpdateClient() {
    ClientEntity clientEntity = getClientEntity();
    Optional optionalClient = Optional.of(clientEntity);
    when(clientsOutboundAdapter.getClientsRepository().findByIdClient(any()))
        .thenReturn(optionalClient);
    when(databaseReference.child(any())).thenReturn(databaseReference);
    Response response = testHandler.handle(clientToReset);
    verify(databaseReference, times(1)).updateChildrenAsync(firebaseParametersCaptor.capture());
    Map<String, Object> params = firebaseParametersCaptor.getValue();
    ClientVerificationFirebase clientVerificationFirebase =
        (ClientVerificationFirebase) params.get("clientVerification");
    assertEquals(TRUE, response.getContent());
    assertEquals(
        "Firebase state", CREATED.name(), clientVerificationFirebase.getVerificationResult());
    assertEquals(
        "Firebase Product state",
        CREDIT_ACCOUNT.name(),
        clientVerificationFirebase.getProductSelected());
  }

  @Test(expected = ClientNotFoundException.class)
  public void handlerBiometricTestFailedSinceClientNotFound() {
    when(clientsOutboundAdapter.getClientsRepository().findByIdClient(any()))
        .thenReturn(Optional.empty());
    Response response = testHandler.handle(clientToReset);
  }

  @Test
  public void handlerBiometricTestFailedSinceIdentityBiometricNotFound() {
    ClientEntity clientEntity = getClientEntity();
    clientEntity.setIdentityBiometric(null);
    Optional optionalClient = Optional.of(clientEntity);
    when(clientsOutboundAdapter.getClientsRepository().findByIdClient(any()))
        .thenReturn(optionalClient);
    Response response = testHandler.handle(clientToReset);
    ValidationResult validationResult =
        (ValidationResult) response.getErrors().stream().findFirst().get();
    assertEquals("Status Failed", String.valueOf(NOT_FOUND.value()), validationResult.getValue());
  }

  @Test
  public void handlerBiometricTestFailedSinceSdkException() {

    when(clientsOutboundAdapter.getClientsRepository().findByIdClient(any()))
        .thenThrow(new SdkClientException("DYNAMO ERROR"));
    Response response = testHandler.handle(clientToReset);
    ValidationResult validationResult =
        (ValidationResult) response.getErrors().stream().findFirst().get();
    assertEquals("Status Failed", String.valueOf(BAD_GATEWAY.value()), validationResult.getValue());
  }

  @NotNull
  private ClientEntity getClientEntity() {
    ClientEntity clientEntity = new ClientEntity();
    clientEntity.setIdClient(ID_CLIENT);
    IdentityBiometric identityBiometric = new IdentityBiometric();
    identityBiometric.setStatus(IN_PROGRESS.name());
    identityBiometric.setIdTransaction("2");
    clientEntity.setIdentityBiometric(identityBiometric);
    OnBoardingStatus onBoardingStatus = new OnBoardingStatus();
    onBoardingStatus.setProductSelected(CREDIT_ACCOUNT.name());
    clientEntity.setOnBoardingStatus(onBoardingStatus);
    return clientEntity;
  }
}
