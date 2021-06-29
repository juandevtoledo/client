package com.lulobank.clients.starter;

import static com.lulobank.clients.sdk.operations.util.CheckPoints.ON_BOARDING;
import static com.lulobank.clients.services.utils.ProductTypeEnum.CREDIT_ACCOUNT;
import static com.lulobank.clients.services.utils.StatusClientVerificationFirebaseEnum.IN_PROGRESS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.amazonaws.SdkClientException;
import com.lulobank.clients.sdk.operations.util.CheckPoints;
import com.lulobank.clients.services.events.CheckIdentityBiometric;
import com.lulobank.clients.services.features.identitybiometric.model.UpdateIdTransactionBiometric;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.outboundadapters.model.OnBoardingStatus;
import com.lulobank.clients.services.utils.ProductTypeEnum;
import com.lulobank.core.events.Event;
import java.util.Objects;
import java.util.Optional;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class UpdateIdentityBiometricTest extends AbstractBaseIntegrationTest {
  private static final String TESTED_URL = "/onboarding/identity/biometric";
  private UpdateIdTransactionBiometric updateIdTransactionBiometric;
  private static final String ID_TRANSACTION_BIOMETRIC = "2w373-28383-2939393";
  private ClientEntity clientEntityFound;
  @Captor protected ArgumentCaptor<Event<CheckIdentityBiometric>> eventArgumentCaptor;

  @Override
  protected void init() {
    updateIdTransactionBiometric = new UpdateIdTransactionBiometric();
    updateIdTransactionBiometric.setIdClient(ID_CLIENT);
    updateIdTransactionBiometric.setIdTransactionBiometric(ID_TRANSACTION_BIOMETRIC);
    clientEntityFound = new ClientEntity();
    clientEntityFound.setIdClient(ID_CLIENT);
    clientEntityFound.setOnBoardingStatus(new OnBoardingStatus(ON_BOARDING.name(), CREDIT_ACCOUNT.name()));
  }

  @Test
  public void shouldUpdateTransactionBiometric() throws Exception {
    when(clientsRepository.findByIdClient(any(String.class)))
        .thenReturn(Optional.of(clientEntityFound));
    when(databaseReference.child(any(String.class)))
            .thenReturn(databaseReference);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(TESTED_URL)
                .contentType(CONTENT_TYPE_JSON)
                .with(bearerToken())
                .content(objectMapper.writeValueAsString(updateIdTransactionBiometric)))
        .andExpect(status().isAccepted());

    verify(clientsRepository, times(1)).save(clientEntityCaptor.capture());
    Mockito.verify(queueMessagingTemplate, times(1))
        .convertAndSend(any(String.class), eventArgumentCaptor.capture(), anyMap());
    Event<CheckIdentityBiometric> eventValue = eventArgumentCaptor.getValue();
    ClientEntity clientEntityUpdate = clientEntityCaptor.getValue();
    assertTrue(
        "IdentityBiometric is not null",
        Objects.nonNull(clientEntityUpdate.getIdentityBiometric()));
    assertEquals(
        "IdentityBiometric status is right",
        IN_PROGRESS.name(),
        clientEntityUpdate.getIdentityBiometric().getStatus());
    assertEquals(
        "IdentityBiometric id transaction is right",
        ID_TRANSACTION_BIOMETRIC,
        clientEntityUpdate.getIdentityBiometric().getIdTransaction());
    assertEquals(
        "EventType is right",
        CheckIdentityBiometric.class.getSimpleName(),
        eventValue.getEventType());
    assertEquals("Event payload IdClient", ID_CLIENT, eventValue.getPayload().getIdClient());
    assertEquals(
        "Event payload IdTransactionBiometric",
        ID_TRANSACTION_BIOMETRIC,
        eventValue.getPayload().getIdTransactionBiometric());
  }

  @Test
  public void shouldNotUpdateSinceClientNotFound() throws Exception {
    when(clientsRepository.findByIdClient(any(String.class))).thenReturn(Optional.empty());
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(TESTED_URL)
                .contentType(CONTENT_TYPE_JSON)
                .with(bearerToken())
                .content(objectMapper.writeValueAsString(updateIdTransactionBiometric)))
        .andExpect(status().isNotFound());
    verify(clientsRepository, times(0)).save(any());
  }

  @Test
  public void shouldNotUpdateSinceDatabaseIsDown() throws Exception {
    when(clientsRepository.findByIdClient(any(String.class)))
        .thenThrow(new SdkClientException("Error"));
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(TESTED_URL)
                .contentType(CONTENT_TYPE_JSON)
                .with(bearerToken())
                .content(objectMapper.writeValueAsString(updateIdTransactionBiometric)))
        .andExpect(status().isBadGateway());
    verify(clientsRepository, times(0)).save(any());
  }

  @Test
  public void shouldReturnBadRequest() throws Exception {
    updateIdTransactionBiometric.setIdTransactionBiometric(null);
    updateIdTransactionBiometric.setIdClient(null);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(TESTED_URL)
                .contentType(CONTENT_TYPE_JSON)
                .with(bearerToken())
                .content(objectMapper.writeValueAsString(updateIdTransactionBiometric)))
        .andExpect(status().isBadRequest());
    verify(clientsRepository, times(0)).save(any());
  }
}
