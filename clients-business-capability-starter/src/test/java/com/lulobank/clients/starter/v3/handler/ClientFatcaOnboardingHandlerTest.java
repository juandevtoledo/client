package com.lulobank.clients.starter.v3.handler;

import com.lulobank.clients.starter.adapter.in.dto.ErrorResponse;
import com.lulobank.clients.starter.adapter.in.dto.GenericResponse;
import com.lulobank.clients.starter.v3.handler.fatca.ClientFatcaOnboardingHandler;
import com.lulobank.clients.v3.usecase.command.ClientFatcaInformation;
import com.lulobank.clients.v3.usecase.command.ClientFatcaResponse;
import com.lulobank.clients.v3.usecase.fatca.ClientFatcaOnboardingUseCase;
import io.vavr.control.Either;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static com.lulobank.clients.starter.adapter.Constant.ID_CLIENT;
import static com.lulobank.clients.starter.utils.Sample.buildClientFatcaRequest;
import static com.lulobank.clients.v3.error.ClientsDataError.clientNotFound;
import static com.lulobank.clients.v3.error.ClientsDataError.connectionFailure;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ClientFatcaOnboardingHandlerTest {

    @Mock
    private ClientFatcaOnboardingUseCase clientFatcaOnboardingUseCase;

    private ClientFatcaOnboardingHandler target;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        target = new ClientFatcaOnboardingHandler(clientFatcaOnboardingUseCase);
    }

    @Test
    public void shouldSaveInformationFatcaOk() {
        when(clientFatcaOnboardingUseCase.execute(any(ClientFatcaInformation.class))).thenReturn(Either.right(new ClientFatcaResponse(true)));

        ResponseEntity<GenericResponse> response = target.saveInformationFatca(buildClientFatcaRequest(), ID_CLIENT);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));

        verify(clientFatcaOnboardingUseCase).execute(any(ClientFatcaInformation.class));
    }

    @Test
    public void shouldNotSaveInformationFatcaWhenClientDoesNotExists() {
        when(clientFatcaOnboardingUseCase.execute(any(ClientFatcaInformation.class))).thenReturn(Either.left(clientNotFound()));

        ResponseEntity<GenericResponse> response = target.saveInformationFatca(buildClientFatcaRequest(), ID_CLIENT);

        assertThat(response.getStatusCode(), is(HttpStatus.NOT_FOUND));
        assertThat(response.getBody(), notNullValue());
        assertThat(response.getBody(), instanceOf(ErrorResponse.class));

        ErrorResponse error = (ErrorResponse) response.getBody();

        assertThat(error.getCode(), is("CLI_101"));
        assertThat(error.getDetail(), is("D"));
        assertThat(error.getFailure(), is("404"));

        verify(clientFatcaOnboardingUseCase).execute(any(ClientFatcaInformation.class));
    }

    @Test
    public void shouldNotSaveInformationFatcaWhenSavingError() {
        when(clientFatcaOnboardingUseCase.execute(any(ClientFatcaInformation.class))).thenReturn(Either.left(connectionFailure()));

        ResponseEntity<GenericResponse> response = target.saveInformationFatca(buildClientFatcaRequest(), ID_CLIENT);

        assertThat(response.getStatusCode(), is(HttpStatus.BAD_GATEWAY));
        assertThat(response.getBody(), notNullValue());
        assertThat(response.getBody(), instanceOf(ErrorResponse.class));

        ErrorResponse error = (ErrorResponse) response.getBody();

        assertThat(error.getCode(), is("CLI_100"));
        assertThat(error.getDetail(), is("D"));
        assertThat(error.getFailure(), is("502"));

        verify(clientFatcaOnboardingUseCase).execute(any(ClientFatcaInformation.class));
    }

    @Test
    public void shouldNotSaveInformationFatcaWhenIsNotSuccess() {
        when(clientFatcaOnboardingUseCase.execute(any(ClientFatcaInformation.class))).thenReturn(Either.right(new ClientFatcaResponse(false)));

        ResponseEntity<GenericResponse> response = target.saveInformationFatca(buildClientFatcaRequest(), ID_CLIENT);

        assertThat(response.getStatusCode(), is(HttpStatus.INTERNAL_SERVER_ERROR));
        assertThat(response.getBody(), notNullValue());
        assertThat(response.getBody(), instanceOf(ErrorResponse.class));

        ErrorResponse error = (ErrorResponse) response.getBody();

        assertThat(error.getCode(), is("CLI_100"));
        assertThat(error.getDetail(), is("D"));
        assertThat(error.getFailure(), is("500"));

        verify(clientFatcaOnboardingUseCase).execute(any(ClientFatcaInformation.class));
    }
}
