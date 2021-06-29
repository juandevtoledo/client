package com.lulobank.clients.v3.usecase.fatca;

import com.lulobank.clients.services.domain.error.UseCaseResponseError;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.clients.v3.usecase.command.ClientFatcaResponse;
import com.lulobank.clients.v3.usecase.fatca.ClientFatcaUseCase;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.lulobank.clients.services.Constants.ID_CLIENT;
import static com.lulobank.clients.services.SamplesV3.buildClientFatcaInformation;
import static com.lulobank.clients.services.SamplesV3.clientEntityV3Builder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class ClientFatcaUseCaseTest {

    @Mock
    private ClientsV3Repository clientsV3Repository;
    @Captor
    private ArgumentCaptor<ClientsV3Entity> entityArgumentCaptor;
    private ClientFatcaUseCase clientFatcaUseCase;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        clientFatcaUseCase = new ClientFatcaUseCase(clientsV3Repository);
    }

    @Test
    public void shouldSaveFatcaInformationOk() {
        ClientsV3Entity clientsV3Entity = clientEntityV3Builder();
        when(clientsV3Repository.findByIdClient(ID_CLIENT)).thenReturn(Option.of(clientsV3Entity));
        when(clientsV3Repository.save(clientsV3Entity)).thenReturn(Try.of(() -> clientsV3Entity));

        Either<UseCaseResponseError, ClientFatcaResponse> response = clientFatcaUseCase.execute(buildClientFatcaInformation(true));

        assertThat(response.isRight(), is(true));
        assertThat(response.get(), notNullValue());
        assertThat(response.get().isSuccess(), is(true));

        verify(clientsV3Repository).findByIdClient(ID_CLIENT);
        verify(clientsV3Repository).save(entityArgumentCaptor.capture());

        ClientsV3Entity entity = entityArgumentCaptor.getValue();
        assertThat(entity.getFatcaInformation(), notNullValue());
        assertThat(entity.getFatcaInformation().getCountryCode(), is("57"));
        assertThat(entity.getFatcaInformation().getDeclaredDate(), notNullValue());
        assertThat(entity.getFatcaInformation().getStatus(), is("VALIDATION"));
        assertThat(entity.getFatcaInformation().getTin(), is("TIN_NUMBER_TEST"));
        assertThat(entity.getFatcaInformation().getTinObservation(), is("TIN_OBSERVATION_TEST"));
    }

    @Test
    public void shouldNotSaveFatcaInformationWhenClientDoesNotExists() {
        when(clientsV3Repository.findByIdClient(ID_CLIENT)).thenReturn(Option.none());

        Either<UseCaseResponseError, ClientFatcaResponse> response = clientFatcaUseCase.execute(buildClientFatcaInformation(true));

        assertThat(response.isLeft(), is(true));

        UseCaseResponseError error = response.getLeft();
        assertThat(error, notNullValue());
        assertThat(error.getBusinessCode(), is("CLI_101"));
        assertThat(error.getDetail(), is("D"));
        assertThat(error.getProviderCode(), is("404"));

        verify(clientsV3Repository).findByIdClient(ID_CLIENT);
        verifyNoMoreInteractions(clientsV3Repository);
    }

    @Test
    public void shouldNotSaveFatcaInformationWhenDatabaseErrorSaving() {
        ClientsV3Entity clientsV3Entity = clientEntityV3Builder();
        when(clientsV3Repository.findByIdClient(ID_CLIENT)).thenReturn(Option.of(clientsV3Entity));
        when(clientsV3Repository.save(clientsV3Entity)).thenReturn(Try.failure(new UnsupportedOperationException()));

        Either<UseCaseResponseError, ClientFatcaResponse> response = clientFatcaUseCase.execute(buildClientFatcaInformation(true));

        assertThat(response.isLeft(), is(true));
        UseCaseResponseError error = response.getLeft();
        assertThat(error, notNullValue());
        assertThat(error.getBusinessCode(), is("CLI_100"));
        assertThat(error.getDetail(), is("D"));
        assertThat(error.getProviderCode(), is("502"));
    }
}