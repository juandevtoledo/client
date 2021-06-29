package com.lulobank.clients.services.application;

import com.lulobank.clients.services.application.port.out.clientsdata.ClientsDataRepositoryPort;
import com.lulobank.clients.services.application.port.out.debitcards.DebitCardsPort;
import com.lulobank.clients.services.application.port.out.savingsaccounts.SavingsAccountsPort;
import com.lulobank.clients.services.ports.out.MessageService;
import com.lulobank.clients.v3.adapters.port.out.messaging.dto.ClientAlertsProperties;
import com.lulobank.clients.v3.adapters.port.out.messaging.dto.NotificationDisabledTypeMessage;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public abstract class BaseUnitTest {
    @Mock
    protected ClientsDataRepositoryPort clientsDataRepositoryPort;
    @Mock
    protected SavingsAccountsPort savingsAccountsPort;
    @Mock
    protected DebitCardsPort debitCardsPort;
    @Mock
    protected ClientAlertsProperties clientAlertsProperties;
    @Mock
    protected MessageService messageService;

    @Captor
    protected ArgumentCaptor<String> idClientCaptor;
    @Captor
    protected ArgumentCaptor<Map<String, String>> headersCaptor;
    @Captor
    protected ArgumentCaptor<String> emailCaptor;
    @Captor
    protected ArgumentCaptor<NotificationDisabledTypeMessage> notificationDisabledCaptor;

}
