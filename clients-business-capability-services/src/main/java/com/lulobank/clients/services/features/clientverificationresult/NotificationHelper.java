package com.lulobank.clients.services.features.clientverificationresult;

import com.lulobank.clients.sdk.operations.util.CheckPoints;
import com.lulobank.clients.services.outboundadapters.ClientsOutboundAdapter;
import com.lulobank.clients.services.utils.ClientVerificationFirebase;
import com.lulobank.clients.services.utils.Constants;
import com.lulobank.clients.services.utils.LogMessages;
import com.lulobank.clients.services.utils.StatusClientVerificationFirebaseEnum;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import io.vavr.control.Option;
import lombok.extern.slf4j.Slf4j;
import org.owasp.encoder.Encode;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static com.lulobank.clients.services.utils.BiometricResultCodes.isCodeFailedBiometricFraud;
import static com.lulobank.clients.services.utils.ClientHelper.CLIENT_VERIFICATION;
import static com.lulobank.clients.services.utils.ClientHelper.getDatabaseReferenceOnBoarding;
import static com.lulobank.clients.services.utils.StatusClientVerificationFirebaseEnum.BLACKLIST_FAILED;
import static com.lulobank.clients.services.utils.StatusClientVerificationFirebaseEnum.FAILED;
import static com.lulobank.clients.services.utils.StatusClientVerificationFirebaseEnum.KO_ALREADY_CLIENT_EXISTS;
import static com.lulobank.clients.services.utils.StatusClientVerificationFirebaseEnum.KO_DIGITAL_EVIDENCE;
import static com.lulobank.clients.services.utils.StatusClientVerificationFirebaseEnum.KO_IDENTITY;
import static com.lulobank.clients.services.utils.StatusClientVerificationFirebaseEnum.KO_IDENTITY_FRAUD;
import static com.lulobank.clients.services.utils.StatusClientVerificationFirebaseEnum.KO_SAVINGS_ACCOUNT_CREATION;
import static com.lulobank.clients.services.utils.StatusClientVerificationFirebaseEnum.KO_ZENDESK_USER_CREATION;
import static com.lulobank.clients.services.utils.StatusClientVerificationFirebaseEnum.OK;
import static com.lulobank.clients.services.utils.StatusClientVerificationFirebaseEnum.START_PEP_VALIDATION;

@Slf4j
public class NotificationHelper {

    protected static Consumer<ClientsV3Entity> notifyBlackListReject(ClientsOutboundAdapter clientsOutboundAdapter,
                                                                     StatusClientVerificationFirebaseEnum statusFirebase) {
        return clientEntity -> {
            Map<String, Object> msg = getMap(firebaseStatus(clientEntity, statusFirebase));
            notify(clientsOutboundAdapter, clientEntity.getIdClient(), msg);
            log.info("Client rejected by blacklist status, status : {}, IdTransactionBiometric: {} , idClient: {} ",
                    clientEntity.getBlackListState(), Encode.forJava(clientEntity.getIdentityBiometric().getIdTransaction()),
                    Encode.forJava(clientEntity.getIdClient()));
        };
    }

    protected static Consumer<ClientsV3Entity> notifyBlackListFailed(ClientsOutboundAdapter clientsOutboundAdapter) {
        return clientEntity -> {
            Map<String, Object> msg = getMap(firebaseStatus(clientEntity, BLACKLIST_FAILED));
            notify(clientsOutboundAdapter, clientEntity.getIdClient(), msg);
            log.info("Blacklist validation failed, status : {}, IdTransactionBiometric: {} , idClient: {} ",
                    clientEntity.getBlackListState(), Encode.forJava(clientEntity.getIdentityBiometric().getIdTransaction()),
                    Encode.forJava(clientEntity.getIdClient()));
        };
    }

    protected static Consumer<ClientsV3Entity> notifyBiometricReject(ClientsOutboundAdapter clientsOutboundAdapter) {
        return clientEntity -> {
            Map<String, Object> msg = getMap(firebaseStatus(clientEntity, getFailedBiometricStatus(clientEntity)));
            notify(clientsOutboundAdapter, clientEntity.getIdClient(), msg);
            log.info("Client rejected by biometric status : {}, IdTransactionBiometric: {} , idClient: {} ",
                    clientEntity.getIdentityBiometric().getTransactionState().getStateName(),
                    Encode.forJava(clientEntity.getIdentityBiometric().getIdTransaction()), Encode.forJava(clientEntity.getIdClient()));
        };
    }

    public static Consumer<ClientsV3Entity> notifyDigitalEvidenceError(ClientsOutboundAdapter clientsOutboundAdapter) {
        return clientEntity -> {
            Map<String, Object> msg = getMap(firebaseStatus(clientEntity, KO_DIGITAL_EVIDENCE));
            notify(clientsOutboundAdapter, clientEntity.getIdClient(), msg);
            log.info("Client without digital evidence : idClient {}.", Encode.forJava(clientEntity.getIdClient()));
        };
    }

    protected static Consumer<ClientsV3Entity> notifyPepValidation(ClientsOutboundAdapter clientsOutboundAdapter){
        return clientEntity -> {
            Map<String, Object> msg = getMap(firebaseStatus(clientEntity, START_PEP_VALIDATION));
            notify(clientsOutboundAdapter, clientEntity.getIdClient(), msg);
            log.info("send event start_pep_validation to firebase : idClient {}, checkpoint {}", Encode.forJava(clientEntity.getIdClient()),
                    CheckPoints.BLACKLIST_FINISHED.name());
        };
    }

    public static Consumer<ClientsV3Entity> notifyCreateCustomerError(ClientsOutboundAdapter clientsOutboundAdapter) {
        return clientEntity -> {
            Map<String, Object> msg = getMap(firebaseStatus(clientEntity, KO_ZENDESK_USER_CREATION));
            notify(clientsOutboundAdapter, clientEntity.getIdClient(), msg);
            log.info("Client without customer service data : idClient {}.", Encode.forJava(clientEntity.getIdClient()));
        };
    }

    protected static Consumer<ClientsV3Entity> notifyIdCardExist(ClientsOutboundAdapter clientsOutboundAdapter) {
        return clientEntity -> {
            Map<String, Object> msg = getMap(firebaseStatus(clientEntity, KO_ALREADY_CLIENT_EXISTS));
            notify(clientsOutboundAdapter, clientEntity.getIdClient(), msg);
            log.info("Client rejected by Id Card Exists, IdTransactionBiometric: {} , idClient: {} ",
                    Encode.forJava(clientEntity.getIdentityBiometric().getIdTransaction()),
                    Encode.forJava(clientEntity.getIdClient()));
        };
    }

    public static Consumer<ClientsV3Entity> notifySavingAccountCreated(ClientsOutboundAdapter clientsOutboundAdapter) {
        return clientsV3Entity -> {
            Map<String, Object> msg = getMap(firebaseStatus(clientsV3Entity, OK));
            notify(clientsOutboundAdapter, clientsV3Entity.getIdClient(), msg);
            log.info(
                    "Saving account created , update in firebase, idClient: {}", Encode.forJava(clientsV3Entity.getIdClient()));
        };
    }

    public static Consumer<ClientsV3Entity> notifyCreateSavingAccountError(ClientsOutboundAdapter clientsOutboundAdapter) {
        return clientEntity -> {
            Map<String, Object> msg = getMap(firebaseStatus(clientEntity, KO_SAVINGS_ACCOUNT_CREATION));
            notify(clientsOutboundAdapter, clientEntity.getIdClient(), msg);
            log.info("Error creating saving account for client : idClient {}.", Encode.forJava(clientEntity.getIdClient()));
        };
    }

    public static void notifyErrorSendingRisk(ClientsOutboundAdapter clientsOutboundAdapter, ClientsV3Entity clientEntity) {
        Map<String, Object> users = getMap(firebaseStatus(clientEntity, FAILED));
        getDatabaseReferenceOnBoarding(clientsOutboundAdapter, clientEntity.getIdClient())
                .mapTry(databaseReference -> databaseReference.updateChildrenAsync(users).get(Constants.TIME_OUT_FIREBASE, TimeUnit.SECONDS))
                .onFailure(error -> log.error(LogMessages.ERROR_UPDATE_FIREBASE.getMessage(), error.getMessage(), clientEntity.getIdClient()));
        log.error("It's Not possible to send event to Risk Engine, IdTransactionBiometric: {} , idClient: {}",
                Encode.forJava(clientEntity.getIdentityBiometric().getIdTransaction()), Encode.forJava(clientEntity.getIdClient()));
    }

    private static StatusClientVerificationFirebaseEnum getFailedBiometricStatus(ClientsV3Entity clientEntity) {
        return Option.of(clientEntity.getIdentityBiometric().getTransactionState())
                .filter(transactionState -> isCodeFailedBiometricFraud.test(transactionState.getId()))
                .map(transactionState -> KO_IDENTITY_FRAUD)
                .getOrElse(KO_IDENTITY);
    }

    private static ClientVerificationFirebase firebaseStatus(
            ClientsV3Entity clientEntity, StatusClientVerificationFirebaseEnum status) {
        return new ClientVerificationFirebase(
                clientEntity.getOnBoardingStatus().getProductSelected(),
                status.name());
    }

    private static Map<String, Object> getMap(ClientVerificationFirebase firebaseStatus) {
        Map<String, Object> users = new HashMap<>();
        users.put(CLIENT_VERIFICATION, firebaseStatus);
        return users;
    }

    private static void notify(ClientsOutboundAdapter clientsOutboundAdapter, String idClient, Map<String, Object> msg) {
        getDatabaseReferenceOnBoarding(clientsOutboundAdapter, idClient)
                .map(databaseReference -> databaseReference.updateChildrenAsync(msg))
                .mapTry(voidApiFuture -> voidApiFuture.get(Constants.TIME_OUT_FIREBASE, TimeUnit.SECONDS))
                .onFailure(error -> log.error(LogMessages.ERROR_UPDATE_FIREBASE.getMessage(), error.getMessage(), idClient));
    }

}
