package com.lulobank.clients.starter.v3.adapters.in;

import com.google.common.collect.ImmutableList;
import com.lulobank.clients.starter.adapter.in.dto.ErrorResponse;
import com.lulobank.clients.starter.adapter.in.dto.GenericResponse;
import com.lulobank.clients.starter.v3.adapters.in.dto.ClientFatcaRequest;
import com.lulobank.clients.starter.v3.handler.fatca.ClientFatcaHandler;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static com.lulobank.clients.starter.adapter.Constant.ID_CLIENT;
import static com.lulobank.clients.starter.utils.Sample.buildClientFatcaRequest;
import static com.lulobank.clients.starter.utils.Sample.buildGetClientFatcaInfoResponse;
import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ClientFatcaAdapterTest {

    @Mock
    private BindingResult bindingResult;
    @Mock
    private MethodParameter methodParameter;
    @Mock
    private ClientFatcaHandler clientFatcaHandler;
    private ClientFatcaAdapter clientFatcaAdapter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        clientFatcaAdapter = new ClientFatcaAdapter(clientFatcaHandler);
    }

    @Test
    public void shouldReturnOk() {
        ClientFatcaRequest request = buildClientFatcaRequest();

        when(clientFatcaHandler.saveInformationFatca(request, ID_CLIENT)).thenReturn(ResponseEntity.status(HttpStatus.OK).build());
        ResponseEntity<GenericResponse> response = clientFatcaAdapter.saveInformationFatca(new HttpHeaders(),
                ID_CLIENT, request);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));

        verify(clientFatcaHandler).saveInformationFatca(request, ID_CLIENT);
    }

    @Test
    public void shouldReturnOkGetFatcaInfo() {
        when(clientFatcaHandler.getInformationFatca(ID_CLIENT)).thenReturn(ResponseEntity.status(HttpStatus.OK)
                .body(buildGetClientFatcaInfoResponse()));

        ResponseEntity<GenericResponse> response = clientFatcaAdapter.getFatcaInformation(new HttpHeaders(), ID_CLIENT);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));

        verify(clientFatcaHandler).getInformationFatca(ID_CLIENT);
    }

    @Test
    public void shouldReturnErrorWhenBindingResultNotEmpty() {
        when(bindingResult.getAllErrors()).thenReturn(ImmutableList.of(new ObjectError("status", "status is null or empty")));

        ErrorResponse response = clientFatcaAdapter
                .handleValidationExceptions(new MethodArgumentNotValidException(methodParameter, bindingResult));

        assertThat(response, notNullValue());
        assertThat(response.getFailure(), is("400"));
        assertThat(response.getDetail(), is("V"));
        assertThat(response.getCode(), is("CLI_102"));
    }

    @Test
    public void shouldReturnErrorWhenBindingResultErrorsEmpty() {
        when(bindingResult.getAllErrors()).thenReturn(emptyList());

        ErrorResponse response = clientFatcaAdapter
                .handleValidationExceptions(new MethodArgumentNotValidException(methodParameter, bindingResult));

        assertThat(response, notNullValue());
        assertThat(response.getFailure(), is("500"));
        assertThat(response.getDetail(), is("D"));
        assertThat(response.getCode(), is("CLI_100"));
    }
}