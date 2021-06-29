package com.lulobank.clients.starter.outboundadapters.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lulobank.clientalerts.sdk.dto.event.notification.NewNotificationEvent;
import com.lulobank.clients.services.events.EventV2;
import com.lulobank.clients.services.events.StoreDigitalEvidence;
import com.lulobank.clients.services.features.profilev2.model.UpdateClientEmailRequest;
import com.lulobank.clients.services.features.profilev2.model.UpdateProductEmailMessage;
import com.lulobank.clients.starter.adapter.Sample;
import com.lulobank.clients.starter.outboundadapter.sqs.DigitalEvidenceEvent;
import com.lulobank.clients.starter.outboundadapter.sqs.NotificationDisabledEvent;
import com.lulobank.clients.starter.outboundadapter.sqs.QueueServiceAdapter;
import com.lulobank.clients.starter.outboundadapter.sqs.UpdateClientAddressNotificationEvent;
import com.lulobank.clients.starter.outboundadapter.sqs.UpdateProductEmailEvent;
import com.lulobank.clients.v3.adapters.port.out.messaging.dto.NotificationDisabledTypeMessage;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class QueueServiceAdapterTest {

    @InjectMocks
    private QueueServiceAdapter testedClass;

    @Mock
    private DigitalEvidenceEvent digitalEvidenceEvent;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private QueueMessagingTemplate queueMessagingTemplate;

    @Mock
    private UpdateProductEmailEvent updateEmailSavingsEvent;

    @Mock
    private UpdateProductEmailEvent updateEmailCreditsEvent;

    @Mock
    private UpdateProductEmailEvent updateEmailCardsEvent;

    @Mock
    private NotificationDisabledEvent notificationDisabledEvent;

    @Mock
    private UpdateClientAddressNotificationEvent updateClientAddressNotificationEvent;

    @Captor
    private ArgumentCaptor<String> eventArgumentCaptorClient;

    @Captor
    private ArgumentCaptor<NotificationDisabledTypeMessage> argumentCaptorNotificationDisabled;

    private static final String CLIENT_ID = "05e1a61c-2fc1-4a6d-b7c3-11bb9ec4ff3d";
    private static final String NEW_EMAIL = "mail@mail.com";

    private UpdateClientEmailRequest updateEmailRequest;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        updateEmailRequest = new UpdateClientEmailRequest();
        updateEmailRequest.setIdClient(CLIENT_ID);
        updateEmailRequest.setNewEmail(NEW_EMAIL);
        List<UpdateProductEmailEvent> updateProductEmailEventList = Stream.of(updateEmailSavingsEvent,
                updateEmailCreditsEvent,
                updateEmailCardsEvent)
                .collect(Collectors.toList());
        testedClass = new QueueServiceAdapter(queueMessagingTemplate, digitalEvidenceEvent, updateProductEmailEventList,
                updateClientAddressNotificationEvent,
                notificationDisabledEvent);
    }

    @Test
    public void validate_Event_Notification() {
        digitalEvidenceEvent = new DigitalEvidenceEvent(Strings.EMPTY);
        EventV2<StoreDigitalEvidence> eventV2 = digitalEvidenceEvent.map(CLIENT_ID);
        assertEquals(CLIENT_ID, eventV2.getPayload().getIdClient());
    }

    @Test
    public void validateNotificationDisabled() {
        notificationDisabledEvent = new NotificationDisabledEvent(StringUtils.EMPTY);
        NotificationDisabledTypeMessage event = Sample.getNotificationDisabledTypeMessage();
        EventV2<NewNotificationEvent> eventV2 = notificationDisabledEvent.map(event);
        assertEquals(event.getCategory().name(), eventV2.getPayload().getCategory());
        assertEquals(event.getDateNotification(), eventV2.getPayload().getInAppNotification().getDateNotification());
        assertEquals(event.getDescription(), eventV2.getPayload().getInAppNotification().getDescription());
        assertEquals(event.getIdClient(), eventV2.getPayload().getInAppNotification().getIdClient());
        assertEquals(event.getTitle(), eventV2.getPayload().getInAppNotification().getTittle());
    }

    @Test
    public void validate_Sqs_Integration() throws JsonProcessingException {
        DigitalEvidenceEvent digitalEvidenceEvent = new DigitalEvidenceEvent(Strings.EMPTY);
        EventV2<StoreDigitalEvidence> eventV2 = new EventV2<>();
        StoreDigitalEvidence storeDigitalEvidence = new StoreDigitalEvidence();
        storeDigitalEvidence.setIdClient(CLIENT_ID);
        eventV2.setPayload(storeDigitalEvidence);
        when(objectMapper.writeValueAsString(any())).thenReturn(String.valueOf(new ObjectMapper()));
        digitalEvidenceEvent.send(CLIENT_ID, e -> e.setEvent(eventV2));
    }

    @Test
    public void validate_Send_Notification() {
        testedClass.sendDigitalEvidenceMessage(CLIENT_ID);
        verify(digitalEvidenceEvent).send(eventArgumentCaptorClient.capture(), any());
        assertEquals(CLIENT_ID, eventArgumentCaptorClient.getValue());
    }

    @Test
    public void validateSendNotificationDisabled() {
        NotificationDisabledTypeMessage event = Sample.getNotificationDisabledTypeMessage();
        testedClass.sendNotificationDisabled(event);
        verify(notificationDisabledEvent).send(argumentCaptorNotificationDisabled.capture(), any());
        assertEquals(event.getCategory(), argumentCaptorNotificationDisabled.getValue().getCategory());
        assertEquals(event.getDateNotification(), argumentCaptorNotificationDisabled.getValue().getDateNotification());
        assertEquals(event.getDescription(), argumentCaptorNotificationDisabled.getValue().getDescription());
        assertEquals(event.getIdClient(), argumentCaptorNotificationDisabled.getValue().getIdClient());
        assertEquals(event.getTitle(), argumentCaptorNotificationDisabled.getValue().getTitle());
    }

    @Test
    public void validate_Send_Notification_To_Client_Queue() {
        testedClass.sendDigitalEvidenceMessage(CLIENT_ID);
        verify(digitalEvidenceEvent, times(1))
                .send(any(String.class), any());
    }

    @Test
    public void should_create_update_email_product_message() {
        UpdateProductEmailEvent updateProductEmailEvent = new UpdateProductEmailEvent(Strings.EMPTY);
        EventV2<UpdateProductEmailMessage> updateEmailMessage = updateProductEmailEvent.map(updateEmailRequest);
        assertEquals(CLIENT_ID, updateEmailMessage.getPayload().getIdClient());
        assertEquals(NEW_EMAIL, updateEmailMessage.getPayload().getNewEmail());
    }

    @Test
    public void should_send_message_to_savings_accounts_credits_and_cards_queue() {
        testedClass.sendUpdateEmailMessage(updateEmailRequest);
        verify(updateEmailSavingsEvent, times(1)).send(any(UpdateClientEmailRequest.class), any());
        verify(updateEmailCreditsEvent, times(1)).send(any(UpdateClientEmailRequest.class), any());
        verify(updateEmailCardsEvent, times(1)).send(any(UpdateClientEmailRequest.class), any());
    }
}
