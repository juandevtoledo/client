package com.lulobank.clients.starter.v3.adapters.out.sqs;

import co.com.lulobank.tracing.sqs.SqsBraveTemplate;
import com.lulobank.clients.starter.v3.adapters.out.Sample;
import com.lulobank.clients.starter.v3.adapters.out.sqs.dto.CheckReferralHoldsForNewClient;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.events.api.Event;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;


public class SqsTransactionsAdapterTest {

    @Mock
    private SqsBraveTemplate sqsBraveTemplate;

    private String transactionSqsEndpoint = "transactions.sqs";
    private SqsTransactionsAdapter testedClass;

    @Captor
    private ArgumentCaptor<Event<CheckReferralHoldsForNewClient>> checkReferralHoldsForNewClientEventArgumentCaptor;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        testedClass = new SqsTransactionsAdapter(transactionSqsEndpoint, sqsBraveTemplate);
    }

    @Test
    public void shouldSendCheckReferralHoldEventSuccessfully() {
        ClientsV3Entity clientsV3Entity = Sample.getClientsV3Entity();

        testedClass.checkReferralHold(clientsV3Entity);

        Mockito.verify(sqsBraveTemplate).convertAndSend(ArgumentMatchers.eq(transactionSqsEndpoint), checkReferralHoldsForNewClientEventArgumentCaptor.capture());

        CheckReferralHoldsForNewClient checkReferralHoldsForNewClient = checkReferralHoldsForNewClientEventArgumentCaptor.getValue().getPayload();

        assertEquals(clientsV3Entity.getIdClient(), checkReferralHoldsForNewClient.getIdClient());
        assertEquals(clientsV3Entity.getEmailAddress(), checkReferralHoldsForNewClient.getEmail());
        assertEquals(clientsV3Entity.getPhonePrefix().toString(), checkReferralHoldsForNewClient.getPhonePrefix());
        assertEquals(clientsV3Entity.getPhoneNumber(), checkReferralHoldsForNewClient.getPhoneNumber());
        assertEquals(clientsV3Entity.getIdSavingAccount(), checkReferralHoldsForNewClient.getAccountId());
        assertEquals(clientsV3Entity.getIdCbs(), checkReferralHoldsForNewClient.getIdCbs());
        assertEquals(clientsV3Entity.getName(), checkReferralHoldsForNewClient.getName());
    }
}