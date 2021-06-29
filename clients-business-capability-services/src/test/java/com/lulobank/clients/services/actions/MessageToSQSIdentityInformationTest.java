package com.lulobank.clients.services.actions;

import com.lulobank.clients.sdk.operations.dto.economicinformation.ClientEconomicInformation;
import com.lulobank.clients.services.events.IdentityInformation;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.core.Response;
import com.lulobank.core.events.Event;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;

import static com.lulobank.clients.sdk.operations.util.CheckPoints.LOAN_CREATED;
import static com.lulobank.clients.services.Constants.BIRTH_DATE_ENTITY;
import static com.lulobank.clients.services.Constants.DATE_ISSUE_ENTITY;
import static com.lulobank.clients.services.Constants.EMAIL;
import static com.lulobank.clients.services.Constants.GENDER;
import static com.lulobank.clients.services.Constants.ID_CARD;
import static com.lulobank.clients.services.Constants.LAST_NAME;
import static com.lulobank.clients.services.Constants.NAME;
import static com.lulobank.clients.services.Constants.PHONE;
import static com.lulobank.clients.services.Constants.PREFIX;
import static com.lulobank.clients.services.Sample.clientEntityBuilder;
import static java.lang.Boolean.TRUE;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class MessageToSQSIdentityInformationTest {

    @Mock
    private QueueMessagingTemplate queueMessagingTemplate;
    String sqsEndPoint = "riskEngineSQSEndpoint";

    private MessageToSQSIdentityInformation messageToSQSIdentityInformation;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        messageToSQSIdentityInformation = new MessageToSQSIdentityInformation(queueMessagingTemplate, sqsEndPoint);
    }

    @Test
    public void shouldCreateEventOk() {
        Response response = new Response(clientEntityBuilder());
        ClientEconomicInformation clientEconomicInformation = new ClientEconomicInformation();
        clientEconomicInformation.setIdClient("ID_CLIENT_TEST");

        Event event = messageToSQSIdentityInformation.buildEvent(response, clientEconomicInformation);

        assertThat(event.getId(), is("ID_CLIENT_TEST"));
        assertThat(event.getPayload(), notNullValue());
        assertThat(event.getPayload(), instanceOf(IdentityInformation.class));

        IdentityInformation payload = (IdentityInformation) event.getPayload();

        assertThat(payload.getBirthDate(), is(BIRTH_DATE_ENTITY));
        assertThat(payload.getDocumentNumber(), is(ID_CARD));
        assertThat(payload.getDocumentType(), is("CC"));
        assertThat(payload.getEmail(), is(EMAIL));
        assertThat(payload.getExpeditionDate(), is(DATE_ISSUE_ENTITY));
        assertThat(payload.getGender(), is(GENDER));
        assertThat(payload.getLastName(), is(LAST_NAME));
        assertThat(payload.getName(), is(NAME));
        assertThat(payload.getPhone(), notNullValue());
        assertThat(payload.getPhone().getNumber(), is(PHONE));
        assertThat(payload.getPhone().getPrefix(), is(String.valueOf(PREFIX)));
    }

    @Test
    public void shouldNotCreateEventWhenInvalidResponse() {
        Response response = new Response(TRUE);
        ClientEconomicInformation clientEconomicInformation = new ClientEconomicInformation();
        clientEconomicInformation.setIdClient("ID_CLIENT_TEST");

        Event event = messageToSQSIdentityInformation.buildEvent(response, clientEconomicInformation);

        assertThat(event, nullValue());
    }

    @Test
    public void shouldNotCreateEventOkWhenOnBordingIsNotFinished() {
        ClientEntity clientEntity = clientEntityBuilder();
        clientEntity.getOnBoardingStatus().setCheckpoint(LOAN_CREATED.name());
        Response response = new Response(clientEntity);
        ClientEconomicInformation clientEconomicInformation = new ClientEconomicInformation();
        clientEconomicInformation.setIdClient("ID_CLIENT_TEST");

        Event event = messageToSQSIdentityInformation.buildEvent(response, clientEconomicInformation);

        assertThat(event, nullValue());
    }
}