package com.lulobank.clients.v3.usecase.pep;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import com.lulobank.clients.v3.usecase.pep.GetPepUseCase;
import com.lulobank.clients.v3.usecase.pep.PepStatus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.clients.v3.usecase.command.GetPepResponse;
import com.lulobank.clients.v3.usecase.command.PepError;

import io.vavr.control.Either;
import io.vavr.control.Option;

import java.time.LocalDateTime;

public class GetPepUseCaseTest {
	
	private GetPepUseCase subject;
	
	@Mock
	private ClientsV3Repository clientsV3Repository;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		subject = new GetPepUseCase(clientsV3Repository);
	}
	
	@Test
	public void shouldGetPepSuccessWithoutData() {
		String idClient = "2er34";
		ClientsV3Entity clientsV3Entity = buildClientsV3Entity(idClient);
		when(clientsV3Repository.findByIdClient(idClient)).thenReturn(Option.of(clientsV3Entity));
		Either<PepError, GetPepResponse> result = subject.execute(idClient);
		assertThat(result.isRight(), is(true));
		assertThat(result.get().getPep(), is(PepStatus.EMPTY_PEP.value()));
		assertThat(result.isLeft(), is(false));
		assertThat(result.isEmpty(), is(false));
	}

	@Test
	public void shouldGetPepSuccessWithData() {
		String idClient = "2er34";
		ClientsV3Entity clientsV3Entity = buildClientsV3Entity(idClient);
		clientsV3Entity.setPep(PepStatus.PEP_WAIT_LIST.value());
		clientsV3Entity.setDateResponsePep(LocalDateTime.now());
		when(clientsV3Repository.findByIdClient(idClient)).thenReturn(Option.of(clientsV3Entity));
		Either<PepError, GetPepResponse> result = subject.execute(idClient);
		assertThat(result.isRight(), is(true));
		assertThat(result.get().getPep(), is(PepStatus.PEP_WAIT_LIST.value()));
		assertThat(result.isLeft(), is(false));
		assertThat(result.isEmpty(), is(false));
	}
	
	@Test
	public void ShouldUpdatePepClientNotFound() {
		String idClient = "2er3434v4";
		when(clientsV3Repository.findByIdClient(idClient)).thenReturn(Option.of(null));
		
		Either<PepError, GetPepResponse> result = subject.execute(idClient);
		assertThat(result.isRight(), is(false));
		assertThat(result.isLeft(), is(true));
		assertThat(result.isEmpty(), is(true));
		assertThat(result.getLeft(), notNullValue());
	}
	
	private ClientsV3Entity buildClientsV3Entity(String idClient) {
		ClientsV3Entity clientsV3Entity = new ClientsV3Entity();
		clientsV3Entity.setIdClient(idClient);
		return clientsV3Entity;
	}
}
