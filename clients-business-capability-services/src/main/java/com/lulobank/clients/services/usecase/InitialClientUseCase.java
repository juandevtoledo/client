package com.lulobank.clients.services.usecase;

import com.lulobank.clientalerts.sdk.dto.notifications.InitialClientNotifications;
import com.lulobank.clients.services.application.port.in.UseCase;
import com.lulobank.clients.services.exception.InitialClientTokenException;
import com.lulobank.clients.services.exception.ValidateRequestException;
import com.lulobank.clients.services.features.initialclient.actions.CreateClientFirebaseOnBoarding;
import com.lulobank.clients.services.features.initialclient.model.CreateInitialClient;
import com.lulobank.clients.services.features.initialclient.model.InitialClientCreated;
import com.lulobank.clients.services.features.login.model.TokensSignUp;
import com.lulobank.clients.services.outboundadapters.ClientsOutboundAdapter;
import com.lulobank.clients.services.ports.out.AcceptancesDocumentService;
import com.lulobank.clients.services.ports.out.AuthenticationService;
import com.lulobank.clients.services.ports.out.ClientNotificationsService;
import com.lulobank.clients.services.ports.out.IdentityProviderService;
import com.lulobank.clients.services.ports.out.ProfileService;
import com.lulobank.clients.services.utils.DatesUtil;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.core.Response;
import com.lulobank.reporting.sdk.operations.dto.AcceptancesDocument;
import io.vavr.concurrent.Future;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.lulobank.clients.services.features.onboardingclients.OnboardingClientsConverter.createClientEntityFromInitialClient;
import static com.lulobank.clients.services.utils.ClientHelper.getHashString;

@Slf4j
public class InitialClientUseCase implements UseCase<CreateInitialClient, Try<InitialClientCreated>> {

    private static final String BAD_REQUEST_EMAIL_ADDRESS_REGISTERED = "Email address is already registered";
    private static final String BAD_REQUEST_PHONE_NUMBER_REGISTERED = "Phone number is already registered";
    private static final String ERROR_GENERATING_TOKEN = "Error generating initial token for new user %s.";
    public static final String IP_ATTRIBUTE_PHONE_NUMBER = "phone_number";
    public static final String IP_ATTRIBUTE_ID_CLIENT = "custom:idClient";
    private static final String HEADER_AUTHORIZATION = "authorization";

    private final AuthenticationService authenticationService;
    private final IdentityProviderService identityProviderService;
    private final ClientsV3Repository clientsRepository;
    private final ClientNotificationsService clientNotificationsService;
    private final ClientsOutboundAdapter clientsOutboundAdapter;
    private final AcceptancesDocumentService acceptancesDocumentService;
    private final ProfileService profileService;

    public InitialClientUseCase(
            ClientsOutboundAdapter clientsOutboundAdapter,
            AuthenticationService authenticationService,
            IdentityProviderService identityProviderService,
            ClientsV3Repository clientsRepository,
            ClientNotificationsService clientNotificationsService,
            AcceptancesDocumentService acceptancesDocumentService, ProfileService profileService) {
        this.clientsOutboundAdapter = clientsOutboundAdapter;
        this.authenticationService = authenticationService;
        this.identityProviderService = identityProviderService;
        this.clientsRepository = clientsRepository;
        this.clientNotificationsService = clientNotificationsService;
        this.acceptancesDocumentService = acceptancesDocumentService;
        this.profileService = profileService;
    }

    @Override
    public Try<InitialClientCreated> execute(CreateInitialClient initialClientRequest) {
        validateUniqueEmailAndPhone(initialClientRequest);
        ClientsV3Entity clientEntity = createClientEntityFromInitialClient(initialClientRequest);
        return Try.success(initialClientRequest)
                .andThenTry(clientRequest -> notifyInitialClientCreation(clientRequest.getAuthorizationHeader(), clientEntity.getIdClient()))
                .flatMap(createInitialClient -> profileService.savePhoneNumberAndEmail(initialClientRequest.getHttpHeaders(),
                        clientEntity.getIdClient(),
                        initialClientRequest.getEmailCreateClientRequest().getAddress(),
                        initialClientRequest.getPhoneCreateInitialClient().getNumber(),
                        initialClientRequest.getPhoneCreateInitialClient().getPrefix()))
                .map(clientRequest -> createUserInIdentityProvider(clientEntity))
                .flatMap(this::createUserInDynamodb)
                .map(initialClient -> generateInitialLuloToken(initialClientRequest, initialClient, initialClientRequest.getPassword()))
                .andThenTry(initialClient -> createClientFirebase(new Response<>(initialClient), initialClientRequest))
                .flatMap(initialClient -> saveAcceptancesDocument(initialClient, clientEntity));
    }

    private void validateUniqueEmailAndPhone(CreateInitialClient client) {
        Either.right(client.getPhoneCreateInitialClient())
                .map(phone -> clientsRepository.findByPhonePrefixAndPhoneNumber(phone.getPrefix(), phone.getNumber()))
                .filter(Option::isEmpty)
                .getOrElseThrow(() -> new ValidateRequestException(BAD_REQUEST_PHONE_NUMBER_REGISTERED, HttpStatus.SC_BAD_REQUEST))
                .map(present -> client.getEmailCreateClientRequest())
                .map(email -> clientsRepository.findByEmailAddress(email.getAddress()))
                .filter(Option::isEmpty)
                .getOrElseThrow(() -> new ValidateRequestException(BAD_REQUEST_EMAIL_ADDRESS_REGISTERED, HttpStatus.SC_BAD_REQUEST));
    }


    private InitialClientCreated generateInitialLuloToken(CreateInitialClient createInitialClient, ClientsV3Entity entity, String password) {
        return authenticationService.generateTokenUser(createInitialClient.getAuthorizationAndFirebaseHeader(),
                entity.getIdClient(), entity, password)
                .map(token -> new TokensSignUp("", "", token))
                .map(tokensSignUp -> new InitialClientCreated(entity.getIdClient(), tokensSignUp))
                .getOrElseThrow(e -> new InitialClientTokenException(String.format(ERROR_GENERATING_TOKEN, entity.getEmailAddress())));
    }

    private ClientsV3Entity createUserInIdentityProvider(ClientsV3Entity clientEntity) {
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put(IP_ATTRIBUTE_PHONE_NUMBER, Collections.singletonList("+" + clientEntity.getPhonePrefix() + clientEntity.getPhoneNumber()));
        attributes.put(IP_ATTRIBUTE_ID_CLIENT, Collections.singletonList(clientEntity.getIdClient()));
        String authId = identityProviderService.createUser(clientEntity.getEmailAddress(),
                clientEntity.getQualityCode(),
                clientEntity.getName(),
                clientEntity.getLastName(),
                attributes);
        clientEntity.setIdKeycloak(authId);

        return clientEntity;
    }

    private void notifyInitialClientCreation(Map<String, String> headers, String idClient) {
        InitialClientNotifications initialClientNotifications = new InitialClientNotifications();
        initialClientNotifications.setIdClient(idClient);
        initialClientNotifications.setEmailNotificationsEnabled(true);
        initialClientNotifications.setPhoneNotificationsEnabled(false);
        initialClientNotifications.setPushNotificationsEnabled(false);

        clientNotificationsService.initialClientNotifications(headers, initialClientNotifications).get();
    }


    private void createClientFirebase(Response<InitialClientCreated> initialClientCreatedResponse, CreateInitialClient createInitialClient) {
        CreateClientFirebaseOnBoarding createClientFirebaseOnBoarding = new CreateClientFirebaseOnBoarding(clientsOutboundAdapter);
        Future.run(() -> createClientFirebaseOnBoarding.run(initialClientCreatedResponse, createInitialClient));
    }

    private Try<InitialClientCreated> saveAcceptancesDocument(InitialClientCreated initialClient, ClientsV3Entity clientEntity) {
        Map<String, String> headers = Collections.singletonMap(HEADER_AUTHORIZATION,
                String.format("Bearer %s", initialClient.getTokensSignUp().getAccessToken()));

        AcceptancesDocument acceptancesDocument = new AcceptancesDocument();
        acceptancesDocument.setDocumentAcceptancesTimestamp(DatesUtil.getFormattedDate(clientEntity.getAcceptances()
                .getDocumentAcceptancesTimestamp()));
        clientEntity.getAcceptances().setPersistedInDigitalEvidence(true);
        return acceptancesDocumentService.generateAcceptancesDocument(headers, clientEntity.getIdClient(), acceptancesDocument)
                .flatMap(res -> clientsRepository.save(clientEntity)
                        .map(clientsV3Entity -> initialClient)
                );
    }

    private Try<ClientsV3Entity> createUserInDynamodb(ClientsV3Entity client) {
        client.setQualityCode(getHashString(client.getQualityCode()));
        return clientsRepository.save(client);
    }
}