package com.lulobank.clients.starter.v3.handler.pep;

import com.lulobank.clients.starter.v3.result.ClientFailureResult;
import com.lulobank.clients.v3.usecase.command.PepError;
import com.lulobank.clients.v3.usecase.command.UpdatePepRequest;
import com.lulobank.clients.v3.usecase.command.UpdatePepResponse;
import com.lulobank.clients.v3.usecase.pep.GetPepUseCase;
import com.lulobank.clients.v3.usecase.pep.UpdatePepOnboardingUseCase;
import io.vavr.control.Either;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class PepOnboardingHandlerTest {

    @Mock
    private UpdatePepOnboardingUseCase updatePepOnboardingUseCase;

    @Mock
    private GetPepUseCase getPepUseCase;

    private PepOnboardingHandler subject;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        subject = new PepOnboardingHandler(updatePepOnboardingUseCase);
    }

    @Test
    public void updatePepSuccess() {
        UpdatePepRequest updatePepRequest = buildUpdatePepRequest();
        when(updatePepOnboardingUseCase.execute(updatePepRequest)).thenReturn(Either.right(new UpdatePepResponse(updatePepRequest.getIdClient())));
        ResponseEntity<Object> result = subject.updatePep(updatePepRequest);
        assertEquals(HttpStatus.OK ,result.getStatusCode());
        assertTrue(result.getBody() instanceof UpdatePepResponse);
    }

    @Test
    public void updatePepFailed() {
        UpdatePepRequest updatePepRequest = buildUpdatePepRequest();
        when(updatePepOnboardingUseCase.execute(updatePepRequest)).thenReturn(Either.left(new PepError("")));
        ResponseEntity<Object> result = subject.updatePep(updatePepRequest);
        assertEquals(HttpStatus.NOT_ACCEPTABLE ,result.getStatusCode());
        assertTrue(result.getBody() instanceof ClientFailureResult);
    }

    private UpdatePepRequest buildUpdatePepRequest() {
        UpdatePepRequest updatePepRequest = new UpdatePepRequest();
        updatePepRequest.setIdClient("idClient");
        updatePepRequest.setPep(true);
        return updatePepRequest;
    }
}
