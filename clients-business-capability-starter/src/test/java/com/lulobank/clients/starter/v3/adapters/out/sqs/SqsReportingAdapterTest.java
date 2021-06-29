package com.lulobank.clients.starter.v3.adapters.out.sqs;

import co.com.lulobank.tracing.sqs.SqsBraveTemplate;
import com.lulobank.clients.services.events.LoanAutomaticDebitMessage;
import com.lulobank.clients.starter.v3.adapters.out.sqs.dto.SendSignedDocumentGroupEvent;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.events.api.Event;
import io.vavr.control.Try;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.lulobank.clients.starter.adapter.Constant.ID_CLIENT;
import static com.lulobank.clients.starter.adapter.Constant.MAIL;
import static com.lulobank.clients.starter.adapter.Constant.PAYMENT_STATUS;
import static com.lulobank.clients.starter.adapter.Constant.VALUED_PAID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

public class SqsReportingAdapterTest {

    @Mock
    private SqsBraveTemplate sqsBraveTemplate;
    private SqsReportingAdapter sqsReportingAdapter;


    @Captor
    private ArgumentCaptor<String> endpointCaptor;
    @Captor
    private ArgumentCaptor<Event<SendSignedDocumentGroupEvent>> eventArgumentCaptor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        sqsReportingAdapter = new SqsReportingAdapter("sqs.endpoint", sqsBraveTemplate);
    }


    @Test
    public void sendEventOk() {
        doNothing().when(sqsBraveTemplate).convertAndSend(endpointCaptor.capture(), eventArgumentCaptor.capture());
        Try<Void> response = sqsReportingAdapter.sendBlacklistedDocuments(getClientEntity());
        assertThat(response.isSuccess(), is(true));
        assertThat(endpointCaptor.getValue(), is("sqs.endpoint"));
        assertThat(eventArgumentCaptor.getValue().getPayload().getIdClient(), is(ID_CLIENT));
        assertThat(eventArgumentCaptor.getValue().getPayload().getEmailAddress(), is(MAIL));
        assertThat(eventArgumentCaptor.getValue().getPayload().getIdSignedDocumentGroup(), is("NEW_CLIENT_DOCUMENTS"));
    }

    @Test
    public void sendEventCatDocumentOk() {
        doNothing().when(sqsBraveTemplate).convertAndSend(endpointCaptor.capture(), eventArgumentCaptor.capture());
        Try<Void> response = sqsReportingAdapter.sendCatDocument(getClientEntity());
        assertThat(response.isSuccess(), is(true));
        assertThat(endpointCaptor.getValue(), is("sqs.endpoint"));
        assertThat(eventArgumentCaptor.getValue().getPayload().getIdClient(), is(ID_CLIENT));
        assertThat(eventArgumentCaptor.getValue().getPayload().getEmailAddress(), is(MAIL));
        assertThat(eventArgumentCaptor.getValue().getPayload().getIdSignedDocumentGroup(), is("CATS_DOCUMENTS"));
    }

    @Test
    public void sendEventFail() {
        doThrow(RuntimeException.class).when(sqsBraveTemplate).convertAndSend(endpointCaptor.capture(),
                eventArgumentCaptor.capture());
        Try<Void> response = sqsReportingAdapter.sendBlacklistedDocuments(getClientEntity());
        assertThat(response.isFailure(), is(true));
        assertThat(endpointCaptor.getValue(), is("sqs.endpoint"));
        assertThat(eventArgumentCaptor.getValue().getPayload().getIdClient(), is(ID_CLIENT));
        assertThat(eventArgumentCaptor.getValue().getPayload().getEmailAddress(), is(MAIL));
        assertThat(eventArgumentCaptor.getValue().getPayload().getIdSignedDocumentGroup(), is("NEW_CLIENT_DOCUMENTS"));
    }



    private ClientsV3Entity getClientEntity() {
        ClientsV3Entity entity = new ClientsV3Entity();
        entity.setIdClient(ID_CLIENT);
        entity.setEmailAddress(MAIL);
        return entity;
    }

}
