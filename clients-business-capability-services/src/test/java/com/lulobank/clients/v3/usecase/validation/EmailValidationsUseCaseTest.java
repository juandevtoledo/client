package com.lulobank.clients.v3.usecase.validation;

import com.lulobank.clients.services.SamplesV3;
import com.lulobank.clients.services.application.Constant;
import com.lulobank.clients.services.domain.error.UseCaseResponseError;
import com.lulobank.clients.services.ports.out.CustomerService;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.usecase.command.ValidateEmailIsUnique;
import com.lulobank.clients.v3.usecase.validations.EmailValidationsUseCase;
import com.lulobank.clients.v3.vo.AdapterCredentials;
import io.vavr.control.Either;
import io.vavr.control.Option;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;

import static com.lulobank.clients.v3.error.ClientsDataErrorStatus.CLI_104;
import static com.lulobank.clients.v3.error.ClientsDataErrorStatus.CLI_105;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class EmailValidationsUseCaseTest {

    @Mock
    private ClientsV3Repository repository;
    @Mock
    private CustomerService customerService;

    private EmailValidationsUseCase target;

    private ValidateEmailIsUnique command;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        target=new EmailValidationsUseCase(repository,customerService);
        command = ValidateEmailIsUnique.builder().email(Constant.MAIL).credentials(new AdapterCredentials(new HashMap<>())).build();
    }

    @Test
    public void clientExistInBD(){
        when(repository.findByEmailAddress(Constant.MAIL)).thenReturn(Option.of(SamplesV3.clientEntityV3Builder()));

        Either<UseCaseResponseError, Boolean> response = target.execute(command);

        assertTrue(response.isLeft());
        assertFalse(response.isRight());
        assertEquals(CLI_104.name(),response.getLeft().getBusinessCode());
    }

    @Test
    public void clientDoesNotExistInBDButExistInCustomerService(){
        when(repository.findByEmailAddress(Constant.MAIL)).thenReturn(Option.none());
        when(customerService.isEmailExist(any(),eq(Constant.MAIL))).thenReturn(Either.right(true));

        Either<UseCaseResponseError, Boolean> response = target.execute(command);

        assertTrue(response.isLeft());
        assertFalse(response.isRight());
        assertEquals(CLI_105.name(),response.getLeft().getBusinessCode());
    }

    @Test
    public void clientDoesNotExistInBDAndCustomerService(){
        when(repository.findByEmailAddress(Constant.MAIL)).thenReturn(Option.none());
        when(customerService.isEmailExist(any(),eq(Constant.MAIL))).thenReturn(Either.right(false));

        Either<UseCaseResponseError, Boolean> response = target.execute(command);

        assertTrue(response.isRight());
        assertFalse(response.isLeft());
        assertFalse(response.get());
    }

}
