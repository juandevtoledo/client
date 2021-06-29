package com.lulobank.clients.services.actions;

import static java.lang.Boolean.TRUE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import com.lulobank.clients.services.features.RetriesOption;
import com.lulobank.clients.services.features.identitybiometric.model.UpdateIdTransactionBiometric;
import com.lulobank.core.Response;
import com.lulobank.core.events.Event;
import java.util.HashMap;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;

public class MessageToSQSUpdateIdBiometricIdentityTest {
  private static final String ID_CLIENT = "idClient";
  private static final String ID_BIOMETRIC_TRANSACTION = "idBiometricTransaction";
  public static final String SQS_ENDPOINT = "sqsEndpoint";
  @Mock private QueueMessagingTemplate queueMessagingTemplate;
  private MessageToSQSUpdateIdBiometricIdentity messageToSQSUpdateIdBiometricIdentity;
  private RetriesOption retriesOption;
  private UpdateIdTransactionBiometric updateIdTransactionBiometric;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    retriesOption = new RetriesOption(4, new HashMap<>());
    messageToSQSUpdateIdBiometricIdentity =
        new MessageToSQSUpdateIdBiometricIdentity(
            queueMessagingTemplate, SQS_ENDPOINT, retriesOption);
    updateIdTransactionBiometric = new UpdateIdTransactionBiometric();
    updateIdTransactionBiometric.setIdClient(ID_CLIENT);
    updateIdTransactionBiometric.setIdTransactionBiometric(ID_BIOMETRIC_TRANSACTION);
  }

  @Test
  public void shouldBuildEventOk() {
    Event event =
        messageToSQSUpdateIdBiometricIdentity.buildEvent(
            new Response<>(TRUE), updateIdTransactionBiometric);

    assertThat(event, notNullValue());
  }

  @Test
  public void shouldReturnEventNullWhenMaxRetriesZero() {
    retriesOption = new RetriesOption(0, new HashMap<>());
    messageToSQSUpdateIdBiometricIdentity =
        new MessageToSQSUpdateIdBiometricIdentity(
            queueMessagingTemplate, SQS_ENDPOINT, retriesOption);

    Event event =
        messageToSQSUpdateIdBiometricIdentity.buildEvent(
            new Response<>(TRUE), updateIdTransactionBiometric);

    assertThat(event, nullValue());
  }
}
