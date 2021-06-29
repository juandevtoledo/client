package com.lulobank.clients.services.usecase;

import com.lulobank.clients.services.exception.InitialClientTokenException;
import com.lulobank.clients.services.exception.ValidateRequestException;
import com.lulobank.clients.services.factory.EntityFactory;
import com.lulobank.clients.services.features.initialclient.model.CreateInitialClient;
import com.lulobank.clients.services.features.initialclient.model.InitialClientCreated;
import com.lulobank.clients.services.ports.out.AcceptancesDocumentService;
import com.lulobank.clients.services.ports.out.AuthenticationService;
import com.lulobank.clients.services.ports.out.ClientNotificationsService;
import com.lulobank.clients.services.ports.out.IdentityProviderService;
import com.lulobank.clients.services.ports.out.ProfileService;
import com.lulobank.clients.v3.adapters.port.out.digitalevidence.dto.AcceptancesDocumentRequest;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class InitialClientUseCaseTest {

    private static final String KEYCLOAK_ID = "KeycloakId";
    private static final String TOKEN = "token";
    private static final String HEADER_AUTHORIZATION = "authorization";
    private static final String HEADER_FIREBASE = "firebase-id";
    private static final String VALUE_HEADER_FIREBASE = "firebase";

    @Mock
    private AuthenticationService authenticationService;
    @Mock
    private IdentityProviderService identityProviderService;
    @Mock
    private ClientsV3Repository clientsRepositoryV3;
    @Mock
    private ClientNotificationsService clientNotificationsService;
    @Mock
    private AcceptancesDocumentService acceptancesDocumentService;

    @Mock
    private ProfileService profileService;

    @InjectMocks
    private InitialClientUseCase testedInitialClientUseCase;

    @Captor
    private ArgumentCaptor<String> textCaptor;
    @Captor
    private ArgumentCaptor<Integer> numberCaptor;
    @Captor
    private ArgumentCaptor<AcceptancesDocumentRequest> acceptancesDocumentArgumentCaptor;
    @Captor
    private ArgumentCaptor<Map<String, String>> headersCaptor;

    private final Try<Void> doNothingMethod = Try.run(() -> {
    });
    private CreateInitialClient request;

    @Before
    public void setup() {

        Map<String, String> httpHeaders = new HashMap<>();
        httpHeaders.put(HEADER_AUTHORIZATION, TOKEN);
        httpHeaders.put(HEADER_FIREBASE, VALUE_HEADER_FIREBASE);
        MockitoAnnotations.initMocks(this);
        request = EntityFactory.CreateInitialClientFactory.createInitialClientValid();
        request.setHttpHeaders(httpHeaders);
    }

    @Test
    public void shouldCreateInitialClientSuccessfully() {
        when(clientsRepositoryV3.findByPhonePrefixAndPhoneNumber(numberCaptor.capture(), textCaptor.capture())).thenReturn(Option.none());
        when(clientsRepositoryV3.findByEmailAddress(textCaptor.capture())).thenReturn(Option.none());
        when(clientNotificationsService.initialClientNotifications(anyMap(), any())).thenReturn(doNothingMethod);
        when(identityProviderService.createUser(any(), any(), any(), any(), any())).thenReturn(KEYCLOAK_ID);
        when(authenticationService.generateTokenUser(headersCaptor.capture(), any(), any(), any())).thenReturn(Try.success(TOKEN));
        when(acceptancesDocumentService.generateAcceptancesDocument(anyMap(), anyString(), any()))
                .thenReturn(Try.of(() -> true));
        when(profileService.savePhoneNumberAndEmail(any(), anyString(), anyString(), anyString(), any())).thenReturn(Try.of(() -> true));
        when(clientsRepositoryV3.save(any())).thenAnswer(
                getAnswer());


        Try<InitialClientCreated> response = testedInitialClientUseCase.execute(request);
        InitialClientCreated clientCreated = response.get();

        assertTrue(response.isSuccess());
        assertEquals(clientCreated.getTokensSignUp().getAccessToken(), TOKEN);
        assertEquals(numberCaptor.getValue().intValue(), request.getPhoneCreateInitialClient().getPrefix());
        assertEquals(textCaptor.getAllValues().get(0), request.getPhoneCreateInitialClient().getNumber());
        assertEquals(textCaptor.getAllValues().get(1), request.getEmailCreateClientRequest().getAddress());
        assertEquals(TOKEN, headersCaptor.getValue().get(HEADER_AUTHORIZATION));
        assertEquals(VALUE_HEADER_FIREBASE, headersCaptor.getValue().get(HEADER_FIREBASE));
    }


    @Test
    public void shouldCreateInitialClientSuccessfullyWithAcceptanceZeroSeconds() {
        when(clientsRepositoryV3.findByPhonePrefixAndPhoneNumber(numberCaptor.capture(), textCaptor.capture())).thenReturn(Option.none());
        when(clientsRepositoryV3.findByEmailAddress(textCaptor.capture())).thenReturn(Option.none());
        when(clientNotificationsService.initialClientNotifications(anyMap(), any())).thenReturn(doNothingMethod);
        when(identityProviderService.createUser(any(), any(), any(), any(), any())).thenReturn(KEYCLOAK_ID);
        when(authenticationService.generateTokenUser(any(), any(), any(), any())).thenReturn(Try.success(TOKEN));
        when(acceptancesDocumentService.generateAcceptancesDocument(anyMap(), anyString(), any()))
                .thenReturn(Try.of(() -> true));
        when(profileService.savePhoneNumberAndEmail(any(), anyString(), anyString(), anyString(), any())).thenReturn(Try.of(() -> true));
        when(clientsRepositoryV3.save(any())).thenAnswer(
                getAnswer());


        CreateInitialClient request = EntityFactory.CreateInitialClientFactory.createInitialClientValid();
        request.setDocumentAcceptancesTimestamp("1597421700");
        Try<InitialClientCreated> response = testedInitialClientUseCase.execute(request);
        InitialClientCreated clientCreated = response.get();

        assertTrue(response.isSuccess());
        assertEquals(clientCreated.getTokensSignUp().getAccessToken(), TOKEN);
        assertEquals(numberCaptor.getValue().intValue(), request.getPhoneCreateInitialClient().getPrefix());
        assertEquals(textCaptor.getAllValues().get(0), request.getPhoneCreateInitialClient().getNumber());
        assertEquals(textCaptor.getAllValues().get(1), request.getEmailCreateClientRequest().getAddress());
    }

    @Test(expected = ValidateRequestException.class)
    public void shouldThrowValidateRequestExceptionAsPhoneNumberExists() {
        when(clientsRepositoryV3.findByPhonePrefixAndPhoneNumber(anyInt(), anyString())).thenReturn(Option.of(new ClientsV3Entity()));
        when(clientsRepositoryV3.findByEmailAddress(anyString())).thenReturn(Option.none());
        when(profileService.savePhoneNumberAndEmail(any(), anyString(), anyString(), anyString(), any())).thenReturn(Try.failure(new ValidateRequestException()));
        when(clientNotificationsService.initialClientNotifications(anyMap(), any())).thenReturn(doNothingMethod);

        testedInitialClientUseCase.execute(request);

        verify(clientsRepositoryV3, times(1)).findByPhonePrefixAndPhoneNumber(anyInt(), anyString());
        verify(clientsRepositoryV3, times(0)).findByEmailAddress(anyString());
    }

    @Test
    public void shouldThrowValidateRequestExceptionAsPhoneNumberExistsInProfile() {
        when(clientsRepositoryV3.findByPhonePrefixAndPhoneNumber(anyInt(), anyString())).thenReturn(Option.none());
        when(clientsRepositoryV3.findByEmailAddress(anyString())).thenReturn(Option.none());
        when(profileService.savePhoneNumberAndEmail(any(), anyString(), anyString(), anyString(), any())).thenReturn(Try.failure(new ValidateRequestException()));
        when(clientNotificationsService.initialClientNotifications(anyMap(), any())).thenReturn(doNothingMethod);

        Try<InitialClientCreated> response = testedInitialClientUseCase.execute(request);
        assertTrue(response.isFailure());
        verify(clientsRepositoryV3, times(1)).findByEmailAddress(anyString());
    }

    @Test(expected = ValidateRequestException.class)
    public void shouldThrowValidateRequestExceptionAsEmailExists() {
        when(clientsRepositoryV3.findByPhonePrefixAndPhoneNumber(anyInt(), anyString())).thenReturn(Option.none());
        when(clientsRepositoryV3.findByEmailAddress(anyString())).thenReturn(Option.of(new ClientsV3Entity()));
        when(clientNotificationsService.initialClientNotifications(anyMap(), any())).thenReturn(doNothingMethod);

        testedInitialClientUseCase.execute(request);

        verify(clientsRepositoryV3, times(1)).findByPhonePrefixAndPhoneNumber(anyInt(), anyString());
        verify(clientsRepositoryV3, times(0)).findByEmailAddress(anyString());
    }

    @Test
    public void shouldFailAsClientNotificationFails() {
        when(clientsRepositoryV3.findByPhonePrefixAndPhoneNumber(anyInt(), anyString())).thenReturn(Option.none());
        when(clientsRepositoryV3.findByEmailAddress(anyString())).thenReturn(Option.none());
        when(clientNotificationsService.initialClientNotifications(anyMap(), any())).thenReturn(doNothingMethod);
        when(profileService.savePhoneNumberAndEmail(any(), anyString(), anyString(), anyString(), any())).thenReturn(Try.of(() -> true));
        when(clientsRepositoryV3.save(any())).thenAnswer(
                getAnswer());

        Try<InitialClientCreated> response = testedInitialClientUseCase.execute(request);
        assertTrue(response.isFailure());
    }

    @Test
    public void shouldFailAsClientNotificationSuccessButTokensSignUpFail() {
        Try<Void> doNothingMethod = Try.run(() -> {
        });
        when(clientsRepositoryV3.findByPhonePrefixAndPhoneNumber(anyInt(), anyString())).thenReturn(Option.none());
        when(clientsRepositoryV3.findByEmailAddress(anyString())).thenReturn(Option.none());
        when(clientNotificationsService.initialClientNotifications(anyMap(), any())).thenReturn(doNothingMethod);
        when(identityProviderService.createUser(any(), any(), any(), any(), any())).thenReturn(KEYCLOAK_ID);
        when(authenticationService.generateTokenUser(any(), any(), any(), any())).thenThrow(new InitialClientTokenException(""));
        when(profileService.savePhoneNumberAndEmail(any(), anyString(), anyString(), anyString(), any())).thenReturn(Try.of(() -> true));
        when(clientsRepositoryV3.save(any())).thenAnswer(
                getAnswer());

        Try<InitialClientCreated> response = testedInitialClientUseCase.execute(request);
        assertTrue(response.isFailure());
    }

    @NotNull
    private Answer<Try<ClientsV3Entity>> getAnswer() {
        return new Answer<Try<ClientsV3Entity>>() {
            @Override
            public Try<ClientsV3Entity> answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return Try.of(() -> (ClientsV3Entity) args[0]);
            }
        };
    }
}
