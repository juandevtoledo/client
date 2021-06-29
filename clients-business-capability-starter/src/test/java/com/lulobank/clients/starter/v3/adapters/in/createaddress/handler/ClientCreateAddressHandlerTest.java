package com.lulobank.clients.starter.v3.adapters.in.createaddress.handler;

import com.lulobank.clients.services.domain.error.UseCaseResponseError;
import com.lulobank.clients.services.utils.HttpCodes;
import com.lulobank.clients.starter.adapter.in.dto.GenericResponse;
import com.lulobank.clients.starter.v3.adapters.in.dto.CreateAddressRequest;
import com.lulobank.clients.starter.v3.adapters.in.dto.CreateAddressResponse;
import com.lulobank.clients.v3.usecase.command.ClientAddressData;
import com.lulobank.clients.v3.usecase.createaddress.ClientCreateAddressUseCase;
import io.vavr.control.Either;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static com.lulobank.clients.starter.adapter.Constant.ID_CLIENT;
import static com.lulobank.clients.starter.utils.Sample.buildClientAddressData;
import static com.lulobank.clients.starter.utils.Sample.buildCreateAddressRequest;
import static com.lulobank.clients.v3.error.UseCaseErrorStatus.CLI_180;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class ClientCreateAddressHandlerTest {

    @Mock
    private ClientCreateAddressUseCase clientCreateAddressUseCase;
    private ClientCreateAddressHandler clientCreateAddressHandler;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        clientCreateAddressHandler = new ClientCreateAddressHandler(clientCreateAddressUseCase);
    }

    @Test
    public void shouldReturnCreated(){
        CreateAddressRequest request = buildCreateAddressRequest();
        ClientAddressData clientAddressData = buildClientAddressData();
        String idClient = ID_CLIENT;
        when(clientCreateAddressUseCase.execute(any()))
                .thenReturn(Either.right(clientAddressData));

        ResponseEntity<GenericResponse> result = clientCreateAddressHandler.execute(idClient,request);
        assertEquals(201,result.getStatusCodeValue());
        CreateAddressResponse body = (CreateAddressResponse)result.getBody();
        assertNotNull(body);
        assertEquals(clientAddressData.getAddress(), body.getAddress());
        assertEquals(clientAddressData.getAddressPrefix(), body.getAddressPrefix());
        assertEquals(clientAddressData.getAddressComplement(), body.getAddressComplement());
        assertEquals(clientAddressData.getCity(), body.getCity());
        assertEquals(clientAddressData.getCityId(), body.getCityId());
        assertEquals(clientAddressData.getDepartment(), body.getDepartment());
        assertEquals(clientAddressData.getDepartmentId(), body.getDepartmentId());
    }

    @Test
    public void shouldReturnError(){
        CreateAddressRequest request = buildCreateAddressRequest();
        String idClient = ID_CLIENT;
        when(clientCreateAddressUseCase.execute(any()))
                .thenReturn(Either.left(new UseCaseResponseError(CLI_180.name(), HttpCodes.INTERNAL_SERVER_ERROR,CLI_180.getMessage())));

        ResponseEntity<GenericResponse> result = clientCreateAddressHandler.execute(idClient,request);
        assertEquals(500,result.getStatusCodeValue());
    }
}