package com.lulobank.clients.v3.usecase;

import com.lulobank.clients.services.domain.error.UseCaseResponseError;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.clients.v3.usecase.command.GetClientFatcaResponse;
import io.vavr.control.Either;
import io.vavr.control.Option;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.lulobank.clients.services.SamplesV3.clientEntityV3Builder;
import static com.lulobank.clients.services.application.Constant.ID_CLIENT;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class GetClientFatcaUseCaseTest {

    @Mock
    private ClientsV3Repository clientsV3Repository;
    private GetClientFatcaUseCase getClientFatcaUseCase;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        getClientFatcaUseCase = new GetClientFatcaUseCase(clientsV3Repository);
    }

    @Test
    public void shouldGetInfoOk() {
        when(clientsV3Repository.findByIdClient(ID_CLIENT)).thenReturn(Option.of(clientEntityV3Builder()));

        Either<UseCaseResponseError, GetClientFatcaResponse> response = getClientFatcaUseCase.execute(ID_CLIENT);

        assertThat(response.isRight(), is(true));
        GetClientFatcaResponse fatcaResponse = response.get();
        assertThat(fatcaResponse, notNullValue());
        assertThat(fatcaResponse.getCountryCode(), is("COUNTRY_CODE_TEST"));
        assertThat(fatcaResponse.getDeclaredDate(), notNullValue());
        assertThat(fatcaResponse.getStatus(), is("STATUS_TEST"));
        assertThat(fatcaResponse.getTin(), is("TIN_NUMBER_TEST"));
        assertThat(fatcaResponse.getTinObservation(), is("TIN_OBSERVATION_TEST"));
    }

    @Test
    public void shouldNotGetInfoOkWhenClientNotFound() {
        when(clientsV3Repository.findByIdClient(ID_CLIENT)).thenReturn(Option.none());

        Either<UseCaseResponseError, GetClientFatcaResponse> response = getClientFatcaUseCase.execute(ID_CLIENT);

        assertThat(response.isLeft(), is(true));
        UseCaseResponseError error = response.getLeft();
        assertThat(error, notNullValue());
        assertThat(error.getProviderCode(), is("404"));
        assertThat(error.getDetail(), is("D"));
        assertThat(error.getBusinessCode(), is("CLI_101"));
    }

    @Test
    public void shouldNotGetInfoOkWhenThereIsNoFatcaInformation() {
        ClientsV3Entity entity = clientEntityV3Builder();
        entity.setFatcaInformation(null);
        when(clientsV3Repository.findByIdClient(ID_CLIENT)).thenReturn(Option.of(entity));

        Either<UseCaseResponseError, GetClientFatcaResponse> response = getClientFatcaUseCase.execute(ID_CLIENT);

        assertThat(response.isLeft(), is(true));
        UseCaseResponseError error = response.getLeft();
        assertThat(error, notNullValue());
        assertThat(error.getProviderCode(), is("404"));
        assertThat(error.getDetail(), is("D"));
        assertThat(error.getBusinessCode(), is("CLI_103"));
    }
}