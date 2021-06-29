package com.lulobank.clients.starter.v3.adapters.out.sqs;

import co.com.lulobank.tracing.sqs.SqsBraveTemplate;
import com.lulobank.clients.services.events.LoanAutomaticDebitMessage;
import com.lulobank.clients.starter.v3.adapters.out.sqs.dto.EmailNotificationMessage;
import com.lulobank.events.api.Event;
import io.vavr.control.Try;
import org.apache.commons.lang3.LocaleUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Locale;
import java.util.TimeZone;

import static com.lulobank.clients.starter.adapter.Constant.ID_CLIENT;
import static com.lulobank.clients.starter.adapter.Constant.PAYMENT_STATUS;
import static com.lulobank.clients.starter.adapter.Constant.VALUED_PAID;
import static com.lulobank.clients.starter.v3.adapters.out.Sample.CLIENT_ID;
import static com.lulobank.clients.starter.v3.adapters.out.Sample.MAIL;
import static com.lulobank.clients.starter.v3.adapters.out.Sample.getClientsV3Entity;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;

public class SqsClientNotificationAdapterTest {

    @Mock
    private SqsBraveTemplate sqsBraveTemplate;
    private SqsClientNotificationAdapter sqsClientNotificationAdapter;
    @Captor
    private ArgumentCaptor<String> endpointCaptor;
    @Captor
    private ArgumentCaptor<Event<LoanAutomaticDebitMessage>> eventArgumentCaptor;
    @Captor
    private ArgumentCaptor<Event<EmailNotificationMessage>> captorEmailMessage;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        sqsClientNotificationAdapter = new SqsClientNotificationAdapter("sqs.endpoint", sqsBraveTemplate, 1, 1);
    }

    @Test
    public void sendEventOk() {
        doNothing().when(sqsBraveTemplate).convertAndSend(endpointCaptor.capture(), eventArgumentCaptor.capture());
        Try<Void> response = sqsClientNotificationAdapter.automaticPayment(getLoanAutomaticDebitMessage());
        assertThat(response.isSuccess(), is(true));
        assertThat(endpointCaptor.getValue(), is("sqs.endpoint"));
        assertThat(eventArgumentCaptor.getValue().getDelay(), is(1));
        assertThat(eventArgumentCaptor.getValue().getMaximumReceives(), is(1));
        assertThat(eventArgumentCaptor.getValue().getPayload().getIdClient(), is(getLoanAutomaticDebitMessage().getIdClient()));
    }

    private LoanAutomaticDebitMessage getLoanAutomaticDebitMessage() {
        return LoanAutomaticDebitMessage.builder()
                .idClient(ID_CLIENT)
                .paymentStatus(PAYMENT_STATUS)
                .valuePaid(VALUED_PAID)
                .build();
    }

    @Test
    public void sendEventBlacklistedTest(){
        Locale.setDefault(LocaleUtils.toLocale("es_CO"));
        TimeZone.setDefault(TimeZone.getTimeZone("America/Bogota"));
        doNothing().when(sqsBraveTemplate).convertAndSend(endpointCaptor.capture(), captorEmailMessage.capture());
        Try<Void> response = sqsClientNotificationAdapter.sendBlacklistNotification(getClientsV3Entity());
        assertThat(response.isSuccess(), is(true));
        assertThat(endpointCaptor.getValue(), is("sqs.endpoint"));
        assertThat(captorEmailMessage.getValue().getPayload().getClientId(), is(CLIENT_ID));
        assertThat(captorEmailMessage.getValue().getPayload().getNotificationType(), is("BLACKLISTED_HIGH_RISK"));
        assertThat(captorEmailMessage.getValue().getPayload().getTo(), is(MAIL));
        assertThat(captorEmailMessage.getValue().getPayload().getAttributes().get("dateReport"), is("18 de noviembre de 2020"));
        assertTrue(captorEmailMessage.getValue().getPayload().getAttributes().get("timeReport").toString().startsWith(("11:37")));

    }

}
