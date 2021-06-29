package com.lulobank.clients.services.features.onboardingclients.action;

import com.lulobank.clients.services.events.EconomicInformationEvent;
import com.lulobank.core.Response;
import com.lulobank.core.events.Event;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;

import java.math.BigDecimal;

import static com.lulobank.clients.services.Constants.ID_CLIENT;
import static com.lulobank.clients.services.Sample.buildClientEconomicInformation;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class MessageToNotifySQSEconomicInformationTest {

    @Mock
    private QueueMessagingTemplate queueMessagingTemplate;
    private String sqsEndPoint;

    private MessageToNotifySQSEconomicInformation sqsEconomicInformation;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        sqsEconomicInformation = new MessageToNotifySQSEconomicInformation(queueMessagingTemplate, sqsEndPoint);
    }

    @Test
    public void shouldBuildEventOk() {
        Event event = sqsEconomicInformation.buildEvent(new Response(true), buildClientEconomicInformation());

        assertThat(event, notNullValue());
        assertThat(event.getId(), is(ID_CLIENT));
        assertThat(event.getEventType(), is("EconomicInformationEvent"));
        assertThat(event.getPayload(), notNullValue());
        assertThat(event.getPayload(), instanceOf(EconomicInformationEvent.class));

        EconomicInformationEvent payload = (EconomicInformationEvent) event.getPayload();
        assertThat(payload.getAdditionalIncome(), is(BigDecimal.ZERO));
        assertThat(payload.getAssets(), is(BigDecimal.valueOf(100000000)));
        assertThat(payload.getEconomicActivity(), is("2029"));
        assertThat(payload.getEmployeeCompany(), notNullValue());
        assertThat(payload.getEmployeeCompany().getName(), is("LuloBank"));
        assertThat(payload.getEmployeeCompany().getState(), is("Bogota"));
        assertThat(payload.getEmployeeCompany().getCity(), is("Bogota"));
        assertThat(payload.getLiabilities(), is(BigDecimal.valueOf(1000000)));
        assertThat(payload.getMonthlyIncome(), is(BigDecimal.valueOf(15000000)));
        assertThat(payload.getMonthlyOutcome(), is(BigDecimal.valueOf(5000000)));
        assertThat(payload.getOccupationType(), is("EMPLOYEE"));
        assertThat(payload.getSavingPurpose(), is("Purpose Test"));
        assertThat(payload.getTypeSaving(), is("Type Test"));
    }

    @Test
    public void shouldNotBuildEventOkWhenRequestIsNull() {
        Event event = sqsEconomicInformation.buildEvent(new Response(true), null);

        assertThat(event, nullValue());
    }
}