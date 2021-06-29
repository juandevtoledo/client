package com.lulobank.clients.starter.v3.handler;

import com.lulobank.clients.starter.adapter.in.dto.ErrorResponse;
import com.lulobank.clients.starter.adapter.in.dto.GenericResponse;
import com.lulobank.clients.starter.v3.adapters.out.Sample;
import com.lulobank.clients.v3.error.ClientsDataError;
import com.lulobank.clients.v3.usecase.UpdateEmailAddressUseCase;
import com.lulobank.clients.v3.usecase.command.UpdateEmailAddress;
import io.vavr.control.Either;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static com.lulobank.clients.starter.adapter.Constant.ID_CLIENT;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class UpdateEmailAddressHandlerTest {

    private UpdateEmailAddressHandler testedClass;

    @Mock
    private UpdateEmailAddressUseCase updateEmailAddressUseCase;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        testedClass = new UpdateEmailAddressHandler(updateEmailAddressUseCase);
    }

    @Test
    public void shouldProcessUpdateEmailOk() {
        when(updateEmailAddressUseCase.execute(any(UpdateEmailAddress.class)))
                .thenReturn(Either.right(Boolean.TRUE));
        ResponseEntity<GenericResponse> response = testedClass
                .updateEmailAddress(ID_CLIENT, Sample.buildUpdateEmailAddressRequest());
        assertThat(response, notNullValue());
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void shouldProcessUpdateEmailClientDoesNotExists() {
        when(updateEmailAddressUseCase.execute(any(UpdateEmailAddress.class)))
                .thenReturn(Either.left(ClientsDataError.clientNotFound()));
        ResponseEntity<GenericResponse> response = testedClass
                .updateEmailAddress(ID_CLIENT, Sample.buildUpdateEmailAddressRequest());
        assertThat(response, notNullValue());
        assertThat(response.getStatusCode(), is(HttpStatus.NOT_FOUND));
        ErrorResponse body = (ErrorResponse) response.getBody();
        assertThat(body, notNullValue());
        assertThat(body.getFailure(), is("404"));
        assertThat(body.getDetail(), is("D"));
        assertThat(body.getCode(), is("CLI_101"));
    }

    @Test
    public void shouldProcessUpdateEmailExists() {
        when(updateEmailAddressUseCase.execute(any(UpdateEmailAddress.class)))
                .thenReturn(Either.left(ClientsDataError.emailIsNotUnique()));
        ResponseEntity<GenericResponse> response = testedClass
                .updateEmailAddress(ID_CLIENT, Sample.buildUpdateEmailAddressRequest());
        assertThat(response, notNullValue());
        assertThat(response.getStatusCode(), is(HttpStatus.PRECONDITION_FAILED));
        ErrorResponse body = (ErrorResponse) response.getBody();
        assertThat(body, notNullValue());
        assertThat(body.getFailure(), is("412"));
        assertThat(body.getDetail(), is("V"));
        assertThat(body.getCode(), is("CLI_104"));
    }

    @Test
    public void shouldProcessUpdateEmailInternalServerError() {
        when(updateEmailAddressUseCase.execute(any(UpdateEmailAddress.class)))
                .thenReturn(Either.left(ClientsDataError.internalServerError()));
        ResponseEntity<GenericResponse> response = testedClass
                .updateEmailAddress(ID_CLIENT, Sample.buildUpdateEmailAddressRequest());
        assertThat(response, notNullValue());
        assertThat(response.getStatusCode(), is(HttpStatus.INTERNAL_SERVER_ERROR));
        ErrorResponse body = (ErrorResponse) response.getBody();
        assertThat(body, notNullValue());
        assertThat(body.getFailure(), is("500"));
        assertThat(body.getDetail(), is("D"));
        assertThat(body.getCode(), is("CLI_109"));
    }
}
