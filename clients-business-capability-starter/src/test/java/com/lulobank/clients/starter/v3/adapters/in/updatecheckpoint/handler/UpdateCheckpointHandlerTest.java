package com.lulobank.clients.starter.v3.adapters.in.updatecheckpoint.handler;

import com.lulobank.clients.sdk.operations.util.CheckPoints;
import com.lulobank.clients.services.domain.error.UseCaseResponseError;
import com.lulobank.clients.services.utils.HttpCodes;
import com.lulobank.clients.starter.adapter.in.dto.GenericResponse;
import com.lulobank.clients.starter.v3.adapters.in.dto.UpdateCheckpointRequest;
import com.lulobank.clients.starter.v3.adapters.in.dto.UpdateCheckpointResponse;
import com.lulobank.clients.v3.usecase.command.UpdateCheckpointInfo;
import com.lulobank.clients.v3.usecase.updatecheckpoint.UpdateCheckpointUseCase;
import io.vavr.control.Either;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static com.lulobank.clients.starter.adapter.Constant.ID_CLIENT;
import static com.lulobank.clients.starter.utils.Sample.*;
import static com.lulobank.clients.v3.error.UseCaseErrorStatus.CLI_180;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UpdateCheckpointHandlerTest {

    @Mock
    private UpdateCheckpointUseCase updateCheckpointUseCase;
    private UpdateCheckpointHandler updateCheckpointHandler;
    private UpdateCheckpointRequest request;
    private UpdateCheckpointInfo updateCheckpointInfo;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        updateCheckpointHandler = new UpdateCheckpointHandler(updateCheckpointUseCase);
        request = buildUpdateCheckpointRequest();
    }

    @Test
    public void shouldUpdateCheckpoint(){
        request.setCheckpoint(CheckPoints.BLACKLIST_STARTED.name());
        when(updateCheckpointUseCase.execute(any()))
                .thenReturn(Either.right(buildUpdateCheckpointInfo(CheckPoints.BLACKLIST_STARTED)));
        ResponseEntity<GenericResponse> result = updateCheckpointHandler.execute(ID_CLIENT,request);
        assertEquals(200,result.getStatusCodeValue());
        UpdateCheckpointResponse body = (UpdateCheckpointResponse)result.getBody();
        assertNotNull(body);
        verify(updateCheckpointUseCase,times(1)).execute(any());
    }

    @Test
    public void shouldReturnError(){
        request.setCheckpoint(CheckPoints.BLACKLIST_STARTED.name());
        String idClient = ID_CLIENT;
        when(updateCheckpointUseCase.execute(any()))
                .thenReturn(Either.left(new UseCaseResponseError(CLI_180.name(), HttpCodes.INTERNAL_SERVER_ERROR,CLI_180.getMessage())));

        ResponseEntity<GenericResponse> result = updateCheckpointHandler.execute(idClient,request);
        assertEquals(500,result.getStatusCodeValue());
        verify(updateCheckpointUseCase,times(1)).execute(any());
    }

    @Test
    public void shouldReturn400WhenTheCheckpointDoesNotExist(){
        request.setCheckpoint("Checkpoint");
        String idClient = ID_CLIENT;

        ResponseEntity<GenericResponse> result = updateCheckpointHandler.execute(idClient,request);
        assertEquals(400,result.getStatusCodeValue());
        verify(updateCheckpointUseCase,times(0)).execute(any());
    }
}