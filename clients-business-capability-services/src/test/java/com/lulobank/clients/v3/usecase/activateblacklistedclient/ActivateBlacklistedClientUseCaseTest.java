package com.lulobank.clients.v3.usecase.activateblacklistedclient;

import com.lulobank.clients.services.application.port.out.clientnotify.BlacklistStateNotifyPort;
import com.lulobank.clients.services.domain.StateBlackList;
import com.lulobank.clients.services.domain.activateblacklistedclient.ActivateBlacklistedClientRequest;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
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
import static com.lulobank.clients.services.application.Constant.REPORT_DATE_BLACKLIST;
import static com.lulobank.clients.services.application.Constant.WHITELIST_EXPIRATION;
import static com.lulobank.clients.services.application.Sample.getActivateBlacklistedClientRequest;
import static com.lulobank.clients.services.application.Sample.getClientsV3Entity;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ActivateBlacklistedClientUseCaseTest {
    @Mock
    private  ClientsV3Repository clientsV3Repository;
    @Mock
    private BlacklistStateNotifyPort blacklistStateNotifyPort;
    @InjectMocks
    private ActivateBlacklistedClientUseCase activateBlacklistedClientUseCase;
    private ClientsV3Entity clientsV3Entity;
    private ActivateBlacklistedClientRequest activateBlacklistedClientRequest;

    @Captor
    private ArgumentCaptor<ClientsV3Entity> clientsV3EntityArgumentCaptor;
    @Captor
    private ArgumentCaptor<String> idCardCaptor;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        clientsV3Entity = getClientsV3Entity();
        activateBlacklistedClientRequest = getActivateBlacklistedClientRequest();
    }

    @Test
    public void processEventOK(){
        when(clientsV3Repository.findByIdCard(any())).thenReturn(Option.of(clientsV3Entity));
        when(clientsV3Repository.updateClientBlacklisted(any())).thenReturn(Try.run(System.out::println));
        when(blacklistStateNotifyPort.sendBlacklistStateNotification(any())).thenReturn(Try.run(System.out::println));
        Try<Void> response = activateBlacklistedClientUseCase.execute(activateBlacklistedClientRequest);
        assertThat(response.isSuccess(), is(true));
        verify(clientsV3Repository).findByIdCard(idCardCaptor.capture());
        verify(clientsV3Repository).updateClientBlacklisted(clientsV3EntityArgumentCaptor.capture());
        assertEquals(ID_CARD, idCardCaptor.getValue());
        assertEquals(StateBlackList.WHITELISTED.name(), clientsV3EntityArgumentCaptor.getValue().getBlackListState());
        assertEquals(REPORT_DATE_BLACKLIST, clientsV3EntityArgumentCaptor.getValue().getBlackListDate());
        assertEquals(WHITELIST_EXPIRATION, clientsV3EntityArgumentCaptor.getValue().getWhitelistExpirationDate());
    }

    @Test
    public void processFailedSinceRepositoryFailed() {

        when(clientsV3Repository.findByIdCard(any())).thenReturn(Option.none());
        Try<Void> process = activateBlacklistedClientUseCase.execute(activateBlacklistedClientRequest);
        assertThat(process.isFailure(), is(true));
        verify(clientsV3Repository).findByIdCard(idCardCaptor.capture());
        verify(clientsV3Repository,never()).updateClientBlacklisted(any());
        assertEquals(ID_CARD, idCardCaptor.getValue());

    }




}
