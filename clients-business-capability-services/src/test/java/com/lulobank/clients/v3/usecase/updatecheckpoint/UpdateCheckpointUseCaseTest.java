package com.lulobank.clients.v3.usecase.updatecheckpoint;

import com.lulobank.clients.sdk.operations.util.CheckPoints;
import com.lulobank.clients.services.application.port.out.clientsdata.ClientsDataRepositoryPort;
import com.lulobank.clients.services.domain.error.UseCaseResponseError;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.clients.v3.error.ClientsDataError;
import com.lulobank.clients.v3.usecase.command.UpdateCheckpointInfo;
import io.vavr.control.Either;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.lulobank.clients.sdk.operations.util.CheckPoints.ON_BOARDING;
import static com.lulobank.clients.services.SamplesV3.buildUpdateCheckpointInfo;
import static com.lulobank.clients.services.SamplesV3.clientEntityV3Builder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class UpdateCheckpointUseCaseTest {
    @Mock
    private ClientsDataRepositoryPort clientsDataRepositoryPort;
    private UpdateCheckpointUseCase updateCheckpointUseCase;
    private CheckPoints checkpoint =  CheckPoints.BLACKLIST_STARTED;
    @Captor
    private ArgumentCaptor<ClientsV3Entity> clientsV3EntityArgumentCaptor;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        updateCheckpointUseCase = new UpdateCheckpointUseCase(clientsDataRepositoryPort);
    }

    @Test
    public void shouldUpdateCheckpoint(){
        ClientsV3Entity clientsV3Entity = clientEntityV3Builder();
        UpdateCheckpointInfo updateCheckpointInfo = buildUpdateCheckpointInfo(clientsV3Entity.getIdClient(), checkpoint);
        when(clientsDataRepositoryPort.findByIdClient(clientsV3Entity.getIdClient()))
                .thenReturn(Either.right(clientsV3Entity));
        when(clientsDataRepositoryPort.save(clientsV3EntityArgumentCaptor.capture()))
                .thenReturn(Either.right(clientsV3Entity));

        Either<UseCaseResponseError, UpdateCheckpointInfo> result = updateCheckpointUseCase.execute(updateCheckpointInfo);

        assertTrue(result.isRight());
        assertEquals(updateCheckpointInfo.getCheckpoint(), result.get().getCheckpoint());
        assertEquals(checkpoint.name(),clientsV3EntityArgumentCaptor.getValue().getOnBoardingStatus().getCheckpoint());
        verify(clientsDataRepositoryPort,times(1)).findByIdClient(clientsV3Entity.getIdClient());
        verify(clientsDataRepositoryPort,times(1)).save(clientsV3Entity);
    }


    @Test
    public void shouldReturnNotFoundWhenTheClientDoesNotExist(){
        ClientsV3Entity clientsV3Entity = clientEntityV3Builder();
        UpdateCheckpointInfo updateCheckpointInfo = buildUpdateCheckpointInfo(clientsV3Entity.getIdClient(), checkpoint);
        ClientsDataError expectedError = ClientsDataError.clientNotFound();
        when(clientsDataRepositoryPort.findByIdClient(updateCheckpointInfo.getClientId()))
                .thenReturn(Either.left(expectedError));
        Either<UseCaseResponseError, UpdateCheckpointInfo> result = updateCheckpointUseCase.execute(updateCheckpointInfo);

        assertTrue(result.isLeft());
        assertEquals(ON_BOARDING.name(),clientsV3Entity.getOnBoardingStatus().getCheckpoint());
        result.peekLeft(error -> assertEquals(error.getBusinessCode(), expectedError.getBusinessCode()));
        verify(clientsDataRepositoryPort,times(1)).findByIdClient(clientsV3Entity.getIdClient());
        verify(clientsDataRepositoryPort,times(0)).save(clientsV3Entity);
    }

    @Test
    public void shouldReturnAnErrorWhenItFailsToSaveTheEntity(){
        ClientsV3Entity clientsV3Entity = clientEntityV3Builder();
        UpdateCheckpointInfo updateCheckpointInfo = buildUpdateCheckpointInfo(clientsV3Entity.getIdClient(), checkpoint);
        ClientsDataError expectedError = ClientsDataError.internalError();

        when(clientsDataRepositoryPort.findByIdClient(updateCheckpointInfo.getClientId()))
                .thenReturn(Either.right(clientsV3Entity));
        when(clientsDataRepositoryPort.save(clientsV3EntityArgumentCaptor.capture()))
                .thenReturn(Either.left(expectedError));

        Either<UseCaseResponseError, UpdateCheckpointInfo> result = updateCheckpointUseCase.execute(updateCheckpointInfo);

        assertTrue(result.isLeft());
        assertEquals(checkpoint.name(),clientsV3EntityArgumentCaptor.getValue().getOnBoardingStatus().getCheckpoint());
        result.peekLeft(error -> assertEquals(error.getBusinessCode(), expectedError.getBusinessCode()));
        verify(clientsDataRepositoryPort,times(1)).findByIdClient(clientsV3Entity.getIdClient());
        verify(clientsDataRepositoryPort,times(1)).save(clientsV3Entity);
    }
}