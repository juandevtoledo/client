package com.lulobank.clients.v3.usecase;

import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.clients.v3.usecase.riskEngineResultEventv2.CreatePreApprovedOfferUseCase;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.lulobank.clients.services.application.Constant.ID_CLIENT;
import static com.lulobank.clients.services.application.Sample.getClientsV3Entity;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CreatePreApprovedOfferUseCaseTest {

    @Mock
    private ClientsV3Repository clientsV3Repository;

    @InjectMocks
    private CreatePreApprovedOfferUseCase CreatePreApprovedOfferUseCase;
    private ClientsV3Entity clientsV3Entity;

    @Captor
    private ArgumentCaptor<String> idClientCaptor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        clientsV3Entity = getClientsV3Entity();

    }

    @Test
    public void processEventOK() {
        when(clientsV3Repository.findByIdClient(any())).thenReturn(Option.of(clientsV3Entity));
        when(clientsV3Repository.save(any())).thenReturn(Try.success(clientsV3Entity));
        Try<Void> process = CreatePreApprovedOfferUseCase.execute(clientsV3Entity);
        verify(clientsV3Repository).findByIdClient(idClientCaptor.capture());
        assertEquals(ID_CLIENT, idClientCaptor.getValue());
    }

    @Test
    public void processFindByIdClientRepositoryFailed() {
        when(clientsV3Repository.findByIdClient(any())).thenReturn(Option.none());
        Try<Void> process = CreatePreApprovedOfferUseCase.execute(clientsV3Entity);
        assertThat(process.isFailure(), is(true));
        verify(clientsV3Repository).findByIdClient(idClientCaptor.capture());
        assertEquals(ID_CLIENT, idClientCaptor.getValue());
    }
}