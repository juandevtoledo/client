package com.lulobank.clients.starter.v3.adapters.out.firebase;

import static com.lulobank.clients.services.utils.LogMessages.ERROR_FIREBASE_CLIENT;
import static com.lulobank.clients.services.utils.LogMessages.ERROR_UPDATE_FIREBASE;
import static com.lulobank.clients.services.utils.StatusClientVerificationFirebaseEnum.CREATED;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.firebase.database.DatabaseReference;
import com.lulobank.clients.services.utils.ClientVerificationFirebase;
import com.lulobank.clients.services.utils.Constants;
import com.lulobank.clients.v3.adapters.port.out.firebase.UserStateRepository;

import io.vavr.control.Try;
import lombok.CustomLog;

@CustomLog
public class UserStateFirebaseRepository implements UserStateRepository {

	private static final String CLIENT_VERIFICATION_KEY = "clientVerification";
	private static final String REFERENCE_ON_BOARDING_FIREBASE_CLIENTS = "on_boarding/clients";

	private final DatabaseReference databaseReference;

	public UserStateFirebaseRepository(DatabaseReference databaseReference) {
		this.databaseReference = databaseReference;
	}

	@Override
	public void notifyCreated(String idClient, String productSelected) {

		Map<String, Object> users = new HashMap<>();
		users.put(CLIENT_VERIFICATION_KEY, new ClientVerificationFirebase(productSelected, CREATED.name()));

		Try.of(() -> databaseReference.child(REFERENCE_ON_BOARDING_FIREBASE_CLIENTS).child(idClient))
				.onFailure(error -> log.error(ERROR_FIREBASE_CLIENT.getMessage(), error.getMessage(), idClient))
				.mapTry(databaseReference -> databaseReference.updateChildrenAsync(users)
						.get(Constants.TIME_OUT_FIREBASE, TimeUnit.SECONDS))
				.onFailure(error -> log.error(ERROR_UPDATE_FIREBASE.getMessage(), error.getMessage(), idClient))
				.onSuccess(success -> log.info("Client update created in firebase, clientId : {} ", idClient));
	}
}
