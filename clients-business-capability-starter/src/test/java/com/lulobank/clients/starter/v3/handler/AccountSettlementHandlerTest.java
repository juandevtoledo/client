package com.lulobank.clients.starter.v3.handler;

import com.lulobank.clients.sdk.operations.dto.onboardingclients.AccountSettlement;
import com.lulobank.clients.starter.adapter.in.dto.ErrorResponse;
import com.lulobank.clients.starter.adapter.in.dto.GenericResponse;
import com.lulobank.clients.starter.v3.adapters.in.dto.TransactionBiometricResponse;
import com.lulobank.clients.v3.usecase.accountsettlement.AccountSettlementUseCase;
import com.lulobank.clients.v3.usecase.command.BiometricResponse;
import io.vavr.control.Either;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static com.lulobank.clients.starter.adapter.Constant.ID_BIOMETRIC;
import static com.lulobank.clients.starter.adapter.Constant.ID_CLIENT;
import static com.lulobank.clients.v3.error.ClientsDataError.clientNotFound;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class AccountSettlementHandlerTest {

    AccountSettlementHandler testedClass;

    @Mock
    private AccountSettlementUseCase accountSettlementUseCase;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        testedClass = new AccountSettlementHandler(accountSettlementUseCase);
    }

    @Test
    public void shouldAccountSettlementSuccess() {
        when(accountSettlementUseCase.execute(any(AccountSettlement.class)))
                .thenReturn(Either.right(new BiometricResponse(ID_BIOMETRIC)));
        ResponseEntity<GenericResponse> result = testedClass.processAccountSettlement(ID_CLIENT);
        TransactionBiometricResponse response =(TransactionBiometricResponse)result.getBody();
        assertEquals(result.getStatusCode(), HttpStatus.ACCEPTED);
        assertEquals(response.getIdTransactionBiometric(), ID_BIOMETRIC);
        assertNotNull(result.getBody());
    }

    @Test
    public void shouldAccountSettlementWhenClientDoesNotExists() {
        when(accountSettlementUseCase.execute(any(AccountSettlement.class))).thenReturn(Either.left(clientNotFound()));
        ResponseEntity<GenericResponse> response = testedClass.processAccountSettlement(ID_CLIENT);
        assertThat(response.getStatusCode(), is(HttpStatus.NOT_FOUND));
        assertThat(response.getBody(), notNullValue());
        assertThat(response.getBody(), instanceOf(ErrorResponse.class));
        ErrorResponse error = (ErrorResponse) response.getBody();
        assertThat(error.getCode(), is("CLI_101"));
        assertThat(error.getDetail(), is("D"));
        assertThat(error.getFailure(), is("404"));
    }

}
