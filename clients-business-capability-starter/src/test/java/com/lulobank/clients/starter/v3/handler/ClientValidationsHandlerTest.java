package com.lulobank.clients.starter.v3.handler;

import com.lulobank.clients.starter.adapter.in.dto.GenericResponse;
import com.lulobank.clients.starter.v3.adapters.out.Sample;
import com.lulobank.clients.v3.error.ClientsDataError;
import com.lulobank.clients.v3.usecase.validations.EmailValidationsUseCase;
import io.vavr.control.Either;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class ClientValidationsHandlerTest {

    @Mock
    private EmailValidationsUseCase useCase;

    private ClientValidationsHandler target;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        target =new ClientValidationsHandler(useCase);
    }

    @Test
    public void useCaseReturnClientInBD(){
        when(useCase.execute(any()))
                .thenReturn(Either.left(ClientsDataError.emailIsNotUnique()));

        ResponseEntity<GenericResponse> response = target.validateEmail(Sample.MAIL,new HashMap<>());

        assertEquals(HttpStatus.PRECONDITION_FAILED.value(),response.getStatusCodeValue());

    }

    @Test
    public void useCaseReturnClientInCustomerService(){
        when(useCase.execute(any()))
                .thenReturn(Either.left(ClientsDataError.emailIsNotUniqueInCustomerService()));

        ResponseEntity<GenericResponse> response = target.validateEmail(Sample.MAIL,new HashMap<>());

        assertEquals(HttpStatus.CONFLICT.value(),response.getStatusCodeValue());

    }

    @Test
    public void useCaseReturnSuccess(){
        when(useCase.execute(any()))
                .thenReturn(Either.right(false));

        ResponseEntity<GenericResponse> response = target.validateEmail(Sample.MAIL,new HashMap<>());

        assertEquals(HttpStatus.OK.value(),response.getStatusCodeValue());

    }
}
