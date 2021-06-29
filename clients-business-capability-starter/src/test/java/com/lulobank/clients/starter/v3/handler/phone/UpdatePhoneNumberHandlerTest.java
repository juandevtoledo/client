package com.lulobank.clients.starter.v3.handler.phone;

import com.lulobank.clients.starter.adapter.in.dto.GenericResponse;
import com.lulobank.clients.starter.v3.adapters.in.phone.dto.UpdatePhoneRequest;
import com.lulobank.clients.starter.v3.adapters.out.Sample;
import com.lulobank.clients.v3.error.ClientsDataError;
import com.lulobank.clients.v3.usecase.phone.UpdatePhoneNumberUseCase;
import io.vavr.control.Either;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class UpdatePhoneNumberHandlerTest {

    @Mock
    private UpdatePhoneNumberUseCase updatePhoneNumberUseCase;

    private UpdatePhoneNumberHandler target;

    private UpdatePhoneRequest request;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        target=new UpdatePhoneNumberHandler(updatePhoneNumberUseCase);
        request =new UpdatePhoneRequest();
        request.setCountryCode(Sample.PHONE_PREFIX);
        request.setNewPhoneNumber(Sample.PHONE_NUMBER);
    }

    @Test
    public void phoneAlreadyExist(){
        when(updatePhoneNumberUseCase.execute(any())).thenReturn(Either.left(ClientsDataError.phoneIsNotUniqueInCustomerService()));
        ResponseEntity<GenericResponse> response = target.updatePhone(Sample.CLIENT_ID,request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR ,response.getStatusCode());
    }

    @Test
    public void phoneUpdateSuccessful(){
        when(updatePhoneNumberUseCase.execute(any())).thenReturn(Either.right(true));
        ResponseEntity<GenericResponse> response = target.updatePhone(Sample.CLIENT_ID,request);

        assertEquals(HttpStatus.OK ,response.getStatusCode());
    }

    @Test
    public void phoneUpdateFail(){
        when(updatePhoneNumberUseCase.execute(any())).thenReturn(Either.right(false));
        ResponseEntity<GenericResponse> response = target.updatePhone(Sample.CLIENT_ID,request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR ,response.getStatusCode());
    }
}
