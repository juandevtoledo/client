package com.lulobank.clients.v3.usecase.pep;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.clients.v3.usecase.command.PepError;
import com.lulobank.clients.v3.usecase.command.UpdatePepRequest;
import com.lulobank.clients.v3.usecase.command.UpdatePepResponse;

import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;

public class UpdatePepUseCaseTest {
	
	private UpdatePepUseCase updatePepUseCase;

	@Mock
	private ClientsV3Repository clientsV3Repository;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		updatePepUseCase = new UpdatePepUseCase(clientsV3Repository);
	}
	
	@Test
	public void ShouldUpdatePepSuccess() {
		UpdatePepRequest updatePepRequest = buildUpdatePepRequest();
		ClientsV3Entity clientsV3Entity = buildClientsV3Entity(updatePepRequest.getIdClient());
		when(clientsV3Repository.findByIdClient(updatePepRequest.getIdClient())).thenReturn(Option.of(clientsV3Entity));
		when(clientsV3Repository.save(setPep(clientsV3Entity))).thenReturn(Try.of(() -> clientsV3Entity));
		
		Either<PepError, UpdatePepResponse> result = updatePepUseCase.execute(updatePepRequest);
		assertThat(result.isRight(), is(true));
		assertThat(result.isLeft(), is(false));
		assertThat(result.isEmpty(), is(false));
	}
	
	@Test
	public void ShouldUpdatePepClientNotFound() {
		UpdatePepRequest updatePepRequest = buildUpdatePepRequest();
		when(clientsV3Repository.findByIdClient(updatePepRequest.getIdClient())).thenReturn(Option.of(null));
		
		Either<PepError, UpdatePepResponse> result = updatePepUseCase.execute(updatePepRequest);
		assertThat(result.isRight(), is(false));
		assertThat(result.isLeft(), is(true));
		assertThat(result.isEmpty(), is(true));
		assertThat(result.getLeft(), notNullValue());
	}
	
	@Test
	public void ShouldUpdatePepSaveClientFailed() {
		UpdatePepRequest updatePepRequest = buildUpdatePepRequest();
		ClientsV3Entity clientsV3Entity = buildClientsV3Entity(updatePepRequest.getIdClient());
		when(clientsV3Repository.findByIdClient(updatePepRequest.getIdClient())).thenReturn(Option.of(clientsV3Entity));
		when(clientsV3Repository.save(setPep(clientsV3Entity))).thenReturn(Try.failure(new RuntimeException()));
		
		Either<PepError, UpdatePepResponse> result = updatePepUseCase.execute(updatePepRequest);
		assertThat(result.isRight(), is(false));
		assertThat(result.isLeft(), is(true));
		assertThat(result.isEmpty(), is(true));
		assertThat(result.getLeft(), notNullValue());
	}
	
	private ClientsV3Entity setPep(ClientsV3Entity clientsV3Entity) {
		clientsV3Entity.setPep(PepStatus.PEP_WAIT_LIST.value());
		return clientsV3Entity;
	}

	private ClientsV3Entity buildClientsV3Entity(String idClient) {
		ClientsV3Entity clientsV3Entity = new ClientsV3Entity();
		clientsV3Entity.setIdClient(idClient);
		return clientsV3Entity;
	}

	private UpdatePepRequest buildUpdatePepRequest() {
		UpdatePepRequest updatePepRequest = new UpdatePepRequest();
		updatePepRequest.setIdClient("idClient");
		updatePepRequest.setPep(true);
		return updatePepRequest;
	}

}
