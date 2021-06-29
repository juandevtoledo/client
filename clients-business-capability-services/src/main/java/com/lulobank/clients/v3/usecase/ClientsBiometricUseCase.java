package com.lulobank.clients.v3.usecase;

import com.lulobank.clients.services.actions.MessageToSQSUpdateIdBiometricIdentity;
import com.lulobank.clients.services.features.identitybiometric.model.UpdateIdTransactionBiometric;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.clients.v3.adapters.port.out.firebase.UserStateRepository;
import com.lulobank.clients.v3.usecase.command.ClientBiometricError;
import com.lulobank.clients.v3.usecase.command.ClientBiometricIdTransactionRequest;
import com.lulobank.clients.v3.usecase.mapper.IdentityBiometricMapper;
import com.lulobank.clients.v3.util.UseCase;

import io.vavr.control.Either;
import lombok.CustomLog;

@CustomLog
public class ClientsBiometricUseCase
		implements UseCase<ClientBiometricIdTransactionRequest, Either<ClientBiometricError, Boolean>> {

	private final ClientsV3Repository clientsV3Repository;
	private final UserStateRepository updateFirebaseState;
	private final MessageToSQSUpdateIdBiometricIdentity sqsUpdateIdBiometricIdentity;

	public ClientsBiometricUseCase(ClientsV3Repository clientsV3Repository, UserStateRepository updateFirebaseState,
			MessageToSQSUpdateIdBiometricIdentity sqsUpdateIdBiometricIdentity) {
		this.clientsV3Repository = clientsV3Repository;
		this.updateFirebaseState = updateFirebaseState;
		this.sqsUpdateIdBiometricIdentity = sqsUpdateIdBiometricIdentity;
	}

	@Override
	public Either<ClientBiometricError, Boolean> execute(
			ClientBiometricIdTransactionRequest clientBiometricIdTransaction) {
		return clientsV3Repository.findByIdClient(clientBiometricIdTransaction.getIdClient()).toTry()
				.peek(this::notifyFirebase).peek(clientsV3Entity -> log.info("Client found"))
				.map(clientsV3Entity -> setIdentityBiometric(clientsV3Entity, clientBiometricIdTransaction))
				.flatMap(clientsV3Entity -> clientsV3Repository.save(clientsV3Entity))
				.map(clientsV3Entity -> Boolean.TRUE)
				.peek(response -> sendNotificationUpdateIdBiometricIdentity(clientBiometricIdTransaction))
				.onFailure(ex -> log.error(String.format("Error updating biometric id. ClientId: %s", clientBiometricIdTransaction.getIdClient()), ex))
				.toEither(() -> new ClientBiometricError(String.format("Update client biometric failed. IdClient %s ",
						clientBiometricIdTransaction.getIdClient())));
	}

	private void sendNotificationUpdateIdBiometricIdentity(
			ClientBiometricIdTransactionRequest clientBiometricIdTransaction) {

		UpdateIdTransactionBiometric updateIdTransactionBiometric = new UpdateIdTransactionBiometric();
		updateIdTransactionBiometric.setIdClient(clientBiometricIdTransaction.getIdClient());
		updateIdTransactionBiometric
				.setIdTransactionBiometric(clientBiometricIdTransaction.getIdTransactionBiometric());

		sqsUpdateIdBiometricIdentity.run(null, updateIdTransactionBiometric);
	}

	private ClientsV3Entity setIdentityBiometric(ClientsV3Entity clientsV3Entity,
			ClientBiometricIdTransactionRequest clientBiometricIdTransaction) {
		clientsV3Entity.setIdentityBiometric(
				IdentityBiometricMapper.INSTANCE.identityBiometricFrom(clientBiometricIdTransaction));
		clientsV3Entity.setResetBiometric(false);
		clientsV3Entity.setIdentityBiometricId(clientBiometricIdTransaction.getIdTransactionBiometric());
		return clientsV3Entity;
	}

	private ClientsV3Entity notifyFirebase(ClientsV3Entity clientsV3Entity) {
		updateFirebaseState.notifyCreated(clientsV3Entity.getIdClient(),
				clientsV3Entity.getOnBoardingStatus().getProductSelected());
		return clientsV3Entity;
	}
}
