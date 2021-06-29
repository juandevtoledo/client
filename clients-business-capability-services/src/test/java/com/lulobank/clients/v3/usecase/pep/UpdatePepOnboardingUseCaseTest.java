package com.lulobank.clients.v3.usecase.pep;

import com.lulobank.clients.sdk.operations.util.CheckPoints;
import com.lulobank.clients.services.SamplesV3;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.OnBoardingStatusV3;
import com.lulobank.clients.v3.usecase.command.PepError;
import com.lulobank.clients.v3.usecase.command.UpdatePepRequest;
import com.lulobank.clients.v3.usecase.command.UpdatePepResponse;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class UpdatePepOnboardingUseCaseTest {

    @Mock
    private ClientsV3Repository clientsV3Repository;
    @Captor
    private ArgumentCaptor<ClientsV3Entity> entityArgumentCaptor;

    private UpdatePepOnboardingUseCase target;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        target = new UpdatePepOnboardingUseCase(clientsV3Repository);
    }

    @Test
    public void ShouldUpdatePepSuccessAndClientIsPep() {
        UpdatePepRequest updatePepRequest = buildUpdatePepRequest();
        OnBoardingStatusV3 onBoardingStatusV3 =new OnBoardingStatusV3(CheckPoints.BLACKLIST_FINISHED.name(),"");
        ClientsV3Entity clientsV3Entity = SamplesV3.clientEntityV3Builder(onBoardingStatusV3,null);
        when(clientsV3Repository.findByIdClient(updatePepRequest.getIdClient())).thenReturn(Option.of(clientsV3Entity));
        when(clientsV3Repository.save(entityArgumentCaptor.capture())).thenReturn(Try.of(() -> clientsV3Entity));

        Either<PepError, UpdatePepResponse> result = target.execute(updatePepRequest);
        assertThat(result.isRight(), is(true));
        assertThat(result.isLeft(), is(false));
        assertThat(result.isEmpty(), is(false));
        assertEquals(entityArgumentCaptor.getValue().getOnBoardingStatus().getCheckpoint(), CheckPoints.BLACKLIST_FINISHED.name());
    }

    @Test
    public void ShouldUpdatePepSuccessAndClientIsNotPep() {
        UpdatePepRequest updatePepRequest = buildUpdatePepRequestIsNotPep();
        ClientsV3Entity clientsV3Entity = SamplesV3.clientEntityV3Builder();
        when(clientsV3Repository.findByIdClient(updatePepRequest.getIdClient())).thenReturn(Option.of(clientsV3Entity));
        when(clientsV3Repository.save(entityArgumentCaptor.capture())).thenReturn(Try.of(() -> clientsV3Entity));

        Either<PepError, UpdatePepResponse> result = target.execute(updatePepRequest);
        assertThat(result.isRight(), is(true));
        assertThat(result.isLeft(), is(false));
        assertThat(result.isEmpty(), is(false));
        assertEquals(entityArgumentCaptor.getValue().getOnBoardingStatus().getCheckpoint(), CheckPoints.PEP_FINISHED.name());
    }


    @Test
    public void ShouldUpdatePepClientNotFound() {
        UpdatePepRequest updatePepRequest = buildUpdatePepRequest();
        when(clientsV3Repository.findByIdClient(updatePepRequest.getIdClient())).thenReturn(Option.of(null));

        Either<PepError, UpdatePepResponse> result = target.execute(updatePepRequest);
        assertThat(result.isRight(), is(false));
        assertThat(result.isLeft(), is(true));
        assertThat(result.isEmpty(), is(true));
        assertThat(result.getLeft(), notNullValue());
    }

    @Test
    public void ShouldUpdatePepSaveClientFailed() {
        UpdatePepRequest updatePepRequest = buildUpdatePepRequestIsNotPep();
        ClientsV3Entity clientsV3Entity = SamplesV3.clientEntityV3Builder();
        when(clientsV3Repository.findByIdClient(updatePepRequest.getIdClient())).thenReturn(Option.of(clientsV3Entity));
        when(clientsV3Repository.save(entityArgumentCaptor.capture())).thenReturn(Try.failure(new RuntimeException()));

        Either<PepError, UpdatePepResponse> result = target.execute(updatePepRequest);
        assertThat(result.isRight(), is(false));
        assertThat(result.isLeft(), is(true));
        assertThat(result.isEmpty(), is(true));
        assertThat(result.getLeft(), notNullValue());
        assertEquals(entityArgumentCaptor.getValue().getOnBoardingStatus().getCheckpoint(), CheckPoints.PEP_FINISHED.name());

    }

    private UpdatePepRequest buildUpdatePepRequest() {
        UpdatePepRequest updatePepRequest = new UpdatePepRequest();
        updatePepRequest.setIdClient("idClient");
        updatePepRequest.setPep(true);
        return updatePepRequest;
    }

    private UpdatePepRequest buildUpdatePepRequestIsNotPep() {
        UpdatePepRequest updatePepRequest = new UpdatePepRequest();
        updatePepRequest.setIdClient("idClient");
        updatePepRequest.setPep(false);
        return updatePepRequest;
    }
}
