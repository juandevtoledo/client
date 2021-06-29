package com.lulobank.clients.v3.usecase;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.clients.v3.usecase.command.ClientDemographicError;
import com.lulobank.clients.v3.usecase.command.ClientDemographicInfo;

import io.vavr.control.Either;
import io.vavr.control.Option;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ClientsDemographicUseCaseTest {
	
	private ClientsDemographicUseCase subject;
	
	@Mock
	private ClientsV3Repository clientsV3Repository;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		subject = new ClientsDemographicUseCase(clientsV3Repository);
	}
	
	@Test
	public void getClientDemographicInfoSuccess() {
		String idClient = "3455223";
		ClientsV3Entity clientsV3Entity = new ClientsV3Entity();
		when(clientsV3Repository.findByIdClient(idClient)).thenReturn(Option.of(clientsV3Entity));
		Either<ClientDemographicError, ClientDemographicInfo> result = subject.execute(idClient);
		assertTrue(result.isRight());
		assertTrue(result.isLeft() == false);
		assertTrue(result.isEmpty() == false);
	}
	
	@Test
	public void getClientDemographicInfoFailure() {
		String idClient = "345122";
		when(clientsV3Repository.findByIdClient(idClient)).thenReturn(Option.of(null));
		Either<ClientDemographicError, ClientDemographicInfo> result = subject.execute(idClient);
		assertTrue(result.isRight() == false);
		assertTrue(result.isLeft());
		assertTrue(result.isEmpty());
	}
}
