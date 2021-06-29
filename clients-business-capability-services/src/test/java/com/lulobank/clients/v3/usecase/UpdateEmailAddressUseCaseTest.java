package com.lulobank.clients.v3.usecase;

import com.lulobank.clients.services.SamplesV3;
import com.lulobank.clients.services.domain.error.UseCaseResponseError;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class UpdateEmailAddressUseCaseTest {

    private UpdateEmailAddressUseCase testedClass;

    @Mock
    private ClientsV3Repository clientsV3Repository;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        testedClass = new UpdateEmailAddressUseCase(clientsV3Repository);
    }

    @Test
    public void shouldUpdateEmailAddressOk(){
        when(clientsV3Repository.findByIdClient(anyString()))
                .thenReturn(Option.of(SamplesV3.clientEntityV3Builder()));
        when(clientsV3Repository.findByEmailAddress(anyString()))
                .thenReturn(Option.none());
        when(clientsV3Repository.updateEmailByIdClient(anyString(), anyString()))
                .thenReturn(Try.run(System.out::println));
        Either<UseCaseResponseError, Boolean> response =
                testedClass.execute(SamplesV3.buildUpdateEmailAddress());
        assertThat(response.isRight(), is(true));
        assertThat(response.get(), is(true));
    }

    @Test
    public void shouldUpdateEmailAddressClientDoesNotExists(){
        when(clientsV3Repository.findByIdClient(anyString()))
                .thenReturn(Option.none());
        Either<UseCaseResponseError, Boolean> response =
                testedClass.execute(SamplesV3.buildUpdateEmailAddress());
        UseCaseResponseError responseError = response.getLeft();
        assertThat(response.isLeft(), is(true));
        assertThat(responseError.getBusinessCode(), is("CLI_101"));
        assertThat(responseError.getDetail(), is("D"));
        assertThat(responseError.getProviderCode(), is("404"));
    }


    @Test
    public void shouldUpdateEmailExists(){
        when(clientsV3Repository.findByIdClient(anyString()))
                .thenReturn(Option.of(SamplesV3.clientEntityV3Builder()));
        when(clientsV3Repository.findByEmailAddress(anyString()))
                .thenReturn(Option.of(SamplesV3.clientEntityV3Builder()));
        when(clientsV3Repository.updateEmailByIdClient(anyString(), anyString()))
                .thenReturn(Try.run(System.out::println));
        Either<UseCaseResponseError, Boolean> response =
                testedClass.execute(SamplesV3.buildUpdateEmailAddress());
        UseCaseResponseError responseError = response.getLeft();
        assertThat(response.isLeft(), is(true));
        assertThat(responseError.getBusinessCode(), is("CLI_104"));
        assertThat(responseError.getDetail(), is("V"));
        assertThat(responseError.getProviderCode(), is("412"));
    }

}
