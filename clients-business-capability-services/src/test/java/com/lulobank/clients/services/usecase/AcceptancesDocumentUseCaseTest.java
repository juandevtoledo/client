package com.lulobank.clients.services.usecase;

import com.lulobank.clients.services.exception.DigitalEvidenceException;
import com.lulobank.clients.services.outboundadapters.model.ClientAcceptance;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.ports.out.AcceptancesDocumentService;
import com.lulobank.clients.services.ports.repository.ClientsRepositoryV2;
import com.lulobank.clients.services.usecase.command.SaveAcceptancesDocument;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AcceptancesDocumentUseCaseTest {

    @Mock
    private ClientsRepositoryV2 clientsRepositoryV2;
    @Mock
    private AcceptancesDocumentService acceptancesDocumentService;

    @Captor
    private ArgumentCaptor<String> textCaptor;
    @Captor private ArgumentCaptor<ClientEntity> clientEntityCaptor;

    @InjectMocks
    private AcceptancesDocumentUseCase acceptancesDocumentUseCase;

    private SaveAcceptancesDocument saveAcceptancesDocument;
    private ClientEntity clientEntity;


    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        clientEntity = new ClientEntity();
        ClientAcceptance clientAcceptance = new ClientAcceptance();
        clientAcceptance.setPersistedInDigitalEvidence(false);
        clientAcceptance.setDocumentAcceptancesTimestamp(LocalDateTime.now());
        clientEntity.setAcceptances(clientAcceptance);

        saveAcceptancesDocument = new SaveAcceptancesDocument();
        saveAcceptancesDocument.setIdClient("123-abcd");
    }

    @Test
    public void shouldCreateAcceptancesDocumentAndUpdatePersisted() {
        when(clientsRepositoryV2.findByIdClient(textCaptor.capture())).thenReturn(Option.of(clientEntity));
        when(acceptancesDocumentService.generateAcceptancesDocument(anyMap(), any(), any()))
                .thenReturn(Try.success(true));
        Try<Boolean> response = acceptancesDocumentUseCase.execute(saveAcceptancesDocument);

        verify(clientsRepositoryV2).save(clientEntityCaptor.capture());
        assertTrue(response.isSuccess());
        assertTrue(response.get());
        assertEquals(textCaptor.getAllValues().get(0), saveAcceptancesDocument.getIdClient());
        assertTrue(clientEntityCaptor.getValue().getAcceptances().isPersistedInDigitalEvidence());
    }


    @Test
    public void shouldNotCreateAcceptanceDocumentAndNotUpdatePersisted() {
        when(clientsRepositoryV2.findByIdClient(anyString())).thenReturn(Option.of(clientEntity));
        when(acceptancesDocumentService.generateAcceptancesDocument(anyMap(), any(), any()))
                .thenReturn(Try.failure(new DigitalEvidenceException("Error")));
        Try<Boolean> response = acceptancesDocumentUseCase.execute(saveAcceptancesDocument);

        verify(clientsRepositoryV2,times(0)).save(clientEntityCaptor.capture());
        assertTrue(response.isFailure());
        assertTrue(response.getCause() instanceof DigitalEvidenceException);
    }
}
