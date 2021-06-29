package com.lulobank.clients.services.features.identitybiometric;

import com.amazonaws.SdkClientException;
import com.lulobank.clients.services.features.identitybiometric.mapper.IdentityBiometricMapper;
import com.lulobank.clients.services.features.identitybiometric.model.UpdateIdTransactionBiometric;
import com.lulobank.clients.services.outboundadapters.ClientsOutboundAdapter;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.utils.ClientVerificationFirebase;
import com.lulobank.clients.services.utils.Constants;
import com.lulobank.clients.services.utils.LogMessages;
import com.lulobank.core.Handler;
import com.lulobank.core.Response;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.owasp.encoder.Encode;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import static com.lulobank.clients.services.utils.ClientHelper.CLIENT_VERIFICATION;
import static com.lulobank.clients.services.utils.ClientHelper.getDatabaseReferenceOnBoarding;
import static com.lulobank.clients.services.utils.HttpCodes.BAD_GATEWAY;
import static com.lulobank.clients.services.utils.HttpCodes.NOT_FOUND;
import static com.lulobank.clients.services.utils.StatusClientVerificationFirebaseEnum.CREATED;
import static com.lulobank.core.utils.ValidatorUtils.getListValidations;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

@Slf4j
public class UpdateIdTransactionBiometricHandler
        implements Handler<Response, UpdateIdTransactionBiometric> {

    private ClientsOutboundAdapter clientsOutboundAdapter;

    public UpdateIdTransactionBiometricHandler(ClientsOutboundAdapter clientsOutboundAdapter) {
        this.clientsOutboundAdapter = clientsOutboundAdapter;
    }

    @Override
    public Response<Boolean> handle(UpdateIdTransactionBiometric request) {
        return Try.of(() -> updateIdBiometric(request))
                .recover(SdkClientException.class, handlerSdkException(request))
                .get();
    }

    public Response<Boolean> updateIdBiometric(UpdateIdTransactionBiometric request) {
        return getClient(request)
                .peek(notifyFirebase())
                .map(setIdentityBiometric(request))
                .peek(update())
                .map(clientEntity -> new Response<>(TRUE))
                .getOrElse(() -> getResponse("client Not found", NOT_FOUND));
    }

    public Option<ClientEntity> getClient(UpdateIdTransactionBiometric request) {
        return Option.ofOptional(clientsOutboundAdapter
                .getClientsRepository()
                .findByIdClient(request.getIdClient()));
    }

    public UnaryOperator<ClientEntity> setIdentityBiometric(UpdateIdTransactionBiometric request) {
        return clientEntity ->{
            clientEntity.setIdentityBiometric(IdentityBiometricMapper.INSTANCE.identityBiometricFrom(request));
            clientEntity.setResetBiometric(FALSE);
            return clientEntity;
        };
    }

    public Consumer<ClientEntity> update() {
        return clientEntity -> clientsOutboundAdapter.getClientsRepository().save(clientEntity);
    }


    private Consumer<ClientEntity> notifyFirebase() {
        return clientEntity -> {
            Map<String, Object> users = new HashMap<>();
            users.put(
                    CLIENT_VERIFICATION,
                    new ClientVerificationFirebase(
                            clientEntity.getOnBoardingStatus().getProductSelected(), CREATED.name()));
            getDatabaseReferenceOnBoarding(clientsOutboundAdapter, clientEntity.getIdClient())
                    .mapTry(databaseReference -> databaseReference.updateChildrenAsync(users).get(Constants.TIME_OUT_FIREBASE, TimeUnit.SECONDS))
                    .onFailure(error -> log.error(LogMessages.ERROR_UPDATE_FIREBASE.getMessage(), error.getMessage(), clientEntity.getIdClient()));
            log.info("Client update created in firebase, clientId : {} ", Encode.forJava(clientEntity.getIdClient()));
        };
    }

    public Function<SdkClientException, Response<Boolean>> handlerSdkException(UpdateIdTransactionBiometric request) {
        return ex -> {
            log.error("Error searching in  Database,  msg: {} , clientId : {} ", ex.getMessage(), Encode.forJava(request.getIdClient()));
            return getResponse("Error in Database", BAD_GATEWAY);
        };
    }

    private Response getResponse(String msg, String code) {
        return new Response<>(
                getListValidations(msg, code));
    }

}
