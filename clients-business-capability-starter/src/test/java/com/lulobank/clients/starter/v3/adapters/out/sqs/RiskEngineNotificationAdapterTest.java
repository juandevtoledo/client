package com.lulobank.clients.starter.v3.adapters.out.sqs;

import co.com.lulobank.tracing.sqs.SqsBraveTemplate;
import com.lulobank.clients.services.events.EconomicInformationEvent;
import com.lulobank.clients.services.events.IdentityInformation;
import com.lulobank.events.api.Event;
import io.vavr.control.Try;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doNothing;

public class RiskEngineNotificationAdapterTest {
    @Mock
    private SqsBraveTemplate sqsBraveTemplate;
    private RiskEngineNotificationAdapter riskEngineNotificationAdapter;
    @Captor
    private ArgumentCaptor<String> endpointCaptor;
    @Captor
    private ArgumentCaptor<Event<EconomicInformationEvent>> eventArgumentCaptorEconomic;
    @Captor
    private ArgumentCaptor<Event<IdentityInformation>> eventArgumentCaptorIdentity;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        riskEngineNotificationAdapter = new RiskEngineNotificationAdapter("sqs.endpoint", sqsBraveTemplate);
    }

    @Test
    public void sendEconomicEventOk() {
        doNothing().when(sqsBraveTemplate).convertAndSend(endpointCaptor.capture(), eventArgumentCaptorEconomic.capture());
        Try<Void> response = riskEngineNotificationAdapter.setEconomicInformation(getEconomicInformationEvent(), new HashMap<String, Object>(), "abcd-efgh");
        assertThat(response.isSuccess(), is(true));
        assertThat(endpointCaptor.getValue(), is("sqs.endpoint"));
        assertThat(eventArgumentCaptorEconomic.getValue().getDelay(), is(5));
        assertThat(eventArgumentCaptorEconomic.getValue().getMaximumReceives(), is(5));
        assertThat(eventArgumentCaptorEconomic.getValue().getPayload().getOccupationType(), is(getEconomicInformationEvent().getOccupationType()));
    }

    private EconomicInformationEvent getEconomicInformationEvent() {
        EconomicInformationEvent event = new EconomicInformationEvent();
        event.setOccupationType("EMPLOYEE");
        return event;
    }

    @Test
    public void sendIdentityEventOk() {
        doNothing().when(sqsBraveTemplate).convertAndSend(endpointCaptor.capture(), eventArgumentCaptorIdentity.capture());
        Try<Void> response = riskEngineNotificationAdapter.setIdentityInformation(getIdentityInformationEvent(), new HashMap<String, Object>(), "abcd-efgh");
        assertThat(response.isSuccess(), is(true));
        assertThat(endpointCaptor.getValue(), is("sqs.endpoint"));
        assertThat(eventArgumentCaptorIdentity.getValue().getDelay(), is(5));
        assertThat(eventArgumentCaptorIdentity.getValue().getMaximumReceives(), is(5));
        assertThat(eventArgumentCaptorIdentity.getValue().getPayload().getDocumentType(), is(getIdentityInformationEvent().getDocumentType()));
        assertThat(eventArgumentCaptorIdentity.getValue().getPayload().getName(), is(getIdentityInformationEvent().getName()));
        assertThat(eventArgumentCaptorIdentity.getValue().getPayload().getGender(), is(getIdentityInformationEvent().getGender()));
    }

    private IdentityInformation getIdentityInformationEvent() {
        IdentityInformation event = new IdentityInformation();
        event.setDocumentType("CC");
        event.setName("Lulobank customer");
        event.setGender("Male");
        return event;
    }

}
