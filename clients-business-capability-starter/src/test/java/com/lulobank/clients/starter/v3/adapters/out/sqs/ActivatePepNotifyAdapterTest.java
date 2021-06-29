package com.lulobank.clients.starter.v3.adapters.out.sqs;

import co.com.lulobank.tracing.sqs.SqsBraveTemplate;
import com.lulobank.clients.services.application.port.out.clientnotify.BlacklistStateNotifyPort;
import com.lulobank.clients.services.utils.DatesUtil;
import com.lulobank.clients.starter.v3.adapters.out.sqs.dto.EmailNotificationMessage;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.clients.v3.adapters.port.out.notification.ActivatePepNotifyPort;
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

import static com.lulobank.clients.services.domain.BlacklistNotificationType.CLIENT_WHITELISTED;
import static com.lulobank.clients.services.domain.StateBlackList.WHITELISTED;
import static com.lulobank.clients.starter.v3.adapters.out.Sample.CLIENT_ID;
import static com.lulobank.clients.starter.v3.adapters.out.Sample.MAIL;
import static com.lulobank.clients.starter.v3.adapters.out.Sample.getClientsV3Entity;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;

public class ActivatePepNotifyAdapterTest {

    @Mock
    private SqsBraveTemplate sqsBraveTemplate;

    private ActivatePepNotifyPort activatePepNotifyPort;
    @Captor
    private ArgumentCaptor<String> endpointCaptor;
    @Captor
    private ArgumentCaptor<Event<EmailNotificationMessage>> captorEmailMessage;

    private ClientsV3Entity clientsV3Entity;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        activatePepNotifyPort = new ActivatePepNotifyAdapter("sqs.endpoint", sqsBraveTemplate);
        clientsV3Entity = getClientsV3Entity();
    }

    @Test
    public void sendPepWhitelistedStatedShouldBeSuccess(){
        Locale.setDefault(LocaleUtils.toLocale("es_CO"));
        TimeZone.setDefault(TimeZone.getTimeZone("America/Bogota"));
        doNothing().when(sqsBraveTemplate).convertAndSend(endpointCaptor.capture(), captorEmailMessage.capture());
        Try<Void> response = activatePepNotifyPort.sendActivatePepNotification(clientsV3Entity);
        assertThat(response.isSuccess(), is(true));
        assertThat(endpointCaptor.getValue(), is("sqs.endpoint"));
        assertThat(captorEmailMessage.getValue().getPayload().getClientId(), is(CLIENT_ID));
        assertThat(captorEmailMessage.getValue().getPayload().getNotificationType(), is(("PEP_REACTIVATION")));
        assertThat(captorEmailMessage.getValue().getPayload().getTo(), is(MAIL));
        assertThat(captorEmailMessage.getValue().getPayload().getAttributes().get("dateReport"), is("18 de noviembre de 2020"));
        assertTrue(captorEmailMessage.getValue().getPayload().getAttributes().get("timeReport").toString().startsWith(("11:37")));

    }
}
