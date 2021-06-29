package com.lulobank.clients.services.utils;

import static com.lulobank.clients.services.features.onboardingclients.OnboardingClientsConverter.createClientVerificationFirebaseFailFromEntity;
import static com.lulobank.clients.services.features.onboardingclients.OnboardingClientsConverter.createClientVerificationFirebaseKOIdentityFromEntity;
import static com.lulobank.clients.services.features.onboardingclients.OnboardingClientsConverter.createIdentityInformationByClientEntity;
import static com.lulobank.clients.services.features.onboardingclients.OnboardingClientsConverter.createLoanRequestedStatusFirebaseFail;
import static com.lulobank.clients.services.features.onboardingclients.OnboardingClientsConverter.getCreateSavingsAccountRequestFromEntity;
import static com.lulobank.clients.services.utils.ProductTypeEnum.isProductCredit;
import static java.lang.Boolean.FALSE;

import com.google.firebase.database.DatabaseReference;
import com.lulobank.clients.services.events.IdentityInformation;
import com.lulobank.clients.services.features.riskengine.model.ClientCreated;
import com.lulobank.clients.services.outboundadapters.ClientsOutboundAdapter;
import com.lulobank.clients.services.outboundadapters.model.*;
import com.lulobank.core.Response;
import com.lulobank.savingsaccounts.sdk.dto.createsavingsaccount.CreateSavingsAccountRequest;
import com.lulobank.savingsaccounts.sdk.dto.createsavingsaccount.CreateSavingsAccountResponse;
import com.nimbusds.jose.JOSEException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

import io.vavr.control.Try;
import org.apache.commons.lang3.StringUtils;
import org.owasp.encoder.Encode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientHelper.class);

    public static final String REFERENCE_ON_BOARDING_FIREBASE_CLIENTS = "on_boarding/clients";
    public static final String REFERENCE_LOAN_REQUESTED_FIREBASE_CLIENTS = "loan_request/clients";
    public static final String LOANREQUESTED_VERIFICATION = "loanVerification";
    public static final String ID_CLIENT_KEY = "ID_CLIENT";
    public static final String BEARER = "Bearer ";
    public static final String CLIENT_VERIFICATION = "clientVerification";


    public static final Predicate<ClientEntity> isOnboardingProductSelectedSavingsAccount =
            (entity) ->
                    ProductTypeEnum.SAVING_ACCOUNT
                            .name()
                            .equals(entity.getOnBoardingStatus().getProductSelected());
    public static final Predicate<ClientEntity> isOnboardingProductSelectedCredit =
            (entity) ->
                    isProductCredit.test(
                            ProductTypeEnum.valueOf(entity.getOnBoardingStatus().getProductSelected()));

    public static final Predicate<ClientEntity> isLoanRequestedFromHome =
            (entity) -> Objects.nonNull(entity.getLoanRequested());

    public static final Consumer<ClientEntity> setOnboardingStatusToClientSaving =
            clientEntity ->
                    clientEntity.setOnBoardingStatus(
                            new OnBoardingStatus(
                                    StatusClientVerificationFirebaseEnum.CREATED.name(),
                                    ProductTypeEnum.SAVING_ACCOUNT.name()));

    public static final BiConsumer<ClientEntity, ClientsOutboundAdapter> notifyRiskEngine =
            (clientEntity, clientsOutboundAdapter) -> {
                if (isOnboardingProductSelectedCredit.or(isLoanRequestedFromHome).test(clientEntity)) {
                    ClientCreated clientCreated = new ClientCreated(clientEntity.getIdClient());
                    IdentityInformation identityInformation =
                            createIdentityInformationByClientEntity(clientEntity);
                    clientsOutboundAdapter
                            .getMessageToNotifySQSRiskEngine()
                            .run(new Response(clientCreated), identityInformation);
                    LOGGER.info(
                            LogMessages.EVENT_SENT_TO_RISK_ENGINE.getMessage(), Encode.forJava(clientEntity.getIdClient()));
                }
            };

    public static final BiConsumer<ClientEntity, ClientsOutboundAdapter> createSavingAccount =
            (clientEntity, clientsOutboundAdapter) -> {
                if (isOnboardingProductSelectedSavingsAccount.test(clientEntity)) {
                    createSavingAccount(
                            getHeadersClientCredentials(clientsOutboundAdapter, clientEntity.getIdClient()),
                            clientEntity,
                            clientsOutboundAdapter);
                }
            };

    public static Map<String, String> getHeadersClientCredentials(
            ClientsOutboundAdapter clientsOutboundAdapter, String idClient) {
        Map<String, String> headers = new HashMap<>();
        try {
            String accessToken =
                    clientsOutboundAdapter.getLuloUserTokenGenerator().getUserToken(idClient);
            headers.put("authorization", BEARER + accessToken);
        } catch (JOSEException e) {
            LOGGER.error(LogMessages.ERROR_GETTING_HEADER.getMessage(), Encode.forJava(idClient), e);
        }
        return headers;
    }

    public static final void createSavingAccount(
            Map<String, String> headers,
            ClientEntity clientEntity,
            ClientsOutboundAdapter clientsOutboundAdapter) {
        CreateSavingsAccountRequest savingAccountToCreate =
                getCreateSavingsAccountRequestFromEntity(clientEntity);
        Response<CreateSavingsAccountResponse> response =
                clientsOutboundAdapter
                        .getSavingsAccount()
                        .createSavingsAccount(headers, savingAccountToCreate);
        clientEntity.setIdCbs(response.getContent().getIdCbs());
    }

    public static final BiConsumer<ClientsOutboundAdapter, Map<String, Object>>
            notifyLoanFromHomeCreated =
            (clientsOutboundAdapter, users) -> {
                if (users.containsKey(ID_CLIENT_KEY)) {
                    String idClient = users.get(ID_CLIENT_KEY).toString();
                    users.remove(ID_CLIENT_KEY);
                    getDatabaseReferenceLoanRequested(clientsOutboundAdapter, idClient)
                            .mapTry(databaseReference -> databaseReference.updateChildrenAsync(users).get(Constants.TIME_OUT_FIREBASE, TimeUnit.SECONDS))
                            .onFailure(error -> LOGGER.error(LogMessages.ERROR_UPDATE_FIREBASE.getMessage(),error.getMessage(),idClient));
                    LOGGER.info(
                            LogMessages.RISK_SCORE_UPDATE_MSG_FROM_LOAN_REQUESTED.getMessage(), Encode.forJava(idClient));
                }
            };

    public static final BiConsumer<ClientsOutboundAdapter, Map<String, Object>>
            notifyLoanOnBordingCreated =
            (clientsOutboundAdapter, users) -> {
                if (users.containsKey(ID_CLIENT_KEY)) {
                    String idClient = users.get(ID_CLIENT_KEY).toString();
                    users.remove(ID_CLIENT_KEY);
                    getDatabaseReferenceOnBoarding(clientsOutboundAdapter, idClient)
                            .mapTry(databaseReference -> databaseReference.updateChildrenAsync(users).get(Constants.TIME_OUT_FIREBASE, TimeUnit.SECONDS))
                            .onFailure(error -> LOGGER.error(LogMessages.ERROR_UPDATE_FIREBASE.getMessage(),error.getMessage(),idClient));

                    LOGGER.info(LogMessages.RISK_SCORE_UPDATE_MSG_FROM_ONBORDING.getMessage(), Encode.forJava(idClient));
                }
            };

    public static final BiConsumer<ClientEntity, ClientsOutboundAdapter> notifyClientOnboarding =
            (clientEntity, clientsOutboundAdapter) -> {
                Map<String, Object> users = new HashMap<>();
                users.put(
                        CLIENT_VERIFICATION,
                        new ClientVerificationFirebase(
                                clientEntity.getOnBoardingStatus().getProductSelected(),
                                StatusClientVerificationFirebaseEnum.CREATED.name()));
                getDatabaseReferenceOnBoarding(clientsOutboundAdapter, clientEntity.getIdClient())
                        .mapTry(databaseReference -> databaseReference.updateChildrenAsync(users).get(Constants.TIME_OUT_FIREBASE, TimeUnit.SECONDS))
                        .onFailure(error -> LOGGER.error(LogMessages.ERROR_UPDATE_FIREBASE.getMessage(),error.getMessage(),clientEntity.getIdClient()));

                LOGGER.info(LogMessages.CLIENT_REJECTED_FIREBASE.getMessage(), Encode.forJava(clientEntity.getIdClient()));
            };

    public static final Predicate<ClientEntity> isAmountPresent =
            clientEntity -> {
                if (isLoanRequestedFromHome.test(clientEntity)) {
                    return Objects.nonNull(clientEntity.getLoanRequested().getLoanClientRequested());
                } else if (isOnboardingProductSelectedCredit.test(clientEntity)) {
                    return Objects.nonNull(clientEntity.getOnBoardingStatus().getLoanClientRequested());
                }
                return FALSE;
            };

    public static final Predicate<ClientEntity> isClientRiskAnalisisPresent =
            clientEntity -> Objects.nonNull(clientEntity.getCreditRiskAnalysis());

    public static final BiConsumer<ClientEntity, ClientsOutboundAdapter> notifyLoanRequestedCreated =
            (clientEntity, clientsOutboundAdapter) -> {
                Map<String, Object> users = new HashMap<>();
                users.put(
                        LOANREQUESTED_VERIFICATION,
                        new LoanRequestedStatusFirebase(clientEntity.getLoanRequested().getStatus()));
                getDatabaseReferenceLoanRequested(clientsOutboundAdapter, clientEntity.getIdClient())
                        .mapTry(databaseReference -> databaseReference.updateChildrenAsync(users).get(Constants.TIME_OUT_FIREBASE, TimeUnit.SECONDS))
                        .onFailure(error -> LOGGER.error(LogMessages.ERROR_UPDATE_FIREBASE.getMessage(),error.getMessage(),clientEntity.getIdClient()));
                LOGGER.info(
                        LogMessages.LOAN_REQUESTED_CREATED_FIREBASE.getMessage(), Encode.forJava(clientEntity.getIdClient()));
            };

    public static final BiConsumer<ClientsOutboundAdapter, Map<String, Object>>
            notifyLoanOnBoardingFailed =
            (clientsOutboundAdapter, users) -> {
                if (users.containsKey(ID_CLIENT_KEY)) {
                    String idClient = users.get(ID_CLIENT_KEY).toString();
                    users.remove(ID_CLIENT_KEY);
                    getDatabaseReferenceOnBoarding(clientsOutboundAdapter, idClient)
                            .mapTry(databaseReference -> databaseReference.updateChildrenAsync(users).get(Constants.TIME_OUT_FIREBASE, TimeUnit.SECONDS))
                            .onFailure(error -> LOGGER.error(LogMessages.ERROR_UPDATE_FIREBASE.getMessage(),error.getMessage(),idClient));

                    LOGGER.error(LogMessages.FIREBASE_LOAN_ONBOARDING_FAILED.getMessage(), Encode.forJava(idClient));
                }
            };
    public static final BiConsumer<ClientsOutboundAdapter, Map<String, Object>>
            notifyLoanFromHomeFailed =
            (clientsOutboundAdapter, users) -> {
                if (users.containsKey(ID_CLIENT_KEY)) {
                    String idClient = users.get(ID_CLIENT_KEY).toString();
                    users.remove(ID_CLIENT_KEY);
                    getDatabaseReferenceLoanRequested(clientsOutboundAdapter, idClient)
                            .mapTry(databaseReference -> databaseReference.updateChildrenAsync(users).get(Constants.TIME_OUT_FIREBASE, TimeUnit.SECONDS))
                            .onFailure(error -> LOGGER.error(LogMessages.ERROR_UPDATE_FIREBASE.getMessage(),error.getMessage(),idClient));
                    LOGGER.error(LogMessages.FIREBASE_LOAN_FROM_HOME_FAILED.getMessage(), Encode.forJava(idClient));
                }
            };

    public static Map<String, Object> getFirebaseFailParamsFromHomeCredit(
            ClientEntity clientEntity, String detail) {
        Map<String, Object> users = new HashMap<>();
        users.put(LOANREQUESTED_VERIFICATION, createLoanRequestedStatusFirebaseFail(detail));
        users.put(ID_CLIENT_KEY, clientEntity.getIdClient());
        return users;
    }

    public static Map<String, Object> getFirebaseFailParamsOnboardingCredit(
            ClientEntity clientEntity, String detail) {
        Map<String, Object> users = new HashMap<>();
        users.put(
                CLIENT_VERIFICATION, createClientVerificationFirebaseFailFromEntity(clientEntity, detail));
        users.put(ID_CLIENT_KEY, clientEntity.getIdClient());
        return users;
    }



    public static ClientVerificationFirebase getClientKOBiometric(ClientEntity clientEntity) {
        return createClientVerificationFirebaseKOIdentityFromEntity(clientEntity);
    };

    public static Try<DatabaseReference> getDatabaseReferenceOnBoarding(
            ClientsOutboundAdapter clientsOutboundAdapter, String idClient) {

        return Try.of(()->clientsOutboundAdapter
                .getDatabaseReference()
                .child(REFERENCE_ON_BOARDING_FIREBASE_CLIENTS)
                .child(idClient))
                .onFailure(error -> LOGGER.error(LogMessages.ERROR_FIREBASE_CLIENT.getMessage(),error.getMessage(),idClient));
    }

    public static Try<DatabaseReference> getDatabaseReferenceLoanRequested(
            ClientsOutboundAdapter clientsOutboundAdapter, String idClient) {
        return Try.of(()->clientsOutboundAdapter
                .getDatabaseReference()
                .child(REFERENCE_LOAN_REQUESTED_FIREBASE_CLIENTS)
                .child(idClient))
                .onFailure(error -> LOGGER.error(LogMessages.ERROR_FIREBASE_CLIENT.getMessage(),error.getMessage(),idClient));
    }

    public static String getHashString(String string) {
        String hashtext = StringUtils.EMPTY;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] messageDigest = md.digest(string.getBytes(Charset.forName("UTF-8")));
            BigInteger no = new BigInteger(1, messageDigest);
            hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error(LogMessages.ERROR_GENERATING_HASH.getMessage(), e.getMessage(), e);
        }
        return hashtext;
    }


}