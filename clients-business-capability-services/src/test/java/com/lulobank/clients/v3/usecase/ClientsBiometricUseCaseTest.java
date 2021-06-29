package com.lulobank.clients.v3.usecase;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.lulobank.clients.services.actions.MessageToSQSUpdateIdBiometricIdentity;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.OnBoardingStatusV3;
import com.lulobank.clients.v3.adapters.port.out.firebase.UserStateRepository;
import com.lulobank.clients.v3.usecase.command.ClientBiometricError;
import com.lulobank.clients.v3.usecase.command.ClientBiometricIdTransactionRequest;

import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;

public class ClientsBiometricUseCaseTest {
	
	private ClientsBiometricUseCase subject;
	
	@Mock
	private ClientsV3Repository clientsV3Repository;
	@Mock
	private MessageToSQSUpdateIdBiometricIdentity sqsUpdateIdBiometricIdentity;
	@Mock
	private UserStateRepository updateFirebaseState;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		subject = new ClientsBiometricUseCase(clientsV3Repository, updateFirebaseState, sqsUpdateIdBiometricIdentity);
	}
	
	@Test
	public void updateClientBiometricSuccess() throws InterruptedException, ExecutionException, TimeoutException {
		
		ClientBiometricIdTransactionRequest clientBiometricIdTransaction = buildClientBiometricIdTransactionRequest();
		ClientsV3Entity clientsV3Entity = buildClientEntity();
		when(clientsV3Repository.findByIdClient(clientBiometricIdTransaction.getIdClient())).thenReturn(Option.of(clientsV3Entity));
		when(clientsV3Repository.save(isA(ClientsV3Entity.class))).thenReturn(Try.of(() -> clientsV3Entity));
		when(clientsV3Repository.findByIdClient(clientBiometricIdTransaction.getIdClient())).thenReturn(Option.of(clientsV3Entity));
		
		Either<ClientBiometricError, Boolean> result = subject.execute(clientBiometricIdTransaction);
		assertTrue(result.isRight());
		assertTrue(result.isLeft() == false);
		assertTrue(result.isEmpty() == false);
		verify(sqsUpdateIdBiometricIdentity).run(any(), any());
		verify(updateFirebaseState).notifyCreated(clientsV3Entity.getIdClient(), clientsV3Entity.getOnBoardingStatus().getProductSelected());
	}

	@Test
	public void getClientDemographicInfoFailure() {
		ClientBiometricIdTransactionRequest clientBiometricIdTransaction = buildClientBiometricIdTransactionRequest();
		when(clientsV3Repository.findByIdClient(clientBiometricIdTransaction.getIdClient())).thenReturn(Option.of(null));
		Either<ClientBiometricError, Boolean> result = subject.execute(clientBiometricIdTransaction);
		assertTrue(result.isRight() == false);
		assertTrue(result.isLeft());
		assertTrue(result.isEmpty());
	}
	
	private ClientsV3Entity buildClientEntity() {
		OnBoardingStatusV3 onBoardingStatusV3 = new OnBoardingStatusV3();
		onBoardingStatusV3.setProductSelected("productSelected");
		ClientsV3Entity clientsV3Entity = new ClientsV3Entity();
		clientsV3Entity.setIdClient("idClient");
		clientsV3Entity.setOnBoardingStatus(onBoardingStatusV3);
		return clientsV3Entity;
	}
	
	private ClientBiometricIdTransactionRequest buildClientBiometricIdTransactionRequest() {
		ClientBiometricIdTransactionRequest clientBiometricIdTransactionRequest = new ClientBiometricIdTransactionRequest();
		clientBiometricIdTransactionRequest.setIdClient("idClient");
		clientBiometricIdTransactionRequest.setIdTransactionBiometric("idTransactionBiometric");
		return clientBiometricIdTransactionRequest;
	}
}
