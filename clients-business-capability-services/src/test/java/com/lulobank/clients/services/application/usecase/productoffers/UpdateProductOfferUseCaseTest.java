package com.lulobank.clients.services.application.usecase.productoffers;

import com.lulobank.clients.services.domain.error.UseCaseResponseError;
import com.lulobank.clients.services.domain.productoffers.UpdateStatus;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import io.vavr.control.Either;
import io.vavr.control.Option;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static com.lulobank.clients.services.application.Constant.ID_CLIENT;
import static com.lulobank.clients.services.application.Sample.buildUpdateOfferRequest;
import static com.lulobank.clients.services.application.Sample.getClientsV3Entity;
import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class UpdateProductOfferUseCaseTest {

    @Mock
    private ClientsV3Repository clientsV3Repository;
    private UpdateProductOfferUseCase updateProductOfferUseCase;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        updateProductOfferUseCase = new UpdateProductOfferUseCase(clientsV3Repository);
    }

    @Test
    public void shouldProcessOk() {
        when(clientsV3Repository.findByIdClient(ID_CLIENT)).thenReturn(Option.of(getClientsV3Entity()));

        Either<UseCaseResponseError, UpdateStatus> response = updateProductOfferUseCase.execute(buildUpdateOfferRequest());

        assertThat(response.isRight(), is(true));

        assertThat(response.get(), notNullValue());
        assertThat(response.get().isSuccess(), is(true));

        verify(clientsV3Repository).save(any(ClientsV3Entity.class));
    }

    @Test
    public void shouldNotProcessWhenEntityNotFound() {
        when(clientsV3Repository.findByIdClient(ID_CLIENT)).thenReturn(Option.none());

        Either<UseCaseResponseError, UpdateStatus> response = updateProductOfferUseCase.execute(buildUpdateOfferRequest());

        assertThat(response.isLeft(), is(true));

        UseCaseResponseError error = response.getLeft();
        assertThat(error, notNullValue());
        assertThat(error.getProviderCode(), is("404"));
        assertThat(error.getDetail(), is("D"));
        assertThat(error.getBusinessCode(), is("CLI_101"));

        verify(clientsV3Repository).findByIdClient(ID_CLIENT);
        verifyNoMoreInteractions(clientsV3Repository);
    }

    @Test
    public void shouldNotProcessWhenApprovedRiskAnalysisNotFound() {
        ClientsV3Entity entity = getClientsV3Entity();
        entity.setApprovedRiskAnalysis(null);
        when(clientsV3Repository.findByIdClient(ID_CLIENT)).thenReturn(Option.of(entity));

        Either<UseCaseResponseError, UpdateStatus> response = updateProductOfferUseCase.execute(buildUpdateOfferRequest());

        assertThat(response.isLeft(), is(true));

        UseCaseResponseError error = response.getLeft();
        assertThat(error, notNullValue());
        assertThat(error.getProviderCode(), is("404"));
        assertThat(error.getDetail(), is("D"));
        assertThat(error.getBusinessCode(), is("CLI_101"));

        verify(clientsV3Repository).findByIdClient(ID_CLIENT);
        verifyNoMoreInteractions(clientsV3Repository);
    }

    @Test
    public void shouldNotProcessWhenOffersArrayEmpty() {
        ClientsV3Entity entity = getClientsV3Entity();
        entity.getApprovedRiskAnalysis().setResults(emptyList());
        when(clientsV3Repository.findByIdClient(ID_CLIENT)).thenReturn(Option.of(entity));

        Either<UseCaseResponseError, UpdateStatus> response = updateProductOfferUseCase.execute(buildUpdateOfferRequest());

        assertThat(response.isLeft(), is(true));

        UseCaseResponseError error = response.getLeft();
        assertThat(error, notNullValue());
        assertThat(error.getProviderCode(), is("404"));
        assertThat(error.getDetail(), is("D"));
        assertThat(error.getBusinessCode(), is("CLI_101"));

        verify(clientsV3Repository).findByIdClient(ID_CLIENT);
        verifyNoMoreInteractions(clientsV3Repository);
    }

    @Test
    public void shouldNotProcessWhenOfferNotFound() {
        ClientsV3Entity entity = getClientsV3Entity();
        entity.getApprovedRiskAnalysis().getResults().get(0).setIdProductOffer(UUID.randomUUID().toString());
        when(clientsV3Repository.findByIdClient(ID_CLIENT)).thenReturn(Option.of(entity));

        Either<UseCaseResponseError, UpdateStatus> response = updateProductOfferUseCase.execute(buildUpdateOfferRequest());

        assertThat(response.isLeft(), is(true));

        UseCaseResponseError error = response.getLeft();
        assertThat(error, notNullValue());
        assertThat(error.getProviderCode(), is("404"));
        assertThat(error.getDetail(), is("D"));
        assertThat(error.getBusinessCode(), is("CLI_101"));

        verify(clientsV3Repository).findByIdClient(ID_CLIENT);
        verifyNoMoreInteractions(clientsV3Repository);
    }
}