package com.lulobank.clients.services.inboundadapters;

import static com.lulobank.clients.services.utils.IdentityBiometricStatus.IN_PROGRESS;
import static com.lulobank.clients.services.utils.SQSUtil.RETRY_COUNT_HEADER;
import static com.lulobank.clients.services.utils.StatusClientVerificationFirebaseEnum.KO_IDENTITY;
import static java.lang.Boolean.TRUE;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lulobank.clients.sdk.operations.util.CheckPoints;
import com.lulobank.clients.services.actions.MessageToSQSCheckBiometricIdentity;
import com.lulobank.clients.services.events.CheckIdentityBiometric;
import com.lulobank.clients.services.features.RetriesOption;
import com.lulobank.clients.services.features.checkidentitybiometric.CheckIdentityBiometricHandler;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.outboundadapters.model.IdentityBiometric;
import com.lulobank.clients.services.outboundadapters.model.OnBoardingStatus;
import com.lulobank.clients.services.utils.ClientVerificationFirebase;
import com.lulobank.clients.services.utils.ProductTypeEnum;
import com.lulobank.core.events.Event;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;

public class SqsCheckIdentityBiometricTest extends AbstractBaseUnitTest {
  private Event<CheckIdentityBiometric> event;
  private static final String ID_CLIENT = "8ee560df-937f-4285-be20-f6cec4c62f5c";
  private static final String ID_TRANSACTION = "65";
  private ObjectMapper objectMapper = new ObjectMapper();
  private ClientEntity clientEntity;
  private MessageToSQSCheckBiometricIdentity messageToSQSCheckBiometricIdentity;
  @Mock private QueueMessagingTemplate queueMessagingTemplate;
  @Mock private RetriesOption retriesOption;
  private SqsClientListenerAdapter testedClass;
  private CheckIdentityBiometricHandler checkIdentityBiometricHandler;
  @Captor protected ArgumentCaptor<ClientEntity> clientEntityCaptor;
  @Captor protected ArgumentCaptor<Map<String, Object>> updateChild;
  @Captor protected ArgumentCaptor<Event<CheckIdentityBiometric>> eventArgumentCaptor;
  @Captor protected ArgumentCaptor<Map<String, Object>> headerCaptor;

  @Override
  protected void init() {
    MessageToSQSCheckBiometricIdentity messageToSQSCheckBiometricIdentity =
        new MessageToSQSCheckBiometricIdentity(queueMessagingTemplate, "http://sqs.com");
    clientsOutboundAdapter.setMessageToSQSCheckBiometricIdentity(
        messageToSQSCheckBiometricIdentity);
    Map<Integer, Integer> delayOptions = new HashMap<>();
    delayOptions.put(0, 5);
    retriesOption = new RetriesOption(1, delayOptions);
    CheckIdentityBiometricHandler checkIdentityBiometricHandler =
        new CheckIdentityBiometricHandler(clientsOutboundAdapter, retriesOption);
    testedClass = new SqsClientListenerAdapter(null, null, null, checkIdentityBiometricHandler,null);
    setEvent();
    setClient();
  }

  @Test
  public void updateFirebaseInKoStatus() throws JsonProcessingException {
    when(clientsOutboundAdapter.getDatabaseReference().child(anyString()))
        .thenReturn(databaseReference);
    when(clientsOutboundAdapter
            .getClientsRepository()
            .findByIdClientAndIdentityBiometric(any(String.class), any(IdentityBiometric.class)))
        .thenReturn(java.util.Optional.ofNullable(clientEntity));
    HashMap<String, Object> header = new HashMap<>();
    header.put(RETRY_COUNT_HEADER, 2);
    String eventString = objectMapper.writeValueAsString(event);
    testedClass.getMessage(header, eventString);
    Mockito.verify(databaseReference, times(1)).updateChildrenAsync(updateChild.capture());
    Map<String, Object> updateChildvalue = updateChild.getValue();
    ClientVerificationFirebase clientVerificationFirebase =
        (ClientVerificationFirebase) updateChildvalue.get("clientVerification");
    assertEquals(
        "Firebase update child ",
        KO_IDENTITY.name(),
        clientVerificationFirebase.getVerificationResult());
  }

  @Test
  public void notUpdateFirebaseSinceClientNotFound() throws JsonProcessingException {
    when(clientsOutboundAdapter
            .getClientsRepository()
            .findByIdClientAndIdentityBiometric(any(String.class), any(IdentityBiometric.class)))
        .thenReturn(Optional.empty());
    HashMap<String, Object> header = new HashMap<>();
    header.put(RETRY_COUNT_HEADER, 1);
    String eventString = objectMapper.writeValueAsString(event);
    testedClass.getMessage(header, eventString);
    Mockito.verify(databaseReference, times(0)).updateChildrenAsync(any());
  }

  @Test
  public void notUpdateFirebaseSinceResetBiometricIsTrue() throws JsonProcessingException {
    clientEntity.setResetBiometric(TRUE);
    when(clientsOutboundAdapter
            .getClientsRepository()
            .findByIdClientAndIdentityBiometric(any(String.class), any(IdentityBiometric.class)))
        .thenReturn(java.util.Optional.ofNullable(clientEntity));
    HashMap<String, Object> header = new HashMap<>();
    String eventString = objectMapper.writeValueAsString(event);
    testedClass.getMessage(header, eventString);
    Mockito.verify(databaseReference, times(0)).updateChildrenAsync(any());
  }

  @Test
  public void retryCheckIdentityMessage() throws JsonProcessingException {
    when(clientsOutboundAdapter
            .getClientsRepository()
            .findByIdClientAndIdentityBiometric(any(String.class), any(IdentityBiometric.class)))
        .thenReturn(java.util.Optional.ofNullable(clientEntity));
    HashMap<String, Object> header = new HashMap<>();
    String eventString = objectMapper.writeValueAsString(event);
    testedClass.getMessage(header, eventString);
    Mockito.verify(queueMessagingTemplate, times(1))
        .convertAndSend(any(String.class), eventArgumentCaptor.capture(), headerCaptor.capture());
    Event<CheckIdentityBiometric> eventValue = eventArgumentCaptor.getValue();
    Map<String, Object> headerValue = headerCaptor.getValue();
    assertEquals(
        "EventType is right",
        CheckIdentityBiometric.class.getSimpleName(),
        eventValue.getEventType());
    assertEquals("Event payload IdClient", ID_CLIENT, eventValue.getPayload().getIdClient());
    assertEquals(
        "Event payload IdTransactionBiometric",
        ID_TRANSACTION,
        eventValue.getPayload().getIdTransactionBiometric());
    assertEquals("Header retry account", 0, headerValue.get(RETRY_COUNT_HEADER));
  }

  private void setEvent() {
    event = new Event<>();
    CheckIdentityBiometric checkIdentityBiometric = new CheckIdentityBiometric();
    checkIdentityBiometric.setIdClient(ID_CLIENT);
    checkIdentityBiometric.setIdTransactionBiometric(ID_TRANSACTION);
    event.setPayload(checkIdentityBiometric);
    event.setId(UUID.randomUUID().toString());
    event.setEventType(CheckIdentityBiometric.class.getSimpleName());
  }

  private void setClient() {
    clientEntity = new ClientEntity();
    clientEntity.setIdClient(ID_CLIENT);
    IdentityBiometric identityBiometric = new IdentityBiometric();
    identityBiometric.setStatus(IN_PROGRESS.name());
    identityBiometric.setIdTransaction(ID_TRANSACTION);
    OnBoardingStatus onBoardingStatus = new OnBoardingStatus();
    onBoardingStatus.setProductSelected(ProductTypeEnum.CREDIT_ACCOUNT.name());
    onBoardingStatus.setCheckpoint(CheckPoints.ON_BOARDING.name());
    clientEntity.setOnBoardingStatus(onBoardingStatus);
    clientEntity.setIdentityBiometric(identityBiometric);
  }
}
