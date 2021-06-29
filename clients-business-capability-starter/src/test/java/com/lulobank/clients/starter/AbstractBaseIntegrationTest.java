package com.lulobank.clients.starter;

import brave.http.HttpTracing;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.SignUpRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.database.DatabaseReference;
import com.google.gson.Gson;
import com.lulobank.biometric.api.validation.otp.OTPJwtValidator;
import com.lulobank.clientalerts.sdk.dto.notifications.InitialClientNotifications;
import com.lulobank.clientalerts.sdk.operations.impl.RetrofitClientNotificationsOperations;
import com.lulobank.clients.sdk.operations.dto.login.LoginRequest;
import com.lulobank.clients.sdk.operations.impl.RetrofitClientOperations;
import com.lulobank.clients.services.ILoginAttempts;
import com.lulobank.clients.services.application.port.in.ZendeskClientInfoPort;
import com.lulobank.clients.services.application.port.out.transactions.savingsaccounts.TransactionsPort;
import com.lulobank.clients.services.events.IdentityInformation;
import com.lulobank.clients.services.features.RetriesOption;
import com.lulobank.clients.services.features.onboardingclients.action.MessageToNotifySQSRiskEngine;
import com.lulobank.clients.services.features.profile.UpdateClientAddressUseCase;
import com.lulobank.clients.services.inboundadapters.ClientOnboardingAdapter;
import com.lulobank.clients.services.outboundadapters.ClientsOutboundAdapter;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.outboundadapters.repository.ClientsRepository;
import com.lulobank.clients.services.outboundadapters.repository.LoginAttemptsRepository;
import com.lulobank.clients.services.ports.out.AuthenticationService;
import com.lulobank.clients.services.ports.out.DigitalEvidenceService;
import com.lulobank.clients.services.ports.repository.TimestampRepository;
import com.lulobank.clients.services.usecase.ClientDigitalEvidenceUseCase;
import com.lulobank.clients.services.usecase.InitialClientUseCase;
import com.lulobank.clients.services.utils.CognitoProperties;
import com.lulobank.clients.starter.outboundadapter.identityprovider.keycloak.KeycloakAdapter;
import com.lulobank.clients.starter.utils.AWSCognitoBearerTokenRequestPostProcessor;
import com.lulobank.clients.starter.utils.BearerTokenRequestPostProcessor;
import com.lulobank.clients.starter.utils.LuloMockRestServer;
import com.lulobank.clients.starter.v3.adapters.in.ClientBiometricAdapterV3;
import com.lulobank.clients.starter.v3.adapters.in.ClientsDemographicAdapterV3;
import com.lulobank.clients.starter.v3.adapters.in.createaddress.handler.ClientCreateAddressHandler;
import com.lulobank.clients.starter.v3.adapters.in.updatecheckpoint.handler.UpdateCheckpointHandler;
import com.lulobank.clients.starter.v3.adapters.out.dynamo.ClientsDataRepository;
import com.lulobank.clients.starter.v3.adapters.out.firebase.UserStateFirebaseRepository;
import com.lulobank.clients.starter.v3.handler.ClientBiometricHandler;
import com.lulobank.clients.starter.v3.handler.ClientsDemographicHandler;
import com.lulobank.clients.starter.v3.handler.ProductOfferHandler;
import com.lulobank.clients.starter.v3.handler.phone.UpdatePhoneNumberHandler;
import com.lulobank.clients.v3.adapters.port.out.credits.CreditsService;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.adapters.port.out.riskengine.RiskEngineService;
import com.lulobank.clients.v3.events.RiskEngineNotificationService;
import com.lulobank.clients.v3.usecase.ClientsBiometricUseCase;
import com.lulobank.clients.v3.usecase.ClientsDemographicUseCase;
import com.lulobank.clients.v3.usecase.phone.UpdatePhoneNumberUseCase;
import com.lulobank.credits.sdk.dto.initialofferv2.GetOfferToClient;
import com.lulobank.credits.sdk.operations.InitialOffersOperations;
import com.lulobank.credits.sdk.operations.impl.RetrofitGetLoanDetailOperations;
import com.lulobank.otp.sdk.operations.impl.RetrofitOtpOperations;
import com.lulobank.savingsaccounts.sdk.operations.GetSavingsAccountService;
import com.lulobank.savingsaccounts.sdk.operations.ISavingsAccount;
import com.lulobank.tracing.BraveTracerWrapper;
import com.lulobank.tracing.DatabaseBrave;
import flexibility.client.models.request.GetAccountRequest;
import flexibility.client.models.request.GetLoanRequest;
import flexibility.client.sdk.FlexibilitySdk;
import io.vavr.control.Try;
import okhttp3.OkHttpClient;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.aws.messaging.listener.SimpleMessageListenerContainer;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@ImportAutoConfiguration(RefreshAutoConfiguration.class)
@ActiveProfiles(profiles = "test")
@WebMvcTest({ClientOnboardingAdapter.class, ClientAdapterTest.class})
public abstract class AbstractBaseIntegrationTest {
  protected static final String ID_CLIENT = "1106bc49-4a0f-4f52-86ca-1994bb3c26d9";
  protected static final String CONTENT_TYPE_JSON = "application/json";
  private static final String tenantAWSToken =
      "eyJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjUyMjMxIiwic3ViIjoic3ViamVjdCIsInNjb3BlIjoiaW50ZXJuYWxfYXBpL2ludGVybmFsX3RyYW5zYWN0aW9ucyIsImV4cCI6NDY4MzgwNTE0MX0.Ns9G1cQD5SuW0_7JICaOmHqJed2iYJ9IrtL-3r7AHFETs08b1iEjmsKrEnNhezPAnEqfholtxN_gYRbIrgYeOCvBGlNbUTmHnMsufUXyfGnRm1jRjwD4TuMpSeQLAYCmlxh4ewhB7Na4l_bZE7Kn2fdWgblVnpuB64IMYaVPYPHhqRdpm0PWflfg243M6-wAjNlEMeNGZbNm1qbTvSxjfZw9Rt8G1HwG80IG1JOGqesw_0TucvI5VseLUfTqxm5yV0xvvc_2c_8x7iiaSAAsOetHXDVyPsZv_d9D003d1d-jhJlI7ac4F8w7rrA7ng8LyPNzBUDC5EbQfDguYDglMg";

  private static final String tenantAWSTokenZendesk =
      "eyJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjUyMjMxIiwic3ViIjoic3ViamVjdCIsInNjb3BlIjoiemVuZGVzay96ZW5kZXNrIiwiZXhwIjo0NjgzODA1MTQxfQ.ADohbBJ41eLr9Znp-RCiwpmK-woaaeXsNQMFatr7nUI-_kC0arlkav343rYKcjsTHyiDaa51Vh9aMr1XyKJSaURBaGuUIvh0Ic-4kUy2toHXPrrI9oezd8KsGdLcA53OYjYX2HzEeVwEoqQ-8BqMuEe6lL3M1-OSZ_2STSMBsv6m2Uf9FOhk9lr91AYDINfj-ELUlqBAsoLMyJTjl2HmGgHYZvyVELe-hmbeUjffJcqtN2yhyUf2FsNodpD2rgm39syG5e4PhXtwoh9K0kbN3hBP5qGijSgkX6AriFxO4ikZdHx4RztYJL55uzHH5IXKV2XT9v6ASdUG17rfjjt0Pw";

  @Autowired protected LuloMockRestServer mockWebServer;
  @MockBean protected QueueMessagingTemplate queueMessagingTemplate;
  @Autowired protected MockMvc mockMvc;
  @MockBean protected FlexibilitySdk flexibilitySdk;
  @MockBean protected RetrofitClientOperations retrofitClientOperations;
  @Autowired protected ObjectMapper objectMapper;
  @MockBean protected SimpleMessageListenerContainer simpleMessageListenerContainer;
  @MockBean protected CognitoProperties cognitoProperties;
  @MockBean protected AWSCognitoIdentityProvider awsCognitoIdentityProvider;
  @MockBean protected KeycloakAdapter identityProviderService;
  @MockBean protected DatabaseReference databaseReference;
  @MockBean protected ClientsRepository clientsRepository;
  @MockBean protected ISavingsAccount savingsAccount;
  @MockBean protected InitialOffersOperations initialOffersOperations;
  @MockBean protected MessageToNotifySQSRiskEngine messageToNotifySQSRiskEngine;
  @MockBean protected RetrofitOtpOperations retrofitOtpOperations;
  @MockBean protected DigitalEvidenceService digitalEvidenceService;
  @MockBean protected TimestampRepository timestampRepository;
  @MockBean protected ClientDigitalEvidenceUseCase clientDigitalEvidenceUseCase;
  @MockBean protected InitialClientUseCase initialClientUseCase;
  @MockBean protected UpdateClientAddressUseCase updateClientAddressUseCase;
  @MockBean protected UpdatePhoneNumberUseCase updatePhoneNumberUseCase;
  @MockBean protected UpdatePhoneNumberHandler updatePhoneNumberHandler;
  @MockBean protected ClientsV3Repository clientsV3Repository;
  @MockBean protected ClientsDemographicUseCase clientsDemographicUseCase;
  @MockBean protected ClientsDemographicHandler clientsDemographicHandler;
  @MockBean protected ClientsDemographicAdapterV3 clientsDemographicAdapterV3;
  @MockBean protected ClientsBiometricUseCase clientsBiometricUseCase;
  @MockBean protected ClientBiometricAdapterV3 clientBiometricAdapterV3;
  @MockBean protected ClientBiometricHandler clientBiometricHandler;
  @MockBean protected UserStateFirebaseRepository stateFirebaseRepository;
  @MockBean protected RiskEngineNotificationService riskEngineNotificationService;
  @MockBean protected RiskEngineService riskEngineService;

  @MockBean protected RetrofitClientNotificationsOperations retrofitClientNotificationsOperations;
  @MockBean protected LoginAttemptsRepository loginAttemptsRepository;
  @MockBean protected ILoginAttempts loginAttemptsService;
  @MockBean protected AuthenticationService authenticationService;
  @Autowired protected ClientsOutboundAdapter clientsOutboundAdapter;
  @MockBean protected GetSavingsAccountService savingsAccountService;
  @MockBean protected RetrofitGetLoanDetailOperations retrofitGetLoanDetailOperations;
  @MockBean protected OTPJwtValidator otpJwtValidator;
  @MockBean protected HttpTracing httpTracing;
  @MockBean protected ZendeskClientInfoPort zendeskClientInfoPort;
  @MockBean protected ClientsDataRepository clientsDataRepository;
  @MockBean protected TransactionsPort transactionsPort;
  @Mock protected RetriesOption retriesOption;
  @MockBean protected DatabaseBrave databaseBrave;
  @MockBean protected ProductOfferHandler productOfferHandler;
  @MockBean protected RestTemplateBuilder savingsRestTemplate;
  @MockBean protected CreditsService creditsService;
  @Captor protected ArgumentCaptor<ClientEntity> clientEntityCaptor;
  @Captor protected ArgumentCaptor<SignUpRequest> signUpRequestArgumentCaptor;
  @Captor protected ArgumentCaptor<ClientEntity> clientEntityArgumentCaptor;

  @Captor
  protected ArgumentCaptor<InitialClientNotifications> initialClientNotificationsArgumentCaptor;

  @Captor protected ArgumentCaptor<String> stringArgumentCaptor;
  @Captor protected ArgumentCaptor<GetOfferToClient> getOfferToClient;
  @Captor protected ArgumentCaptor<HashMap> hashMapArgumentCaptor;
  @Captor protected ArgumentCaptor<LoginRequest> loginRequest;
  @Captor protected ArgumentCaptor<Map> firebaseParametersCaptor;
  @Captor protected ArgumentCaptor<IdentityInformation> identityInformationCaptor;
  @Captor protected ArgumentCaptor<GetAccountRequest> getAccountRequestArgumentCaptor;
  @Captor protected ArgumentCaptor<GetLoanRequest> getLoanRequestArgumentCaptor;
  @Captor protected ArgumentCaptor<Map<String, Object>> updateChild;
  @MockBean
  protected BraveTracerWrapper braveTracerWrapper;
  @MockBean(name="okHttpBuilderTracing")
  protected OkHttpClient builder;

  @MockBean
  protected ClientCreateAddressHandler clientCreateAddressHandler;
  @MockBean
  protected UpdateCheckpointHandler updateCheckpointHandler;
  @Before
  public void setUp()  {
    MockitoAnnotations.initMocks(this);
    init();
  }

  @After
  public void clean() {
    this.mockWebServer.reset();
  }

  protected abstract void init();

  protected static BearerTokenRequestPostProcessor bearerToken() {
    return new BearerTokenRequestPostProcessor();
  }

  protected static AWSCognitoBearerTokenRequestPostProcessor bearerTokenAWS() {
    return new AWSCognitoBearerTokenRequestPostProcessor(tenantAWSToken);
  }

  protected static AWSCognitoBearerTokenRequestPostProcessor bearerTokenAWSZendesk() {
    return new AWSCognitoBearerTokenRequestPostProcessor(tenantAWSTokenZendesk);
  }

  protected  <T> T deserializeResource(Resource resource, Class<T> parsingClass) {
    return Try.of(resource::getFile)
            .mapTry(file -> FileUtils.readFileToString(file, StandardCharsets.UTF_8))
            .map(rawResource -> new Gson().fromJson(rawResource, parsingClass))
            .get();
  }
}
