package com.lulobank.clients.starter.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.lulobank.clients.services.domain.notification.NotificationDisabledRequest;
import com.lulobank.clients.starter.adapter.out.debitcards.DebitCardsClient;
import com.lulobank.clients.starter.v3.adapters.out.dynamo.ClientsDataRepository;
import com.lulobank.clients.v3.adapters.port.in.notification.NotificationDisabledPort;
import com.lulobank.tracing.DatabaseBrave;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BindingResult;

import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public abstract class BaseUnitTest {


    @Mock
    protected ClientsDataRepository clientsDataRepository;
    @Mock
    protected DebitCardsClient debitCardsClient;
    @Mock
    protected NotificationDisabledPort notificationDisabledPort;
    @Mock
    protected BindingResult bindingResult;
    @Mock
    protected DatabaseBrave databaseBrave;
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8082);


    @Captor
    protected ArgumentCaptor<String> idClientCaptor;
    @Captor
    protected ArgumentCaptor<Map<String, String>> headersCaptor;
    @Captor
    protected ArgumentCaptor<String> emailCaptor;
    @Captor
    protected ArgumentCaptor<NotificationDisabledRequest> notificationDisabledArgumentCaptor;

    protected ObjectMapper objectMapper = new ObjectMapper();


}
