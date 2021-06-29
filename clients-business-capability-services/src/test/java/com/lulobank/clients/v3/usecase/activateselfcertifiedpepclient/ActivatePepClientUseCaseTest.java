package com.lulobank.clients.v3.usecase.activateselfcertifiedpepclient;

import com.lulobank.clients.sdk.operations.util.CheckPoints;
import com.lulobank.clients.services.domain.activateselfcertifiedpepclient.ActivatePepClientRequest;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.clients.v3.adapters.port.out.notification.ActivatePepNotifyPort;
import com.lulobank.clients.v3.usecase.pep.PepStatus;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.lulobank.clients.services.application.Constant.ID_CARD;
import static com.lulobank.clients.services.application.Sample.getActivateSelfCertifiedPEPClientRequest;
import static com.lulobank.clients.services.application.Sample.getActivateSelfCertifiedPEPClientRequestPepBlacklisted;
import static com.lulobank.clients.services.application.Sample.getClientsV3Entity;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

public class ActivatePepClientUseCaseTest {

    @Mock
    private ClientsV3Repository clientsV3Repository;
    @Mock
    private ActivatePepNotifyPort activatePepNotifyPort;
    @InjectMocks
    private ActivatePepClientUseCase activateSelfCertifiedPEPClientUseCase;
    private ClientsV3Entity clientsV3Entity;
    private ActivatePepClientRequest activatePEPClientRequest;
    @Captor
    private ArgumentCaptor<ClientsV3Entity> clientsV3EntityArgumentCaptor;
    @Captor
    private ArgumentCaptor<String> idCardCaptor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        clientsV3Entity = getClientsV3Entity();
        activatePEPClientRequest = getActivateSelfCertifiedPEPClientRequest();
    }


    @Test
    public void processEventOK() {
        when(clientsV3Repository.findByIdCard(any())).thenReturn(Option.of(clientsV3Entity));
        when(clientsV3Repository.save(any())).thenReturn(Try.success(clientsV3Entity));
        when(activatePepNotifyPort.sendActivatePepNotification(any())).thenReturn(Try.run(System.out::println));
        Try<Void> response = activateSelfCertifiedPEPClientUseCase.execute(activatePEPClientRequest);
        assertThat(response.isSuccess(), is(true));
        verify(clientsV3Repository).findByIdCard(idCardCaptor.capture());
        verify(clientsV3Repository).save(clientsV3EntityArgumentCaptor.capture());
        verify(activatePepNotifyPort).sendActivatePepNotification(any());
        assertEquals(ID_CARD, idCardCaptor.getValue());
        assertEquals(PepStatus.PEP_WHITELISTED.value(), clientsV3EntityArgumentCaptor.getValue().getPep());
        assertEquals(CheckPoints.PEP_FINISHED.name(), clientsV3EntityArgumentCaptor.getValue()
                .getOnBoardingStatus().getCheckpoint());
    }

    @Test
    public void processEventOKPepBlacklisted() {
        when(clientsV3Repository.findByIdCard(any())).thenReturn(Option.of(clientsV3Entity));
        when(clientsV3Repository.save(any())).thenReturn(Try.success(clientsV3Entity));
        Try<Void> response =
                activateSelfCertifiedPEPClientUseCase.execute(getActivateSelfCertifiedPEPClientRequestPepBlacklisted());
        assertThat(response.isSuccess(), is(true));
        verify(clientsV3Repository).findByIdCard(idCardCaptor.capture());
        verify(clientsV3Repository).save(clientsV3EntityArgumentCaptor.capture());
        verifyNoInteractions(activatePepNotifyPort);
        assertEquals(ID_CARD, idCardCaptor.getValue());
        assertEquals(PepStatus.PEP_BLACKLISTED.value(), clientsV3EntityArgumentCaptor.getValue().getPep());
        assertEquals(CheckPoints.PEP_FINISHED.name(), clientsV3EntityArgumentCaptor.getValue()
                .getOnBoardingStatus().getCheckpoint());
    }

    @Test
    public void processFailedSinceRepositoryFailed() {

        when(clientsV3Repository.findByIdCard(any())).thenReturn(Option.none());
        Try<Void> process = activateSelfCertifiedPEPClientUseCase.execute(activatePEPClientRequest);
        assertThat(process.isFailure(), is(true));
        verify(clientsV3Repository).findByIdCard(idCardCaptor.capture());
        verify(clientsV3Repository, never()).updateClientBlacklisted(any());
        assertEquals(ID_CARD, idCardCaptor.getValue());

    }



}
