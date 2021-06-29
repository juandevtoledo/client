package com.lulobank.clients.services.features.clientverificationresult;

import com.lulobank.clients.sdk.operations.dto.economicinformation.ClientEconomicInformation;
import com.lulobank.clients.services.events.IdentityInformation;
import com.lulobank.clients.services.features.onboardingclients.action.MessageToNotifySQSEconomicInformation;
import com.lulobank.clients.services.features.onboardingclients.action.MessageToNotifySQSRiskEngine;
import com.lulobank.clients.services.outboundadapters.ClientsOutboundAdapter;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static com.lulobank.clients.sdk.operations.dto.economicinformation.OccupationType.RETIRED;
import static com.lulobank.clients.services.Constants.BIRTH_DATE_ENTITY;
import static com.lulobank.clients.services.Constants.DATE_ISSUE_ENTITY;
import static com.lulobank.clients.services.Constants.EMAIL;
import static com.lulobank.clients.services.Constants.GENDER;
import static com.lulobank.clients.services.Constants.ID_CARD;
import static com.lulobank.clients.services.Constants.ID_CLIENT;
import static com.lulobank.clients.services.Constants.LAST_NAME;
import static com.lulobank.clients.services.Constants.NAME;
import static com.lulobank.clients.services.Constants.PHONE;
import static com.lulobank.clients.services.SamplesV3.clientEntityV3Builder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

public class ProcessResultByCreditTest {

    @Mock
    private ClientsOutboundAdapter clientsOutboundAdapter;
    @Mock
    private ClientsV3Repository clientsV3Repository;
    @Mock
    private MessageToNotifySQSRiskEngine messageToNotifySQSRiskEngine;
    @Mock
    private MessageToNotifySQSEconomicInformation messageToNotifySQSEconomicInformation;
    @Captor
    private ArgumentCaptor<ClientEconomicInformation> economicArgumentCaptor;
    @Captor
    private ArgumentCaptor<IdentityInformation> identityArgumentCaptor;
    private ClientsV3Entity clientEntity;
    private ProcessResultByCredit processResultByCredit;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        clientEntity = clientEntityV3Builder();
        processResultByCredit = new ProcessResultByCredit(clientsOutboundAdapter, clientEntity, null, null, null,
                clientsV3Repository);

        when(clientsOutboundAdapter.getMessageToNotifySQSEconomicInformation()).thenReturn(messageToNotifySQSEconomicInformation);
        when(clientsOutboundAdapter.getMessageToNotifySQSRiskEngine()).thenReturn(messageToNotifySQSRiskEngine);
    }

    @Test
    public void shouldProcessOk() {
        clientEntity.setEconomicProcessed(true);

        processResultByCredit.process();

        verify(messageToNotifySQSEconomicInformation).run(any(), economicArgumentCaptor.capture());
        verify(messageToNotifySQSRiskEngine).run(any(), identityArgumentCaptor.capture());
        verify(clientsV3Repository).updateOnBoarding(clientEntity);

        assertEconomicInfo(economicArgumentCaptor.getValue());
        assertIdentityInfo(identityArgumentCaptor.getValue());
    }

    @Test
    public void shouldNotSendSqsEventsWhenEconomicProcessedFlagIsNotTrue() {
        processResultByCredit = new ProcessResultByCredit(clientsOutboundAdapter, clientEntity, null, null, null,
                clientsV3Repository);

        processResultByCredit.process();

        verifyNoInteractions(messageToNotifySQSEconomicInformation);
        verifyNoInteractions(messageToNotifySQSRiskEngine);
        verify(clientsV3Repository).updateOnBoarding(clientEntity);
    }

    private void assertIdentityInfo(IdentityInformation identityArgument) {
        assertThat(identityArgument, notNullValue());
        assertThat(identityArgument.getPhone(), notNullValue());
        assertThat(identityArgument.getPhone().getNumber(), is(PHONE));
        assertThat(identityArgument.getBirthDate(), is(BIRTH_DATE_ENTITY));
        assertThat(identityArgument.getDocumentNumber(), is(ID_CARD));
        assertThat(identityArgument.getDocumentType(), is("CC"));
        assertThat(identityArgument.getEmail(), is(EMAIL));
        assertThat(identityArgument.getExpeditionDate(), is(DATE_ISSUE_ENTITY));
        assertThat(identityArgument.getGender(), is(GENDER));
        assertThat(identityArgument.getLastName(), is(LAST_NAME));
        assertThat(identityArgument.getName(), is(NAME));
    }

    private void assertEconomicInfo(ClientEconomicInformation economicInformation) {
        assertThat(economicInformation, notNullValue());
        assertThat(economicInformation.getAdditionalIncome(), is(BigDecimal.valueOf(10000000)));
        assertThat(economicInformation.getAssets(), is(BigDecimal.valueOf(20000000)));
        assertThat(economicInformation.getEconomicActivity(), is("2029"));
        assertThat(economicInformation.getEmployeeCompany(), notNullValue());
        assertThat(economicInformation.getEmployeeCompany().getCity(), is("Bogota"));
        assertThat(economicInformation.getEmployeeCompany().getName(), is("Lulobank"));
        assertThat(economicInformation.getEmployeeCompany().getState(), is("Bogota"));
        assertThat(economicInformation.getIdClient(), is(ID_CLIENT));
        assertThat(economicInformation.getSavingPurpose(), is("Purpose Test"));
        assertThat(economicInformation.getLiabilities(), is(BigDecimal.valueOf(30000000)));
        assertThat(economicInformation.getMonthlyIncome(), is(BigDecimal.valueOf(40000000)));
        assertThat(economicInformation.getMonthlyOutcome(), is(BigDecimal.valueOf(50000000)));
        assertThat(economicInformation.getOccupationType(), is(RETIRED));
        assertThat(economicInformation.getTypeSaving(), is("Type Test"));
    }
}