package com.lulobank.clients.services.usecase;

import com.lulobank.clients.services.Constants;
import com.lulobank.clients.services.exception.DigitalEvidenceException;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.ports.out.DigitalEvidenceService;
import com.lulobank.clients.services.ports.repository.ClientsRepositoryV2;
import com.lulobank.clients.services.usecase.command.SaveDigitalEvidence;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.clients.v3.util.DigitalEvidenceTypes;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.lulobank.clients.services.domain.StateBlackList.BLACKLISTED;
import static com.lulobank.clients.services.domain.StateBlackList.NON_BLACKLISTED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ClientDigitalEvidenceUseCaseTest {

    @Mock private ClientsV3Repository clientsV3Repository;
    @Mock private DigitalEvidenceService digitalEvidenceService;

    @Captor private ArgumentCaptor<String> textCaptor;
    @Captor private ArgumentCaptor<ClientsV3Entity> clientEntityCaptor;
    @Captor private ArgumentCaptor<Boolean> booleanArgumentCaptor;

    @InjectMocks private ClientDigitalEvidenceUseCase clientDigitalEvidenceUseCase;

    private SaveDigitalEvidence saveDigitalEvidence;
    private ClientsV3Entity clientEntity;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        clientEntity = new ClientsV3Entity();
        clientEntity.setEmailAddress(Constants.EMAIL);
        clientEntity.setBlackListState(NON_BLACKLISTED.name());

        saveDigitalEvidence = new SaveDigitalEvidence();
        saveDigitalEvidence.setIdClient(Constants.ID_CLIENT);
    }

    @Test
    public void shouldCreateDigitalEvidenceAndUpdateStorageStatusApp() {
        when(clientsV3Repository.findByIdClient(textCaptor.capture())).thenReturn(Option.of(clientEntity));
        when(digitalEvidenceService.saveDigitalEvidence(anyMap(), any(), any())).thenReturn(Try.success(true));
        saveDigitalEvidence.setDigitalEvidenceTypes(DigitalEvidenceTypes.APP);
        Try<Boolean> response = clientDigitalEvidenceUseCase.execute(saveDigitalEvidence);

        verify(clientsV3Repository).save(clientEntityCaptor.capture());

        assertTrue(response.isSuccess());
        assertTrue(response.get());
        assertEquals(textCaptor.getAllValues().get(0), saveDigitalEvidence.getIdClient());
        assertTrue(clientEntityCaptor.getValue().isDigitalStorageStatus());
    }

    @Test
    public void shouldCreateDigitalEvidenceAndUpdateStorageStatusSavingAccount() {
        when(clientsV3Repository.findByIdClient(textCaptor.capture())).thenReturn(Option.of(clientEntity));
        when(digitalEvidenceService.saveDigitalEvidence(anyMap(), any(), any())).thenReturn(Try.success(true));
        saveDigitalEvidence.setDigitalEvidenceTypes(DigitalEvidenceTypes.SAVINGS_ACCOUNT);
        Try<Boolean> response = clientDigitalEvidenceUseCase.execute(saveDigitalEvidence);

        verify(clientsV3Repository).save(clientEntityCaptor.capture());

        assertTrue(response.isSuccess());
        assertTrue(response.get());
        assertEquals(textCaptor.getAllValues().get(0), saveDigitalEvidence.getIdClient());
        assertTrue(clientEntityCaptor.getValue().isCatsDocumentStatus());
    }

    @Test
    public void shouldCreateDigitalEvidenceAndUpdateStorageStatusBlacklisted() {
        clientEntity.setBlackListState(BLACKLISTED.name());
        when(clientsV3Repository.findByIdClient(textCaptor.capture())).thenReturn(Option.of(clientEntity));
        when(digitalEvidenceService.saveDigitalEvidence(anyMap(), any(), any())).thenReturn(Try.success(true));
        saveDigitalEvidence.setDigitalEvidenceTypes(DigitalEvidenceTypes.APP);
        Try<Boolean> response = clientDigitalEvidenceUseCase.execute(saveDigitalEvidence);

        verify(clientsV3Repository).save(clientEntityCaptor.capture());

        assertTrue(response.isSuccess());
        assertTrue(response.get());
        assertEquals(textCaptor.getAllValues().get(0), saveDigitalEvidence.getIdClient());
        assertTrue(clientEntityCaptor.getValue().isDigitalStorageStatus());
    }

    @Test
    public void shouldNotCreateDigitalEvidenceAndNotUpdateStorageStatus() {
        when(clientsV3Repository.findByIdClient(anyString())).thenReturn(Option.of(clientEntity));
        when(digitalEvidenceService.saveDigitalEvidence(anyMap(), any(), any())).thenReturn(Try.failure(new DigitalEvidenceException("Error")));
        saveDigitalEvidence.setDigitalEvidenceTypes(DigitalEvidenceTypes.APP);
        Try<Boolean> response = clientDigitalEvidenceUseCase.execute(saveDigitalEvidence);

        verify(clientsV3Repository,times(0)).save(clientEntityCaptor.capture());

        assertTrue(response.isFailure());
        assertTrue(response.getCause() instanceof DigitalEvidenceException);
    }
}
