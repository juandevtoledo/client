package com.lulobank.clients.services.features.initialclient.actions;

import static com.lulobank.clients.services.utils.ClientHelper.getDatabaseReferenceOnBoarding;

import com.lulobank.clients.services.features.initialclient.model.CreateInitialClient;
import com.lulobank.clients.services.features.initialclient.model.InitialClientCreated;
import com.lulobank.clients.services.outboundadapters.ClientsOutboundAdapter;
import com.lulobank.clients.services.utils.ClientVerificationFirebase;
import com.lulobank.clients.services.utils.Constants;
import com.lulobank.clients.services.utils.LogMessages;
import com.lulobank.clients.services.utils.StatusClientVerificationFirebaseEnum;
import com.lulobank.core.Response;
import com.lulobank.core.actions.Action;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.owasp.encoder.Encode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateClientFirebaseOnBoarding
        implements Action<Response<InitialClientCreated>, CreateInitialClient> {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(CreateClientFirebaseOnBoarding.class);
    private ClientsOutboundAdapter clientsOutboundAdapter;

    public CreateClientFirebaseOnBoarding(ClientsOutboundAdapter clientsOutboundAdapter) {
        this.clientsOutboundAdapter = clientsOutboundAdapter;
    }

    @Override
    public void run(
            Response<InitialClientCreated> initialClientCreatedResponse,
            CreateInitialClient createInitialClient) {

        Map<String, Object> users = new HashMap<>();
        users.put(
                "clientVerification",
                new ClientVerificationFirebase(
                        createInitialClient.getSelectedProduct().name(),
                        StatusClientVerificationFirebaseEnum.CREATED.name()));

        getDatabaseReferenceOnBoarding(clientsOutboundAdapter, initialClientCreatedResponse.getContent().getIdClient())
                .map(databaseReference -> databaseReference.updateChildrenAsync(users))
                .mapTry(voidApiFuture -> voidApiFuture.get(Constants.TIME_OUT_FIREBASE, TimeUnit.SECONDS))
                .onFailure(error -> LOGGER.error(LogMessages.ERROR_UPDATE_FIREBASE.getMessage(), error.getMessage(), initialClientCreatedResponse.getContent().getIdClient()))
                .onSuccess(success -> LOGGER.info(
                        LogMessages.CLIENT_CREATED_FIREBASE.getMessage(),
                        Encode.forJava(initialClientCreatedResponse.getContent().getIdClient())));

    }
}
