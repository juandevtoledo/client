package com.lulobank.clients.services.inboundadapters;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.AWSCognitoIdentityProviderException;
import com.amazonaws.services.cognitoidp.model.AdminDeleteUserRequest;
import com.amazonaws.services.cognitoidp.model.AuthenticationResultType;
import com.amazonaws.services.cognitoidp.model.InitiateAuthRequest;
import com.amazonaws.services.cognitoidp.model.InitiateAuthResult;
import com.amazonaws.services.cognitoidp.model.NotAuthorizedException;
import com.amazonaws.services.cognitoidp.model.SignUpRequest;
import com.amazonaws.services.cognitoidp.model.SignUpResult;
import com.amazonaws.services.cognitoidp.model.UsernameExistsException;
import com.lulobank.clients.sdk.operations.dto.ClientErrorResult;
import com.lulobank.clients.sdk.operations.dto.ClientInformationByTypeResponse;
import com.lulobank.clients.sdk.operations.dto.ClientResult;
import com.lulobank.clients.sdk.operations.dto.ClientSuccessResult;
import com.lulobank.clients.sdk.operations.dto.Credit;
import com.lulobank.clients.sdk.operations.dto.UpdateClientAddressRequest;
import com.lulobank.clients.sdk.operations.dto.onboardingclients.ClientInformationByIdClient;
import com.lulobank.clients.sdk.operations.util.CheckPoints;
import com.lulobank.clients.services.ILoginAttempts;
import com.lulobank.clients.services.application.port.out.transactions.savingsaccounts.TransactionsPort;
import com.lulobank.clients.services.application.port.out.transactions.savingsaccounts.error.TransactionsError;
import com.lulobank.clients.services.domain.RiskLevelBlackList;
import com.lulobank.clients.services.domain.StateBlackList;
import com.lulobank.clients.services.exception.ClientNotFoundException;
import com.lulobank.clients.services.features.clientproducts.closuremethodvalidator.CardlessWithdrawalMethodValidator;
import com.lulobank.clients.services.features.clientproducts.closuremethodvalidator.CashierCheckMethodValidator;
import com.lulobank.clients.services.features.clientproducts.closuremethodvalidator.ClosureMethodValidator;
import com.lulobank.clients.services.features.clientproducts.closuremethodvalidator.DonationMethodValidator;
import com.lulobank.clients.services.features.clientproducts.closuremethodvalidator.InterbankMethodValidator;
import com.lulobank.clients.services.features.clientproducts.closuremethodvalidator.LuloMethodValidator;
import com.lulobank.clients.services.features.clientproducts.closuremethodvalidator.OfficeWithdrawalMethodValidator;
import com.lulobank.clients.services.features.clientproducts.model.ProductsBasicInfo;
import com.lulobank.clients.services.features.login.model.AttemptTimeResult;
import com.lulobank.clients.services.features.login.model.SignUp;
import com.lulobank.clients.services.features.onboardingclients.model.AccountBasicInfo;
import com.lulobank.clients.services.features.onboardingclients.model.CheckingAccountCreateClientRequest;
import com.lulobank.clients.services.features.onboardingclients.model.ClientInformationByPhone;
import com.lulobank.clients.services.features.onboardingclients.model.CreateClientRequest;
import com.lulobank.clients.services.features.onboardingclients.model.CreateClientResponse;
import com.lulobank.clients.services.features.onboardingclients.model.EmailCreateClientRequest;
import com.lulobank.clients.services.features.onboardingclients.model.ForeignTransactionCreateClientRequest;
import com.lulobank.clients.services.features.onboardingclients.model.PhoneCreateClientRequest;
import com.lulobank.clients.services.features.onboardingclients.model.PhoneDeviceCreateClientRequest;
import com.lulobank.clients.services.features.profile.model.UpdateEmailClientRequest;
import com.lulobank.clients.services.features.profile.model.VerifyEmailRequest;
import com.lulobank.clients.services.outboundadapters.model.AdditionalPersonalInformation;
import com.lulobank.clients.services.outboundadapters.model.AttemptEntity;
import com.lulobank.clients.services.outboundadapters.model.ClientAcceptance;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.outboundadapters.model.IdentityBiometric;
import com.lulobank.clients.services.outboundadapters.model.LoginAttemptsEntity;
import com.lulobank.clients.services.outboundadapters.model.OnBoardingStatus;
import com.lulobank.clients.services.outboundadapters.repository.ClientsRepository;
import com.lulobank.clients.services.outboundadapters.repository.LoginAttemptsRepository;
import com.lulobank.clients.services.utils.AccountStatusEnum;
import com.lulobank.clients.services.utils.BalanceClosureMethods;
import com.lulobank.clients.services.utils.ClientErrorResultsEnum;
import com.lulobank.clients.services.utils.CognitoProperties;
import com.lulobank.clients.services.utils.DatesUtil;
import com.lulobank.clients.services.utils.IdentityBiometricStatus;
import com.lulobank.clients.services.utils.MambuErrorsResultEnum;
import com.lulobank.clients.services.utils.TransactionClosureMethods;
import com.lulobank.core.Response;
import com.lulobank.core.crosscuttingconcerns.PostActionsDecoratorHandler;
import com.lulobank.core.crosscuttingconcerns.ValidatorDecoratorHandler;
import com.lulobank.core.validations.ValidationResult;
import com.lulobank.credits.sdk.dto.loandetails.LoanDetail;
import com.lulobank.credits.sdk.dto.loandetails.LoanDetailRetrofitResponse;
import com.lulobank.credits.sdk.operations.IClientProductOfferOperations;
import com.lulobank.credits.sdk.operations.impl.RetrofitGetLoanDetailOperations;
import com.lulobank.savingsaccounts.sdk.dto.SavingSuccessResult;
import com.lulobank.savingsaccounts.sdk.dto.accountsinformation.AccountByClient;
import com.lulobank.savingsaccounts.sdk.operations.GetSavingsAccountService;
import com.lulobank.savingsaccounts.sdk.operations.ICreateSavingAccountService;
import com.lulobank.utils.exception.ServiceException;
import flexibility.client.connector.ProviderException;
import flexibility.client.models.request.GetAccountRequest;
import flexibility.client.models.request.GetLoanRequest;
import flexibility.client.models.response.GetAccountResponse;
import flexibility.client.models.response.GetLoanResponse;
import flexibility.client.sdk.FlexibilitySdk;
import io.vavr.control.Either;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.lulobank.clients.sdk.operations.util.CheckPoints.CLIENT_VERIFICATION;
import static com.lulobank.clients.services.utils.ProductTypeEnum.CREDIT_ACCOUNT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ClientAdapterTest {
    private static final String ID_CLIENT = "1106bc49-4a0f-4f52-86ca-1994bb3c26d9";
    private static final String ID_CARD = "12345678";
    private static final String NAME = "usertest";
    private static final String LAST_NAME = "lastname_test";
    private static final String PASSWORD = "123456";
    private static final String ADDRESS = "address_test";
    private static final String ADDRESS_PREFIX = "prefix_test";
    private static final String ADDRESS_2 = "address_test2";
    private static final String DEPARTMENT = "department_test";
    private static final String CITY = "city_test";
    private static final String PHONE_NUMBER = "573214477726";
    private static final String QUALITY_CODE = "ba3253876aed6bc22d4a6ff53d8406c6ad864195ed144ab5c87621b6c233b548baeae6956df346ec8c17f5ea10f35ee3cbc514797ed7ddd3145464e2a0bab413";
    private static final String SEARCH_TYPE_EMAIL = "EMAIL";
    private static final String SEARCH_TYPE_PHONE = "PHONE_NUMBER";
    private static final String SEARCH_TYPE_ID_CLIENT = "ID_CLIENT";
    private static final List<AccountBasicInfo> SAVINGS_ACCOUNTS =
            Arrays.asList(new AccountBasicInfo("LCES471", "true"));
    private static final int PHONE_PREFIX = 57;
    private static final String PHONE_NUMBER_1 = "3102897766";
    private static final String PHONE_NUMBER_2 = "3168906733";
    private static final String OLD_EMAIL = "oldmail@mail.com";
    private static final String NEW_EMAIL = "newmail@mail.com";
    private static final String FIRST_NAME = "First";
    private static final String SECOND_NAME = "Second";
    private static final String CHECKPOINT_ON_BOARDING = CheckPoints.FINISH_ON_BOARDING.name();
    private static final String PRODUCT_SELECTED = "CREDIT_ACCOUNT";
    private static final BigDecimal LULO_MAX_BALANCE = BigDecimal.valueOf(1000D);
    private static final BigDecimal CARDLESS_WITHDRAWAL_MIN_BALANCE = BigDecimal.valueOf(20000D);
    private static final BigDecimal CARDLESS_WITHDRAWAL_MAX_BALANCE = BigDecimal.valueOf(400000D);
    private static final BigDecimal OFFICE_WITHDRAWAL_MIN_BALANCE = BigDecimal.valueOf(1000D);
    private static final BigDecimal OFFICE_WITHDRAWAL_MAX_BALANCE = BigDecimal.valueOf(1200000D);
    private ClientAdapter testedClass;
    private CreateClientRequest clientRequest;
    private ClientEntity clientEntity;
    private ClientEntity clientEntityFound;
    private SignUpResult signUpResult;
    private SignUp signUp;
    private UpdateClientAddressRequest updateClientRequest;
    private UpdateEmailClientRequest updateEmailClientRequest;
    private VerifyEmailRequest verifyEmailRequest;
    private AttemptEntity failedAttempt;
    private Optional<LoginAttemptsEntity> loginAttemptsOpt;
    private static final String ATTEMPT_JSON =
            "\"{\"penalty\":900.0,\"timeRemaining\":-900.0,\"maxAttempt\":true}\"";
    private static final String WHITELIST_EXPIRATION_DATE="2020-12-26T09:53:04.545";
    @Mock
    private FlexibilitySdk flexibilitySdk;
    @Mock
    private ClientsRepository clientsRepository;
    @Mock
    private QueueMessagingTemplate queueMessagingTemplate;
    @Mock
    private CognitoProperties cognitoProperties;
    @Mock
    private GetSavingsAccountService getSavingsAccountService;
    @Mock
    private IClientProductOfferOperations clientProductOfferOperations;
    @Mock
    private ICreateSavingAccountService createSavingAccountService;
    @Mock
    private AWSCognitoIdentityProvider awsCognitoIdentityProvider;
    @Mock
    private RetrofitGetLoanDetailOperations getLoanDetailOperationsService;
    @Mock
    private ILoginAttempts loginAttemptsService;
    @Mock
    private LoginAttemptsRepository loginAttemptsRepository;
    @Mock
    private ValidatorDecoratorHandler getClientInfoByIdCardHandler;
    @Mock
    private TransactionsPort transactionsPort;
    @Mock
    private PostActionsDecoratorHandler updateClientInformationHandler;
    private InitiateAuthResult initiateAuthResult;
    private AuthenticationResultType authenticationResultType;
    private String MESSAGE_FIELD_EXIST_IN_DATABASE = "Exist in the database";
    private String MESSAGE_WRONG_FORMAT = "has a wrong format";
    private flexibility.client.models.response.CreateClientResponse clientResponse;
    private ResponseEntity<SavingSuccessResult> getSavingAccountResult;
    private AttemptTimeResult attemptTimeResult;
    private AttemptTimeResult attemptTimeResultFalse;
    @Captor
    private ArgumentCaptor<ClientEntity> clientEntityArgumentCaptor;
    @Captor
    private ArgumentCaptor<GetAccountRequest> getAccountRequestArgumentCaptor;
    @Captor
    private ArgumentCaptor<String> findByIdClientEntityCaptor;
    @Captor
    private ArgumentCaptor<SignUpRequest> signUpRequestArgumentCaptor;
    @Captor
    private ArgumentCaptor<AdminDeleteUserRequest> adminDisableUserRequestArgumentCaptor;
    @Captor
    private ArgumentCaptor<AttemptTimeResult> attemptTimeResultArgumentCaptor;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        Map<String, ClosureMethodValidator> closureMethodValidatorMap = new HashMap<>();
        closureMethodValidatorMap.put(
                BalanceClosureMethods.LULO.name(),
                new LuloMethodValidator(
                        LULO_MAX_BALANCE,
                        CARDLESS_WITHDRAWAL_MIN_BALANCE,
                        CARDLESS_WITHDRAWAL_MAX_BALANCE,
                        OFFICE_WITHDRAWAL_MIN_BALANCE,
                        OFFICE_WITHDRAWAL_MAX_BALANCE));
        closureMethodValidatorMap.put(
                BalanceClosureMethods.CARDLESS_WITHDRAWAL.name(),
                new CardlessWithdrawalMethodValidator(
                        LULO_MAX_BALANCE,
                        CARDLESS_WITHDRAWAL_MIN_BALANCE,
                        CARDLESS_WITHDRAWAL_MAX_BALANCE,
                        OFFICE_WITHDRAWAL_MIN_BALANCE,
                        OFFICE_WITHDRAWAL_MAX_BALANCE));
        closureMethodValidatorMap.put(
                BalanceClosureMethods.INTERBANK.name(),
                new InterbankMethodValidator(
                        LULO_MAX_BALANCE,
                        CARDLESS_WITHDRAWAL_MIN_BALANCE,
                        CARDLESS_WITHDRAWAL_MAX_BALANCE,
                        OFFICE_WITHDRAWAL_MIN_BALANCE,
                        OFFICE_WITHDRAWAL_MAX_BALANCE));
        closureMethodValidatorMap.put(
                BalanceClosureMethods.OFFICE_WITHDRAWAL.name(),
                new OfficeWithdrawalMethodValidator(
                        LULO_MAX_BALANCE,
                        CARDLESS_WITHDRAWAL_MIN_BALANCE,
                        CARDLESS_WITHDRAWAL_MAX_BALANCE,
                        OFFICE_WITHDRAWAL_MIN_BALANCE,
                        OFFICE_WITHDRAWAL_MAX_BALANCE));
        closureMethodValidatorMap.put(
                BalanceClosureMethods.CASHIERCHECK.name(),
                new CashierCheckMethodValidator(
                        LULO_MAX_BALANCE,
                        CARDLESS_WITHDRAWAL_MIN_BALANCE,
                        CARDLESS_WITHDRAWAL_MAX_BALANCE,
                        OFFICE_WITHDRAWAL_MIN_BALANCE,
                        OFFICE_WITHDRAWAL_MAX_BALANCE));
        closureMethodValidatorMap.put(
                BalanceClosureMethods.DONATION.name(),
                new DonationMethodValidator(
                        LULO_MAX_BALANCE,
                        CARDLESS_WITHDRAWAL_MIN_BALANCE,
                        CARDLESS_WITHDRAWAL_MAX_BALANCE,
                        OFFICE_WITHDRAWAL_MIN_BALANCE,
                        OFFICE_WITHDRAWAL_MAX_BALANCE));
        testedClass =
                new ClientAdapter(
                        clientsRepository,
                        queueMessagingTemplate,
                        cognitoProperties,
                        flexibilitySdk,
                        getLoanDetailOperationsService,
                        loginAttemptsService,
                        getClientInfoByIdCardHandler,
                        closureMethodValidatorMap,
                        transactionsPort);
        PhoneDeviceCreateClientRequest device = new PhoneDeviceCreateClientRequest();
        device.setIddevice("123456");
        device.setIpAddress("127.0.0.1");
        device.setGeolocation("x,y");
        device.setCountry("COL");
        device.setCity("BOG");
        device.setMobileDevice("Motorola");
        device.setSimCardId("123456");
        device.setModel("G5");
        device.setOperator("Claro");
        EmailCreateClientRequest email = new EmailCreateClientRequest();
        email.setAddress("test@test.com");
        email.setVerified(false);
        PhoneCreateClientRequest phone = new PhoneCreateClientRequest();
        phone.setDeviceInfo(device);
        phone.setNumber(PHONE_NUMBER_1);
        phone.setPrefix(PHONE_PREFIX);
        phone.setVerified(true);
        clientRequest = new CreateClientRequest();
        clientRequest.setIdClient(ID_CLIENT);
        clientRequest.setIdCard(ID_CARD);
        clientRequest.setName(NAME);
        clientRequest.setLastName(LAST_NAME);
        clientRequest.setPassword(PASSWORD);
        clientRequest.setAddress(ADDRESS);
        clientRequest.setEmail(email);
        clientRequest.setPhone(phone);
        List<ForeignTransactionCreateClientRequest> listForeignCurrencyTransactions = new ArrayList<>();
        listForeignCurrencyTransactions.add(
                new ForeignTransactionCreateClientRequest(
                        "CHECKING_ACCOUNT",
                        new CheckingAccountCreateClientRequest(
                                1000000d, "12345678", "CO", "SUDAMERIS GNB", "USD", "Bogota")));
        clientRequest.setForeignCurrencyTransactions(listForeignCurrencyTransactions);
        clientEntity = new ClientEntity();
        clientEntity.setIdClient(ID_CLIENT);
        clientEntity.setIdCard(ID_CARD);
        clientEntity.setIdCard(PASSWORD);
        clientEntity.setName(NAME);
        clientEntity.setLastName(LAST_NAME);
        clientEntity.setAddress(ADDRESS);
        clientEntity.setAddressPrefix(ADDRESS_PREFIX);
        clientEntity.setPhonePrefix(PHONE_PREFIX);
        clientEntity.setPhoneNumber(PHONE_NUMBER_2);
        clientEntity.setEmailAddress(NEW_EMAIL);
        clientEntity.setEmailVerified(Boolean.TRUE);
        clientEntity.setIdCard(ID_CARD);
        IdentityBiometric identityBiometric = new IdentityBiometric();
        identityBiometric.setStatus(IdentityBiometricStatus.FINISHED.name());
        clientEntity.setIdentityBiometric(identityBiometric);
        OnBoardingStatus onBoardingStatus = new OnBoardingStatus();
        onBoardingStatus.setCheckpoint(CHECKPOINT_ON_BOARDING);
        onBoardingStatus.setProductSelected(PRODUCT_SELECTED);
        clientEntity.setOnBoardingStatus(onBoardingStatus);
        clientEntity.setBlackListState(StateBlackList.NON_BLACKLISTED.name());
        clientEntity.setWhitelistExpirationDate(LocalDateTime.parse(WHITELIST_EXPIRATION_DATE));
        clientEntity.setBlackListRiskLevel(RiskLevelBlackList.NO_RISK.getLevel());
        AdditionalPersonalInformation additionalPersonalInformation = new AdditionalPersonalInformation();
        additionalPersonalInformation.setFirstName(FIRST_NAME);
        additionalPersonalInformation.setSecondName(SECOND_NAME);
        additionalPersonalInformation.setFirstSurname(FIRST_NAME);
        additionalPersonalInformation.setSecondSurname(SECOND_NAME);
        clientEntity.setAdditionalPersonalInformation(additionalPersonalInformation);
        signUpResult = new SignUpResult();
        signUpResult.setUserSub(ID_CLIENT);
        clientEntityFound = new ClientEntity();
        clientEntityFound.setIdClient(ID_CLIENT);
        clientEntityFound.setQualityCode(QUALITY_CODE);
        clientEntityFound.setEmailAddress(OLD_EMAIL);
        clientEntityFound.setDateOfIssue(LocalDate.now());
        clientEntityFound.setIdCard(ID_CARD);
        clientEntityFound.setOnBoardingStatus(
                new OnBoardingStatus(CLIENT_VERIFICATION.name(), CREDIT_ACCOUNT.name()));
        signUp = new SignUp();
        signUp.setPassword(PASSWORD);
        signUp.setUsername(NEW_EMAIL);
        updateClientRequest = new UpdateClientAddressRequest();
        updateClientRequest.setIdClient(ID_CLIENT);
        updateClientRequest.setAddress(ADDRESS);
        updateClientRequest.setDepartment(DEPARTMENT);
        updateClientRequest.setCity(CITY);
        initiateAuthResult = new InitiateAuthResult();
        authenticationResultType = new AuthenticationResultType();
        authenticationResultType.setIdToken("");
        authenticationResultType.setRefreshToken("");
        initiateAuthResult.setAuthenticationResult(authenticationResultType);
        clientResponse = new flexibility.client.models.response.CreateClientResponse();
        flexibility.client.models.response.CreateClientResponse.Account account =
                new flexibility.client.models.response.CreateClientResponse.Account();
        flexibility.client.models.response.CreateClientResponse.Client client =
                new flexibility.client.models.response.CreateClientResponse.Client();
        client.setId(ID_CLIENT);
        account.setId(ID_CLIENT);
        clientResponse.setAccount(account);
        clientResponse.setClient(client);
        List<AccountByClient> accountList = new ArrayList();
        accountList.add(new AccountByClient("1a2b3c4d5e6f", ID_CLIENT, "", "", ""));
        SavingSuccessResult result = new SavingSuccessResult(accountList);
        getSavingAccountResult = new ResponseEntity<>(result, HttpStatus.OK);
        Credit credit = new Credit();
        credit.setIdCredit("1234-5678");
        credit.setIdOffer("8765-4321");
        updateEmailClientRequest = new UpdateEmailClientRequest();
        updateEmailClientRequest.setIdClient(ID_CLIENT);
        updateEmailClientRequest.setOldEmail(OLD_EMAIL);
        updateEmailClientRequest.setNewEmail(NEW_EMAIL);
        updateEmailClientRequest.setPassword(PASSWORD);
        verifyEmailRequest = new VerifyEmailRequest("lulotest@yopmail.com");

        failedAttempt = new AttemptEntity();
        failedAttempt.setPenalty(10d);
        failedAttempt.setAttemptDate(DatesUtil.getLocalDateGMT5());
        failedAttempt.setAttemptDate(failedAttempt.getAttemptDate().minus(1L, ChronoUnit.MINUTES));
        failedAttempt.setMaxAttempt(true);
        List<AttemptEntity> failedAttempts = new ArrayList<>();
        failedAttempts.add(failedAttempt);
        LoginAttemptsEntity loginAttempts = new LoginAttemptsEntity();
        loginAttempts.setIdClient(UUID.fromString("6c64b082-d327-4d45-8cb6-371879cd7497"));
        loginAttempts.setFailsAttempt(failedAttempts);
        loginAttemptsOpt = Optional.of(loginAttempts);

        attemptTimeResult = new AttemptTimeResult();
        attemptTimeResult.setMaxAttempt(true);
        attemptTimeResult.setPenalty(2D);
        attemptTimeResult.setTimeRemaining(123D);

        attemptTimeResultFalse = new AttemptTimeResult();
        attemptTimeResultFalse.setMaxAttempt(false);
        attemptTimeResultFalse.setPenalty(2D);
        attemptTimeResultFalse.setTimeRemaining(123D);
    }

    @Test
    public void should_Return_ACCEPTED_Since_Client_Should_Saved() {
        when(clientsRepository.save(any(ClientEntity.class))).thenReturn(clientEntity);
        when(cognitoProperties.getAwsCognitoIdentityProvider()).thenReturn(awsCognitoIdentityProvider);
        when(awsCognitoIdentityProvider.signUp(any(SignUpRequest.class))).thenReturn(signUpResult);
        when(clientsRepository.findByIdClient(any(String.class))).thenReturn(Optional.of(clientEntity));
        when(clientsRepository.findByIdClient(any(String.class)))
                .thenReturn(java.util.Optional.ofNullable(clientEntityFound));
        when(clientsRepository.findByIdCard(any(String.class))).thenReturn(clientEntityFound);
        when(clientsRepository.findByIdClientAndIdCard(any(String.class), anyString()))
                .thenReturn(Optional.of(clientEntityFound));
        ResponseEntity<Response<CreateClientResponse>> response =
                testedClass.saveClient(new HttpHeaders(), clientRequest);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals(ID_CLIENT, response.getBody().getContent().getUserId());
    }

    @Test
    public void should_Return_ACCEPTED_Since_Client_Foreign_Transactions_Are_Null_Or_Empty() {
        clientRequest.setForeignCurrencyTransactions(null);
        when(clientsRepository.save(any(ClientEntity.class))).thenReturn(clientEntity);
        when(cognitoProperties.getAwsCognitoIdentityProvider()).thenReturn(awsCognitoIdentityProvider);
        when(awsCognitoIdentityProvider.signUp(any(SignUpRequest.class))).thenReturn(signUpResult);
        when(clientsRepository.findByIdClient(any(String.class))).thenReturn(Optional.of(clientEntity));
        when(clientsRepository.findByIdCard(any(String.class))).thenReturn(clientEntityFound);
        when(clientsRepository.findByIdClientAndIdCard(any(String.class), anyString()))
                .thenReturn(Optional.of(clientEntityFound));
        ResponseEntity<Response<CreateClientResponse>> response =
                testedClass.saveClient(new HttpHeaders(), clientRequest);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals(ID_CLIENT, response.getBody().getContent().getUserId());
    }

    @Test
    public void should_Return_BAD_REQUEST_Since_Client_Id_Card_Has_Wrong_Format() {
        clientRequest.setIdCard("12341215345234");
        when(clientsRepository.save(any(ClientEntity.class))).thenReturn(clientEntity);
        ResponseEntity<Response<CreateClientResponse>> response =
                testedClass.saveClient(new HttpHeaders(), clientRequest);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody().getErrors());
        assertEquals(1, response.getBody().getErrors().size());
        assertEquals(MESSAGE_WRONG_FORMAT, response.getBody().getErrors().get(0).getFailure());
        assertEquals("idCard", response.getBody().getErrors().get(0).getValue());
    }

    @Test
    public void should_Return_BAD_REQUEST_Since_Client_Id_Card_Is_Null() {
        clientRequest.setIdCard(null);
        when(clientsRepository.save(any(ClientEntity.class))).thenReturn(clientEntity);
        ResponseEntity<Response<CreateClientResponse>> response =
                testedClass.saveClient(new HttpHeaders(), clientRequest);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody().getErrors());
        assertEquals(1, response.getBody().getErrors().size());
        assertEquals(MESSAGE_WRONG_FORMAT, response.getBody().getErrors().get(0).getFailure());
        assertEquals("idCard", response.getBody().getErrors().get(0).getValue());
    }

    @Test
    public void should_Return_BAD_REQUEST_Since_Client_Phone_Has_Wrong_Size() {
        clientRequest.getPhone().setNumber("12345678901");
        when(clientsRepository.save(any(ClientEntity.class))).thenReturn(clientEntity);
        when(clientsRepository.findByIdClient(any(String.class)))
                .thenReturn(java.util.Optional.ofNullable(clientEntityFound));
        when(clientsRepository.findByIdCard(any(String.class))).thenReturn(clientEntityFound);
        when(clientsRepository.findByIdClientAndIdCard(any(String.class), anyString()))
                .thenReturn(Optional.of(clientEntityFound));
        ResponseEntity<Response<CreateClientResponse>> response =
                testedClass.saveClient(new HttpHeaders(), clientRequest);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody().getErrors());
        assertEquals(1, response.getBody().getErrors().size());
        assertEquals(MESSAGE_WRONG_FORMAT, response.getBody().getErrors().get(0).getFailure());
        assertEquals("phoneNumber", response.getBody().getErrors().get(0).getValue());
    }

    @Test
    public void should_Return_BAD_REQUEST_Since_Client_Id_Card_Is_Empty() {
        clientRequest.setIdCard("");
        when(clientsRepository.save(any(ClientEntity.class))).thenReturn(clientEntity);
        ResponseEntity<Response<CreateClientResponse>> response =
                testedClass.saveClient(new HttpHeaders(), clientRequest);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody().getErrors());
        assertEquals(1, response.getBody().getErrors().size());
        assertEquals(MESSAGE_WRONG_FORMAT, response.getBody().getErrors().get(0).getFailure());
        assertEquals("idCard", response.getBody().getErrors().get(0).getValue());
    }

    @Test
    public void should_Return_BAD_REQUEST_Since_Client_Email_Is_Wrong_Format() {
        clientRequest.getEmail().setAddress("qwerqwe");
        when(clientsRepository.save(any(ClientEntity.class))).thenReturn(clientEntity);
        when(clientsRepository.findByIdClient(any(String.class)))
                .thenReturn(java.util.Optional.ofNullable(clientEntityFound));
        when(clientsRepository.findByIdCard(any(String.class))).thenReturn(clientEntityFound);
        when(clientsRepository.findByIdClientAndIdCard(any(String.class), anyString()))
                .thenReturn(Optional.of(clientEntityFound));
        ResponseEntity<Response<CreateClientResponse>> response =
                testedClass.saveClient(new HttpHeaders(), clientRequest);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody().getErrors());
        assertEquals(1, response.getBody().getErrors().size());
        assertEquals(MESSAGE_WRONG_FORMAT, response.getBody().getErrors().get(0).getFailure());
        assertEquals("emailAddress", response.getBody().getErrors().get(0).getValue());
    }

    @Test
    public void should_Return_BAD_REQUEST_Since_Client_Email_Is_Null() {
        clientRequest.getEmail().setAddress("");
        when(clientsRepository.save(any(ClientEntity.class))).thenReturn(clientEntity);
        when(clientsRepository.findByIdClient(any(String.class)))
                .thenReturn(java.util.Optional.ofNullable(clientEntityFound));
        when(clientsRepository.findByIdCard(any(String.class))).thenReturn(clientEntityFound);
        when(clientsRepository.findByIdClientAndIdCard(any(String.class), anyString()))
                .thenReturn(Optional.of(clientEntityFound));
        ResponseEntity<Response<CreateClientResponse>> response =
                testedClass.saveClient(new HttpHeaders(), clientRequest);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody().getErrors());
        assertEquals(1, response.getBody().getErrors().size());
        assertEquals(MESSAGE_WRONG_FORMAT, response.getBody().getErrors().get(0).getFailure());
        assertEquals("emailAddress", response.getBody().getErrors().get(0).getValue());
    }

    @Test
    public void should_Return_BAD_REQUEST_Since_Client_Phone_Has_Words() {
        clientRequest.getPhone().setNumber("1234fghhr243");
        when(clientsRepository.save(any(ClientEntity.class))).thenReturn(clientEntity);
        when(clientsRepository.findByIdClient(any(String.class)))
                .thenReturn(java.util.Optional.ofNullable(clientEntityFound));
        when(clientsRepository.findByIdCard(any(String.class))).thenReturn(clientEntityFound);
        when(clientsRepository.findByIdClientAndIdCard(any(String.class), anyString()))
                .thenReturn(Optional.of(clientEntityFound));
        ResponseEntity<Response<CreateClientResponse>> response =
                testedClass.saveClient(new HttpHeaders(), clientRequest);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody().getErrors());
        assertEquals(1, response.getBody().getErrors().size());
        assertEquals(MESSAGE_WRONG_FORMAT, response.getBody().getErrors().get(0).getFailure());
        assertEquals("phoneNumber", response.getBody().getErrors().get(0).getValue());
    }

    @Test
    public void should_Return_BAD_REQUEST_Since_Client_Phone_Is_Null() {
        clientRequest.getPhone().setNumber(null);
        when(clientsRepository.findByIdClient(any(String.class)))
                .thenReturn(java.util.Optional.ofNullable(clientEntityFound));
        when(clientsRepository.findByIdCard(any(String.class))).thenReturn(clientEntityFound);
        when(clientsRepository.save(any(ClientEntity.class))).thenReturn(clientEntity);
        when(clientsRepository.findByIdClientAndIdCard(any(String.class), anyString()))
                .thenReturn(Optional.of(clientEntityFound));
        ResponseEntity<Response<CreateClientResponse>> response =
                testedClass.saveClient(new HttpHeaders(), clientRequest);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody().getErrors());
        assertEquals(1, response.getBody().getErrors().size());
        assertEquals(MESSAGE_WRONG_FORMAT, response.getBody().getErrors().get(0).getFailure());
        assertEquals("phoneNumber", response.getBody().getErrors().get(0).getValue());
    }

    @Test
    public void should_Return_BAD_REQUEST_Since_Client_Phone_Exist() {
        when(clientsRepository.findByPhonePrefixAndPhoneNumber(anyInt(), any(String.class)))
                .thenReturn(Optional.of(clientEntity));
        when(clientsRepository.findByIdClient(any(String.class)))
                .thenReturn(java.util.Optional.ofNullable(clientEntityFound));
        when(clientsRepository.findByIdCard(any(String.class))).thenReturn(clientEntityFound);
        when(clientsRepository.findByIdClientAndIdCard(any(String.class), anyString()))
                .thenReturn(Optional.of(clientEntityFound));
        ResponseEntity<Response<CreateClientResponse>> response =
                testedClass.saveClient(new HttpHeaders(), clientRequest);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody().getErrors());
        assertEquals(1, response.getBody().getErrors().size());
        assertEquals(
                MESSAGE_FIELD_EXIST_IN_DATABASE, response.getBody().getErrors().get(0).getFailure());
        assertEquals("phoneNumber", response.getBody().getErrors().get(0).getValue());
    }

    @Test
    public void should_Return_BAD_REQUEST_Since_Client_Email_Exist() {
        when(clientsRepository.findByEmailAddress(any(String.class))).thenReturn(clientEntity);
        when(clientsRepository.findByIdClient(any(String.class)))
                .thenReturn(java.util.Optional.ofNullable(clientEntityFound));
        when(clientsRepository.findByIdCard(any(String.class))).thenReturn(clientEntityFound);
        when(clientsRepository.findByIdClientAndIdCard(any(String.class), anyString()))
                .thenReturn(Optional.of(clientEntityFound));
        ResponseEntity<Response<CreateClientResponse>> response =
                testedClass.saveClient(new HttpHeaders(), clientRequest);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody().getErrors());
        assertEquals(1, response.getBody().getErrors().size());
        assertEquals(
                MESSAGE_FIELD_EXIST_IN_DATABASE, response.getBody().getErrors().get(0).getFailure());
        assertEquals("emailAddress", response.getBody().getErrors().get(0).getValue());
    }

    @Test
    public void should_Return_BAD_REQUEST_Since_Client_Id_Client_Has_Wrong_Format() {
        clientRequest.setIdClient("00000001-0001-0001-0001-000000000001");
        when(clientsRepository.save(any(ClientEntity.class))).thenReturn(clientEntity);
        when(clientsRepository.findByIdClient(any(String.class)))
                .thenReturn(java.util.Optional.ofNullable(clientEntityFound));
        when(clientsRepository.findByIdCard(any(String.class))).thenReturn(clientEntityFound);
        when(clientsRepository.findByIdClientAndIdCard(any(String.class), anyString()))
                .thenReturn(Optional.of(clientEntityFound));
        ResponseEntity<Response<CreateClientResponse>> response =
                testedClass.saveClient(new HttpHeaders(), clientRequest);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody().getErrors());
        assertEquals(1, response.getBody().getErrors().size());
        assertEquals(MESSAGE_WRONG_FORMAT, response.getBody().getErrors().get(0).getFailure());
        assertEquals("idClient", response.getBody().getErrors().get(0).getValue());
    }

    @Test
    public void should_Return_BAD_REQUEST_Since_Client_Id_Client_Is_Null() {
        clientRequest.setIdClient(null);
        when(clientsRepository.save(any(ClientEntity.class))).thenReturn(clientEntity);
        when(clientsRepository.findByIdClient(any(String.class)))
                .thenReturn(java.util.Optional.ofNullable(clientEntityFound));
        when(clientsRepository.findByIdCard(any(String.class))).thenReturn(clientEntityFound);
        when(clientsRepository.findByIdClientAndIdCard(any(String.class), anyString()))
                .thenReturn(Optional.of(clientEntityFound));
        ResponseEntity<Response<CreateClientResponse>> response =
                testedClass.saveClient(new HttpHeaders(), clientRequest);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody().getErrors());
        assertEquals(2, response.getBody().getErrors().size());
        assertEquals(MESSAGE_WRONG_FORMAT, response.getBody().getErrors().get(0).getFailure());
        assertEquals("idClient", response.getBody().getErrors().get(0).getValue());
    }

    @Test
    public void should_Return_BAD_REQUEST_Since_Client_Id_Client_Is_Empty() {
        clientRequest.setIdClient("");
        when(clientsRepository.save(any(ClientEntity.class))).thenReturn(clientEntity);
        when(clientsRepository.findByIdClient(any(String.class)))
                .thenReturn(java.util.Optional.ofNullable(clientEntityFound));
        when(clientsRepository.findByIdCard(any(String.class))).thenReturn(clientEntityFound);
        when(clientsRepository.findByIdClientAndIdCard(any(String.class), anyString()))
                .thenReturn(Optional.of(clientEntityFound));
        ResponseEntity<Response<CreateClientResponse>> response =
                testedClass.saveClient(new HttpHeaders(), clientRequest);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody().getErrors());
        assertEquals(1, response.getBody().getErrors().size());
        assertEquals(MESSAGE_WRONG_FORMAT, response.getBody().getErrors().get(0).getFailure());
        assertEquals("idClient", response.getBody().getErrors().get(0).getValue());
    }

    @Test
    public void should_Return_Transaction_Client_And_Status_Accepted() {
        when(clientsRepository.findByPhonePrefixAndPhoneNumber(any(Integer.class), anyString()))
                .thenReturn(Optional.of(clientEntity));
        ResponseEntity<Response<ClientInformationByPhone>> response =
                testedClass.getClientByPhoneNumber(new HttpHeaders(), PHONE_PREFIX, PHONE_NUMBER_1);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ID_CLIENT, response.getBody().getContent().getIdClient());
    }

    @Test
    public void should_Not_Return_Transaction_Client_And_Status_NotFound() {
        when(clientsRepository.findByPhonePrefixAndPhoneNumber(any(Integer.class), anyString()))
                .thenReturn(Optional.empty());
        ResponseEntity<Response<ClientInformationByPhone>> response =
                testedClass.getClientByPhoneNumber(new HttpHeaders(), PHONE_PREFIX, PHONE_NUMBER_1);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void should_Return_ClientInformation_By_IdClient_And_Status_Accepted()
            throws ProviderException {
        List<GetAccountResponse> accounts = getaccountsGMF();
        clientEntity.setIdCbs("ID_CBS");
        when(clientsRepository.findByIdClient(any(String.class))).thenReturn(Optional.of(clientEntity));
        when(flexibilitySdk.getAccountsByClientId(any(GetAccountRequest.class))).thenReturn(accounts);
        ResponseEntity<Response<ClientInformationByIdClient>> response =
                testedClass.getClientByIdClient(new HttpHeaders(), ID_CLIENT);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(
                "it Should map the idClient", ID_CLIENT, response.getBody().getContent().getIdClient());
        assertEquals("it Should map the idCard", ID_CARD, response.getBody().getContent().getIdCard());
        assertEquals("it Should map the Name", NAME, response.getBody().getContent().getName());
        assertEquals(
                "it Should map the Lastname", LAST_NAME, response.getBody().getContent().getLastName());
        assertEquals(
                "it Should map the Address",
                ADDRESS_PREFIX + " " + ADDRESS,
                response.getBody().getContent().getAddress());
        assertEquals(
                "it Should map the savings accounts",
                SAVINGS_ACCOUNTS.get(0).getSavingsAccount(),
                response.getBody().getContent().getSavingsAccounts().get(0).getSavingsAccount());
    }

    @Test
    public void should_Not_Return_ClientInformation_And_Status_InternalServerError_CLIENT_DB_ERROR()
            throws ProviderException {
        when(clientsRepository.findByIdClient(any(String.class))).thenThrow(SdkClientException.class);

        ResponseEntity<Response<ClientInformationByIdClient>> response =
                testedClass.getClientByIdClient(new HttpHeaders(), ID_CLIENT);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void
    should_Not_Return_ClientInformation_And_Status_InternalServerError_CLIENT_SDK_FLEXIBILITY_ERROR()
            throws ProviderException {
        when(clientsRepository.findByIdClient(any(String.class))).thenReturn(Optional.of(clientEntity));
        when(flexibilitySdk.getAccountsByClientId(any(GetAccountRequest.class)))
                .thenThrow(ProviderException.class);

        ResponseEntity<Response<ClientInformationByIdClient>> response =
                testedClass.getClientByIdClient(new HttpHeaders(), ID_CLIENT);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void should_Return_ClientInformation_And_Status_OK_ServiceException()
            throws ProviderException {
        when(clientsRepository.findByIdClient(any(String.class))).thenReturn(Optional.of(clientEntity));
        when(flexibilitySdk.getAccountsByClientId(any(GetAccountRequest.class)))
                .thenThrow(ServiceException.class);

        ResponseEntity<Response<ClientInformationByIdClient>> response =
                testedClass.getClientByIdClient(new HttpHeaders(), ID_CLIENT);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test(expected = ClientNotFoundException.class)
    public void should_Not_Return_ClientInformation_By_IdClient_And_Status_NotFound() {
        when(clientsRepository.findByIdClient(any(String.class))).thenReturn(Optional.empty());
        ResponseEntity<Response<ClientInformationByIdClient>> response =
                testedClass.getClientByIdClient(new HttpHeaders(), ID_CLIENT);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void should_Not_Return_ClientInformation_By_IdClient_And_Status_InternalServerError() {
        when(clientsRepository.findByIdClient(any(String.class))).thenThrow(SdkClientException.class);
        ResponseEntity<Response<ClientInformationByIdClient>> response =
                testedClass.getClientByIdClient(new HttpHeaders(), ID_CLIENT);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void should_return_PRECONDITION_FAILED_Since_idClientEmailPassword_not_found_update_email() {
        updateEmailClientRequest.setOldEmail(NEW_EMAIL);
        when(clientsRepository.findByIdClient(any(String.class))).thenReturn(Optional.of(clientEntityFound));
        ResponseEntity<ClientResult> response = testedClass.updateClientEmail(new HttpHeaders(), ID_CLIENT, updateEmailClientRequest);
        verify(clientsRepository, times(0)).save(any(ClientEntity.class));
        verify(cognitoProperties, times(0)).getAwsCognitoIdentityProvider();
        assertEquals(HttpStatus.PRECONDITION_FAILED, response.getStatusCode());
    }

    @Test
    public void should_return_INTERNAL_SERVER_ERROR_Since_cognito_error_at_update_email() {
        when(clientsRepository.findByIdClient(any(String.class))).thenReturn(Optional.of(clientEntityFound));
        when(cognitoProperties.getAwsCognitoIdentityProvider()).thenReturn(awsCognitoIdentityProvider);
        when(awsCognitoIdentityProvider.signUp(any(SignUpRequest.class)))
                .thenThrow(AWSCognitoIdentityProviderException.class);
        ResponseEntity<ClientResult> response = testedClass.updateClientEmail(new HttpHeaders(), ID_CLIENT, updateEmailClientRequest);
        verify(clientsRepository, times(0)).save(any(ClientEntity.class));
        verify(cognitoProperties, times(1)).getAwsCognitoIdentityProvider();
        verify(awsCognitoIdentityProvider, times(1)).signUp(signUpRequestArgumentCaptor.capture());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(NEW_EMAIL, signUpRequestArgumentCaptor.getValue().getUsername());
    }

    @Test
    public void should_return_BAD_REQUEST_Since_CognitoError_at_update_email() {
        updateEmailClientRequest.setPassword("");
        ResponseEntity<ClientResult> response = testedClass.updateClientEmail(new HttpHeaders(), ID_CLIENT, updateEmailClientRequest);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void should_return_PRECONDITION_FAILED_Since_newEmail_Exist_In_DB() {
        when(clientsRepository.findByEmailAddress(any(String.class))).thenReturn(clientEntityFound);
        ResponseEntity<ClientResult> response = testedClass.updateClientEmail(new HttpHeaders(), ID_CLIENT, updateEmailClientRequest);
        assertEquals(HttpStatus.PRECONDITION_FAILED, response.getStatusCode());
    }

    @Test
    public void should_return_PRECONDITION_FAILED_Since_New_Email_Exist_In_Cognito() {
        when(clientsRepository.findByIdClient(any(String.class))).thenReturn(Optional.of(clientEntityFound));
        when(cognitoProperties.getAwsCognitoIdentityProvider()).thenReturn(awsCognitoIdentityProvider);
        when(awsCognitoIdentityProvider.signUp(any(SignUpRequest.class))).thenThrow(UsernameExistsException.class);
        ResponseEntity<ClientResult> response = testedClass.updateClientEmail(new HttpHeaders(), ID_CLIENT, updateEmailClientRequest);
        verify(cognitoProperties, times(1)).getAwsCognitoIdentityProvider();
        verify(awsCognitoIdentityProvider, times(1)).signUp(signUpRequestArgumentCaptor.capture());
        assertEquals(HttpStatus.PRECONDITION_FAILED, response.getStatusCode());
        assertEquals(NEW_EMAIL, signUpRequestArgumentCaptor.getValue().getUsername());
    }

    @Test
    public void should_return_OK_at_update_email() {
        when(clientsRepository.findByIdClient(any(String.class)))
                .thenReturn(Optional.of(clientEntityFound));
        when(cognitoProperties.getAwsCognitoIdentityProvider()).thenReturn(awsCognitoIdentityProvider);
        when(awsCognitoIdentityProvider.signUp(any(SignUpRequest.class))).thenReturn(signUpResult);
        ResponseEntity<ClientResult> response = testedClass.updateClientEmail(new HttpHeaders(), ID_CLIENT, updateEmailClientRequest);
        verify(cognitoProperties, times(2)).getAwsCognitoIdentityProvider();
        verify(awsCognitoIdentityProvider, times(1)).signUp(signUpRequestArgumentCaptor.capture());
        verify(awsCognitoIdentityProvider, times(1))
                .adminDeleteUser(adminDisableUserRequestArgumentCaptor.capture());
        verify(clientsRepository, times(1)).save(clientEntityArgumentCaptor.capture());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(NEW_EMAIL, signUpRequestArgumentCaptor.getValue().getUsername());
        assertEquals(OLD_EMAIL, adminDisableUserRequestArgumentCaptor.getValue().getUsername());
        assertEquals(NEW_EMAIL, clientEntityArgumentCaptor.getValue().getEmailAddress());
        assertEquals(ID_CLIENT, clientEntityArgumentCaptor.getValue().getIdCognito());
    }

    private LoanDetail getLoanDetail1() {
        LoanDetail loanDetail1 = new LoanDetail();
        loanDetail1.setIdLoan("1010");
        loanDetail1.setIdCredit("1111");
        loanDetail1.setProductType("LOAN");
        loanDetail1.setBalance(500000d);
        return loanDetail1;
    }

    private LoanDetail getLoanDetail2() {
        LoanDetail loanDetail2 = new LoanDetail();
        loanDetail2.setIdLoan("2020");
        loanDetail2.setIdCredit("2222");
        loanDetail2.setProductType("LOAN");
        loanDetail2.setBalance(500000d);
        return loanDetail2;
    }

    private GetAccountResponse getAccountResponse1() {
        GetAccountResponse account1 = new GetAccountResponse();
        GetAccountResponse.Balance balance1 = new GetAccountResponse.Balance();
        GetAccountResponse.Balance availableBalance = new GetAccountResponse.Balance();
        balance1.setCurrency("COP");
        balance1.setAmount(100400d);
        availableBalance.setCurrency("COP");
        availableBalance.setAmount(100400d);
        account1.setId("1");
        account1.setBalance(balance1);
        account1.setAvailableBalance(availableBalance);
        account1.setCreationDate(DatesUtil.getLocalDateGMT5());
        account1.setGmf("true");
        return account1;
    }

    private GetAccountResponse getAccountResponse2() {
        GetAccountResponse account2 = new GetAccountResponse();
        GetAccountResponse.Balance balance2 = new GetAccountResponse.Balance();
        balance2.setCurrency("COP");
        balance2.setAmount(200000d);
        account2.setId("2");
        account2.setBalance(balance2);
        account2.setCreationDate(DatesUtil.getLocalDateGMT5());
        return account2;
    }

    private GetAccountResponse getAccountResponseWithInterestAccrued() {
        GetAccountResponse account1 = new GetAccountResponse();
        GetAccountResponse.Balance balance1 = new GetAccountResponse.Balance();
        GetAccountResponse.Balance availableBalance = new GetAccountResponse.Balance();
        GetAccountResponse.InterestAccrued interestAccrued = new GetAccountResponse.InterestAccrued();
        balance1.setCurrency("COP");
        balance1.setAmount(0.0);
        interestAccrued.setCurrency("COP");
        interestAccrued.setAmount(12.43);
        availableBalance.setCurrency("COP");
        availableBalance.setAmount(0.0);
        account1.setId("1");
        account1.setBalance(balance1);
        account1.setInterestAccrued(interestAccrued);
        account1.setAvailableBalance(availableBalance);
        account1.setCreationDate(DatesUtil.getLocalDateGMT5());
        account1.setGmf("true");
        return account1;
    }

    private List<GetAccountResponse> getaccountsGMF() {
        List<GetAccountResponse> accounts = new ArrayList<>();
        GetAccountResponse getAccountResponse = getAccountResponse1();
        getAccountResponse.setState(AccountStatusEnum.ACTIVE.name());
        getAccountResponse.setNumber("LCES471");
        getAccountResponse.setGmf("true");
        accounts.add(getAccountResponse);
        return accounts;
    }

    @Test
    public void should_return_ok_getProductsBasicInfoByClient() throws ProviderException {
        List<String> expectedBalanceClosureMethods = new ArrayList<>();
        expectedBalanceClosureMethods.add(BalanceClosureMethods.LULO.name());
        expectedBalanceClosureMethods.add(BalanceClosureMethods.INTERBANK.name());
        expectedBalanceClosureMethods.add(BalanceClosureMethods.CARDLESS_WITHDRAWAL.name());
        expectedBalanceClosureMethods.add(BalanceClosureMethods.OFFICE_WITHDRAWAL.name());
        expectedBalanceClosureMethods.add(BalanceClosureMethods.DONATION.name());
        String idClient = "123456789";
        clientEntity.setIdCbs("102030");
        GetLoanResponse getLoanResponse = getGetLoanResponse();
        List<LoanDetail> loanDetails = getLoanDetails();
        List<GetAccountResponse> accounts = getGetAccountResponses();
        LoanDetailRetrofitResponse loanDetailRetrofitResponse = new LoanDetailRetrofitResponse();
        loanDetailRetrofitResponse.setContent(loanDetails);
        when(clientsRepository.findByIdClient(any(String.class))).thenReturn(Optional.of(clientEntity));
        when(flexibilitySdk.getAccountsByClientId(any(GetAccountRequest.class))).thenReturn(accounts);
        when(getLoanDetailOperationsService.getCreditsProducts(any(HashMap.class), any(String.class)))
                .thenReturn(loanDetailRetrofitResponse);
        when(flexibilitySdk.getLoanByLoanAccountId(any(GetLoanRequest.class)))
                .thenReturn(getLoanResponse);
        when(transactionsPort.hasPendingTransactions(any(), any()))
                .thenReturn(buildNotPendingTransactionResponses());
        ResponseEntity<ClientResult> response =
                testedClass.getProductsBasicInfoByClient(new HttpHeaders(), idClient);
        ClientSuccessResult result = (ClientSuccessResult) response.getBody();
        ProductsBasicInfo productsBasicInfo = (ProductsBasicInfo) result.getContent();
        assertEquals(HttpStatus.OK.name(), response.getStatusCode().name());
        assertEquals(2, productsBasicInfo.getSavingsAccounts().size());
        assertEquals(2, productsBasicInfo.getCredits().size());
        assertEquals(getLoanResponse.getId(), productsBasicInfo.getCredits().get(0).getIdCredit());
        assertEquals(false, productsBasicInfo.isAllSavingsAccountsCloseable());
        assertEquals(false, productsBasicInfo.getSavingsAccounts().get(0).isSavingAccountClosable());
        MatcherAssert.assertThat(
                productsBasicInfo.getAvailableClosingMethods(),
                CoreMatchers.is(expectedBalanceClosureMethods));
        assertEquals(
                BigDecimal.valueOf(100000D),
                productsBasicInfo.getSavingsAccounts().get(0).getCardlessAmount());
    }

    @Test
    public void should_return_ok_getProductsBasicInfoByClientWithBalanceAvailableForLuloTransfer()
            throws ProviderException {
        List<String> expectedBalanceClosureMethods = new ArrayList<>();
        expectedBalanceClosureMethods.add(BalanceClosureMethods.LULO.name());
        expectedBalanceClosureMethods.add(BalanceClosureMethods.DONATION.name());
        String idClient = "123456789";
        clientEntity.setIdCbs("102030");
        GetLoanResponse getLoanResponse = getGetLoanResponse();
        List<LoanDetail> loanDetails = getLoanDetails();
        List<GetAccountResponse> accounts = getGetAccountResponses();
        GetAccountResponse.Balance balance = new GetAccountResponse.Balance();
        balance.setAmount(1000D);
        balance.setCurrency("COP");
        accounts.get(0).setAvailableBalance(balance);
        LoanDetailRetrofitResponse loanDetailRetrofitResponse = new LoanDetailRetrofitResponse();
        loanDetailRetrofitResponse.setContent(loanDetails);
        when(clientsRepository.findByIdClient(any(String.class))).thenReturn(Optional.of(clientEntity));
        when(flexibilitySdk.getAccountsByClientId(any(GetAccountRequest.class))).thenReturn(accounts);
        when(getLoanDetailOperationsService.getCreditsProducts(any(HashMap.class), any(String.class)))
                .thenReturn(loanDetailRetrofitResponse);
        when(flexibilitySdk.getLoanByLoanAccountId(any(GetLoanRequest.class)))
                .thenReturn(getLoanResponse);
        when(transactionsPort.hasPendingTransactions(any(), any()))
                .thenReturn(buildNotPendingTransactionResponses());
        ResponseEntity<ClientResult> response =
                testedClass.getProductsBasicInfoByClient(new HttpHeaders(), idClient);
        ClientSuccessResult result = (ClientSuccessResult) response.getBody();
        ProductsBasicInfo productsBasicInfo = (ProductsBasicInfo) result.getContent();
        assertEquals(HttpStatus.OK.name(), response.getStatusCode().name());
        assertEquals(2, productsBasicInfo.getSavingsAccounts().size());
        assertEquals(2, productsBasicInfo.getCredits().size());
        assertEquals(getLoanResponse.getId(), productsBasicInfo.getCredits().get(0).getIdCredit());
        assertEquals(false, productsBasicInfo.isAllSavingsAccountsCloseable());
        assertEquals(false, productsBasicInfo.getSavingsAccounts().get(0).isSavingAccountClosable());
        MatcherAssert.assertThat(
                productsBasicInfo.getAvailableClosingMethods(),
                CoreMatchers.is(expectedBalanceClosureMethods));
    }

    @Test
    public void should_return_ok_getProductsBasicInfoByClientWithBalanceAvailableForCashierCheck()
            throws ProviderException {
        List<String> expectedBalanceClosureMethods = new ArrayList<>();
        expectedBalanceClosureMethods.add(BalanceClosureMethods.LULO.name());
        expectedBalanceClosureMethods.add(BalanceClosureMethods.INTERBANK.name());
        expectedBalanceClosureMethods.add(BalanceClosureMethods.CASHIERCHECK.name());
        expectedBalanceClosureMethods.add(BalanceClosureMethods.DONATION.name());
        String idClient = "123456789";
        clientEntity.setIdCbs("102030");
        GetLoanResponse getLoanResponse = getGetLoanResponse();
        List<LoanDetail> loanDetails = getLoanDetails();
        List<GetAccountResponse> accounts = getGetAccountResponses();
        GetAccountResponse.Balance balance = new GetAccountResponse.Balance();
        balance.setAmount(1205000D);
        balance.setCurrency("COP");
        accounts.get(0).setAvailableBalance(balance);
        LoanDetailRetrofitResponse loanDetailRetrofitResponse = new LoanDetailRetrofitResponse();
        loanDetailRetrofitResponse.setContent(loanDetails);
        when(clientsRepository.findByIdClient(any(String.class))).thenReturn(Optional.of(clientEntity));
        when(flexibilitySdk.getAccountsByClientId(any(GetAccountRequest.class))).thenReturn(accounts);
        when(getLoanDetailOperationsService.getCreditsProducts(any(HashMap.class), any(String.class)))
                .thenReturn(loanDetailRetrofitResponse);
        when(flexibilitySdk.getLoanByLoanAccountId(any(GetLoanRequest.class)))
                .thenReturn(getLoanResponse);
        when(transactionsPort.hasPendingTransactions(any(), any()))
                .thenReturn(buildNotPendingTransactionResponses());
        ResponseEntity<ClientResult> response =
                testedClass.getProductsBasicInfoByClient(new HttpHeaders(), idClient);
        ClientSuccessResult result = (ClientSuccessResult) response.getBody();
        ProductsBasicInfo productsBasicInfo = (ProductsBasicInfo) result.getContent();
        assertEquals(HttpStatus.OK.name(), response.getStatusCode().name());
        assertEquals(2, productsBasicInfo.getSavingsAccounts().size());
        assertEquals(2, productsBasicInfo.getCredits().size());
        assertEquals(getLoanResponse.getId(), productsBasicInfo.getCredits().get(0).getIdCredit());
        assertEquals(false, productsBasicInfo.isAllSavingsAccountsCloseable());
        assertEquals(false, productsBasicInfo.getSavingsAccounts().get(0).isSavingAccountClosable());
        MatcherAssert.assertThat(
                productsBasicInfo.getAvailableClosingMethods(),
                CoreMatchers.is(expectedBalanceClosureMethods));
    }

    @Test
    public void should_return_ok_getProductsBasicInfoByClientWithNoClosureMethodsDueToPendingBalance()
            throws ProviderException {
        List<String> expectedBalanceClosureMethods = new ArrayList<>();
        expectedBalanceClosureMethods.add(BalanceClosureMethods.LULO.name());
        expectedBalanceClosureMethods.add(BalanceClosureMethods.DONATION.name());
        expectedBalanceClosureMethods.add(TransactionClosureMethods.PENDING_TRANSACTIONS.name());
        String idClient = "123456789";
        clientEntity.setIdCbs("102030");
        GetLoanResponse getLoanResponse = getGetLoanResponse();
        List<LoanDetail> loanDetails = getLoanDetails();
        List<GetAccountResponse> accounts = getGetAccountResponses();
        GetAccountResponse.Balance balance = new GetAccountResponse.Balance();
        balance.setAmount(900D);
        balance.setCurrency("COP");
        accounts.get(0).setAvailableBalance(balance);
        LoanDetailRetrofitResponse loanDetailRetrofitResponse = new LoanDetailRetrofitResponse();
        loanDetailRetrofitResponse.setContent(loanDetails);
        when(clientsRepository.findByIdClient(any(String.class))).thenReturn(Optional.of(clientEntity));
        when(flexibilitySdk.getAccountsByClientId(any(GetAccountRequest.class))).thenReturn(accounts);
        when(getLoanDetailOperationsService.getCreditsProducts(any(HashMap.class), any(String.class)))
                .thenReturn(loanDetailRetrofitResponse);
        when(flexibilitySdk.getLoanByLoanAccountId(any(GetLoanRequest.class)))
                .thenReturn(getLoanResponse);
        when(transactionsPort.hasPendingTransactions(any(), any()))
                .thenReturn(buildPendingTransactionResponses());
        ResponseEntity<ClientResult> response =
                testedClass.getProductsBasicInfoByClient(new HttpHeaders(), idClient);
        ClientSuccessResult result = (ClientSuccessResult) response.getBody();
        ProductsBasicInfo productsBasicInfo = (ProductsBasicInfo) result.getContent();
        assertEquals(HttpStatus.OK.name(), response.getStatusCode().name());
        assertEquals(2, productsBasicInfo.getSavingsAccounts().size());
        assertEquals(2, productsBasicInfo.getCredits().size());
        assertEquals(getLoanResponse.getId(), productsBasicInfo.getCredits().get(0).getIdCredit());
        assertEquals(false, productsBasicInfo.isAllSavingsAccountsCloseable());
        assertEquals(false, productsBasicInfo.getSavingsAccounts().get(0).isSavingAccountClosable());
        MatcherAssert.assertThat(
                productsBasicInfo.getAvailableClosingMethods(),
                CoreMatchers.is(expectedBalanceClosureMethods));
    }

    private Either<TransactionsError, Boolean> buildPendingTransactionResponses() {
        return Either.right(true);
    }

    private Either<TransactionsError, Boolean> buildNotPendingTransactionResponses() {
        return Either.right(false);
    }

    @NotNull
    private List<GetAccountResponse> getGetAccountResponses() {
        List<GetAccountResponse> accounts = new ArrayList<>();
        GetAccountResponse account1 = getAccountResponse1();
        GetAccountResponse account2 = getAccountResponse2();
        accounts.add(account1);
        accounts.add(account2);
        return accounts;
    }

    @NotNull
    private List<GetAccountResponse> getGetAccountResponseWithInterestAccrued() {
        List<GetAccountResponse> accounts = new ArrayList<>();
        GetAccountResponse account1 = getAccountResponseWithInterestAccrued();
        accounts.add(account1);
        return accounts;
    }

    @NotNull
    private List<LoanDetail> getLoanDetails() {
        List<LoanDetail> loanDetails = new ArrayList<>();
        LoanDetail loanDetail1 = getLoanDetail1();
        LoanDetail loanDetail2 = getLoanDetail2();
        loanDetails.add(loanDetail1);
        loanDetails.add(loanDetail2);
        return loanDetails;
    }

    @NotNull
    private GetLoanResponse getGetLoanResponse() {
        GetLoanResponse.Balance balance = new GetLoanResponse.Balance();
        balance.setAmount(500000d);
        GetLoanResponse getLoanResponse = new GetLoanResponse();
        getLoanResponse.setId("1010");
        getLoanResponse.setBalance(balance);
        getLoanResponse.setCreationDate(DatesUtil.getLocalDateGMT5());
        return getLoanResponse;
    }

    @Test
    public void should_return_ok_getProductsBasicInfoByClient_accounts_are_closed()
            throws ProviderException {
        String idClient = "123456789";
        clientEntity.setIdCbs("102030");
        List<GetAccountResponse> accounts = new ArrayList<>();
        List<LoanDetail> loanDetails = new ArrayList<>();
        LoanDetailRetrofitResponse loanDetailRetrofitResponse = new LoanDetailRetrofitResponse();
        GetLoanResponse getLoanResponse = getGetLoanResponse();
        GetAccountResponse account1 = getAccountResponse1();
        account1.setState("CLOSED");
        GetAccountResponse account2 = getAccountResponse2();
        account2.setState("CLOSED");
        accounts.add(account1);
        accounts.add(account2);
        loanDetailRetrofitResponse.setContent(loanDetails);
        when(clientsRepository.findByIdClient(any(String.class))).thenReturn(Optional.of(clientEntity));
        when(flexibilitySdk.getAccountsByClientId(any(GetAccountRequest.class))).thenReturn(accounts);
        when(getLoanDetailOperationsService.getCreditsProducts(any(HashMap.class), any(String.class)))
                .thenReturn(loanDetailRetrofitResponse);
        when(flexibilitySdk.getLoanByLoanAccountId(any(GetLoanRequest.class)))
                .thenReturn(getLoanResponse);
        when(transactionsPort.hasPendingTransactions(any(), any()))
                .thenReturn(buildPendingTransactionResponses());
        ResponseEntity<ClientResult> response =
                testedClass.getProductsBasicInfoByClient(new HttpHeaders(), idClient);
        ClientSuccessResult result = (ClientSuccessResult) response.getBody();
        ProductsBasicInfo productsBasicInfo = (ProductsBasicInfo) result.getContent();
        assertEquals(HttpStatus.OK.name(), response.getStatusCode().name());
        assertEquals(0, productsBasicInfo.getSavingsAccounts().size());
        assertEquals(0, productsBasicInfo.getCredits().size());
        assertEquals(true, productsBasicInfo.isAllSavingsAccountsCloseable());
    }

    @Test
    public void should_return_ok_getProductsBasicInfoByClient_saving_loans_empty()
            throws ProviderException {
        clientEntity.setIdCbs("102030");
        List<LoanDetail> loanDetails = new ArrayList<>();
        LoanDetailRetrofitResponse loanDetailRetrofitResponse = new LoanDetailRetrofitResponse();
        GetLoanResponse.Balance balance = new GetLoanResponse.Balance();
        balance.setAmount(500000d);
        loanDetailRetrofitResponse.setContent(loanDetails);
        when(clientsRepository.findByIdClient(any(String.class))).thenReturn(Optional.of(clientEntity));
        when(flexibilitySdk.getAccountsByClientId(any(GetAccountRequest.class))).thenReturn(null);
        when(getLoanDetailOperationsService.getCreditsProducts(any(HashMap.class), any(String.class)))
                .thenReturn(loanDetailRetrofitResponse);
        when(transactionsPort.hasPendingTransactions(any(), any()))
                .thenReturn(buildPendingTransactionResponses());
        ResponseEntity<ClientResult> response =
                testedClass.getProductsBasicInfoByClient(new HttpHeaders(), ID_CLIENT);
        ClientSuccessResult result = (ClientSuccessResult) response.getBody();
        ProductsBasicInfo productsBasicInfo = (ProductsBasicInfo) result.getContent();
        verify(flexibilitySdk, times(1))
                .getAccountsByClientId(getAccountRequestArgumentCaptor.capture());
        verify(clientsRepository, times(2)).findByIdClient(findByIdClientEntityCaptor.capture());
        assertEquals(clientEntity.getIdCbs(), getAccountRequestArgumentCaptor.getValue().getClientId());
        assertEquals(ID_CLIENT, findByIdClientEntityCaptor.getAllValues().get(0));
        assertEquals(ID_CLIENT, findByIdClientEntityCaptor.getAllValues().get(1));
        assertEquals(HttpStatus.OK.name(), response.getStatusCode().name());
        assertEquals(0, productsBasicInfo.getSavingsAccounts().size());
        assertEquals(0, productsBasicInfo.getCredits().size());
        assertTrue(productsBasicInfo.isAllSavingsAccountsCloseable());
    }

    @Test
    public void should_getProductsBasicInfoByClient_flexibilitySdk_throws_ProviderException()
            throws ProviderException {
        String idClient = "123456789";
        clientEntity.setIdCbs("102030");
        List<GetAccountResponse> accounts = new ArrayList<>();
        List<LoanDetail> loanDetails = new ArrayList<>();
        List<ValidationResult> validationResultsReceveid;
        LoanDetailRetrofitResponse loanDetailRetrofitResponse = new LoanDetailRetrofitResponse();
        LoanDetail loanDetail1 = getLoanDetail1();
        LoanDetail loanDetail2 = getLoanDetail2();
        GetAccountResponse account1 = getAccountResponse1();
        GetAccountResponse account2 = getAccountResponse2();
        accounts.add(account1);
        accounts.add(account2);
        loanDetails.add(loanDetail1);
        loanDetails.add(loanDetail2);
        loanDetailRetrofitResponse.setContent(loanDetails);
        when(clientsRepository.findByIdClient(any(String.class))).thenReturn(Optional.of(clientEntity));
        when(flexibilitySdk.getAccountsByClientId(any(GetAccountRequest.class)))
                .thenThrow(new ProviderException("Error message", "Error code"));
        ResponseEntity<ClientResult> response =
                testedClass.getProductsBasicInfoByClient(new HttpHeaders(), idClient);
        ClientErrorResult result = (ClientErrorResult) response.getBody();
        validationResultsReceveid = result.getErrors();
        assertEquals(HttpStatus.NOT_ACCEPTABLE.name(), response.getStatusCode().name());
        assertEquals(
                MambuErrorsResultEnum.MAMBU_SERVICE_ERROR.name(),
                validationResultsReceveid.get(0).getFailure());
    }

    @Test
    public void should_getProductsBasicInfoByClient__client_does_not_exists()
            throws ProviderException {
        String idClient = "123456789";
        List<GetAccountResponse> accounts = new ArrayList<>();
        List<LoanDetail> loanDetails = new ArrayList<>();
        List<ValidationResult> validationResultsReceveid;
        LoanDetailRetrofitResponse loanDetailRetrofitResponse = new LoanDetailRetrofitResponse();
        LoanDetail loanDetail1 = getLoanDetail1();
        LoanDetail loanDetail2 = getLoanDetail2();
        GetAccountResponse account1 = getAccountResponse1();
        GetAccountResponse account2 = getAccountResponse2();
        accounts.add(account1);
        accounts.add(account2);
        loanDetails.add(loanDetail1);
        loanDetails.add(loanDetail2);
        loanDetailRetrofitResponse.setContent(loanDetails);
        when(clientsRepository.findByIdClient(any(String.class))).thenReturn(Optional.empty());
        ResponseEntity<ClientResult> response =
                testedClass.getProductsBasicInfoByClient(new HttpHeaders(), idClient);
        ClientErrorResult result = (ClientErrorResult) response.getBody();
        validationResultsReceveid = result.getErrors();
        assertEquals(HttpStatus.NOT_ACCEPTABLE.name(), response.getStatusCode().name());
        assertEquals(
                ClientErrorResultsEnum.CLIENT_NOT_FOUND_IN_DB.name(),
                validationResultsReceveid.get(0).getFailure());
    }

    @Test
    public void should_getProductsBasicInfoByClient_loanDetailRetrofitResponse_has_errors()
            throws ProviderException {
        String idClient = "123456789";
        clientEntity.setIdCbs("102030");
        List<GetAccountResponse> accounts = new ArrayList<>();
        List<LoanDetail> loanDetails = new ArrayList<>();
        LoanDetailRetrofitResponse loanDetailRetrofitResponse = new LoanDetailRetrofitResponse();
        List<ValidationResult> validationResults = new ArrayList<>();
        validationResults.add(new ValidationResult("", ""));
        LoanDetail loanDetail1 = getLoanDetail1();
        LoanDetail loanDetail2 = getLoanDetail2();
        GetAccountResponse account1 = getAccountResponse1();
        GetAccountResponse account2 = getAccountResponse2();
        accounts.add(account1);
        accounts.add(account2);
        loanDetails.add(loanDetail1);
        loanDetails.add(loanDetail2);
        loanDetailRetrofitResponse.setContent(null);
        loanDetailRetrofitResponse.setHasErrors(true);
        loanDetailRetrofitResponse.setErrors(validationResults);
        when(clientsRepository.findByIdClient(any(String.class))).thenReturn(Optional.of(clientEntity));
        when(flexibilitySdk.getAccountsByClientId(any(GetAccountRequest.class))).thenReturn(accounts);
        when(getLoanDetailOperationsService.getCreditsProducts(any(HashMap.class), any(String.class)))
                .thenThrow(ServiceException.class);
        ResponseEntity<ClientResult> response =
                testedClass.getProductsBasicInfoByClient(new HttpHeaders(), idClient);
        assertEquals(HttpStatus.NOT_ACCEPTABLE.name(), response.getStatusCode().name());
    }

    @Test
    public void should_return_ok_getProductsBasicInfoByClient_does_not_exists_credit()
            throws ProviderException {
        String idClient = "123456789";
        clientEntity.setIdCbs("102030");
        List<GetAccountResponse> accounts = new ArrayList<>();
        List<LoanDetail> loanDetails = new ArrayList<>();
        LoanDetailRetrofitResponse loanDetailRetrofitResponse = new LoanDetailRetrofitResponse();
        GetAccountResponse account1 = getAccountResponse1();
        GetAccountResponse account2 = getAccountResponse2();
        accounts.add(account1);
        accounts.add(account2);
        loanDetailRetrofitResponse.setContent(loanDetails);
        when(clientsRepository.findByIdClient(any(String.class))).thenReturn(Optional.of(clientEntity));
        when(flexibilitySdk.getAccountsByClientId(any(GetAccountRequest.class))).thenReturn(accounts);
        when(getLoanDetailOperationsService.getCreditsProducts(any(HashMap.class), any(String.class)))
                .thenReturn(loanDetailRetrofitResponse);
        when(transactionsPort.hasPendingTransactions(any(), any()))
                .thenReturn(buildPendingTransactionResponses());
        ResponseEntity<ClientResult> response =
                testedClass.getProductsBasicInfoByClient(new HttpHeaders(), idClient);
        ClientSuccessResult result = (ClientSuccessResult) response.getBody();
        ProductsBasicInfo productsBasicInfo = (ProductsBasicInfo) result.getContent();
        assertEquals(HttpStatus.OK.name(), response.getStatusCode().name());
        assertEquals(2, productsBasicInfo.getSavingsAccounts().size());
        assertEquals(0, productsBasicInfo.getCredits().size());
        assertEquals(false, productsBasicInfo.isAllSavingsAccountsCloseable());
        assertEquals(false, productsBasicInfo.getSavingsAccounts().get(0).isSavingAccountClosable());
    }

    @Test
    public void shouldReturnOkVerifyEmailClientInformationSinceClientExists()
            throws ProviderException {
        when(clientsRepository.findByEmailAddress(verifyEmailRequest.getEmail()))
                .thenReturn(clientEntity);
        ResponseEntity response =
                testedClass.verifyEmailClientInformation(new HttpHeaders(), verifyEmailRequest.getEmail());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void shouldReturnOkGetClientByTypePhoneSinceClientExists() {
        when(clientsRepository.findByPhonePrefixAndPhoneNumber(any(Integer.class), any(String.class)))
                .thenReturn(Optional.of(clientEntity));
        ResponseEntity response =
                testedClass.getClientByType(new HttpHeaders(), SEARCH_TYPE_PHONE, PHONE_NUMBER);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void shouldReturnOkGetClientByTypeInternalPhoneSinceClientExists() {
        when(clientsRepository.findByPhonePrefixAndPhoneNumber(any(Integer.class), any(String.class)))
                .thenReturn(Optional.of(clientEntity));
        ResponseEntity response =
                testedClass.getClientByTypeInternal(new HttpHeaders(), SEARCH_TYPE_PHONE, PHONE_NUMBER);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void shouldReturnOkGetClientByTypePhoneSinceClientDoesNotExists() {
        when(clientsRepository.findByPhonePrefixAndPhoneNumber(any(Integer.class), any(String.class)))
                .thenReturn(null);
        ResponseEntity response =
                testedClass.getClientByType(new HttpHeaders(), SEARCH_TYPE_PHONE, PHONE_NUMBER);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void shouldReturnOkGetClientByTypeInternalPhoneSinceClientDoesNotExists() {
        when(clientsRepository.findByPhonePrefixAndPhoneNumber(any(Integer.class), any(String.class)))
                .thenReturn(null);
        ResponseEntity response =
                testedClass.getClientByTypeInternal(new HttpHeaders(), SEARCH_TYPE_PHONE, PHONE_NUMBER);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void shouldReturnOkGetClientByTypeEmailSinceClientExists() {
        when(clientsRepository.findByEmailAddress(any(String.class))).thenReturn(clientEntity);
        ResponseEntity response =
                testedClass.getClientByType(new HttpHeaders(), SEARCH_TYPE_EMAIL, NEW_EMAIL);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(((ClientInformationByTypeResponse) ((ClientSuccessResult) response.getBody()).getContent())
                .getEmailVerified());
        assertEquals(
                ID_CLIENT,
                ((ClientInformationByTypeResponse) ((ClientSuccessResult) response.getBody()).getContent())
                        .getIdClient());
        assertEquals(
                IdentityBiometricStatus.FINISHED.name(),
                ((ClientInformationByTypeResponse) ((ClientSuccessResult) response.getBody()).getContent())
                        .getBiometricStatus());
        assertEquals(
                CHECKPOINT_ON_BOARDING,
                ((ClientInformationByTypeResponse) ((ClientSuccessResult) response.getBody()).getContent())
                        .getCheckpoint());
        assertEquals(
                PRODUCT_SELECTED,
                ((ClientInformationByTypeResponse) ((ClientSuccessResult) response.getBody()).getContent())
                        .getProductSelected());

        assertEquals(
                RiskLevelBlackList.NO_RISK.getLevel(),
                ((ClientInformationByTypeResponse) ((ClientSuccessResult) response.getBody()).getContent())
                        .getRiskLevel());
        assertEquals(
                StateBlackList.NON_BLACKLISTED.name(),
                ((ClientInformationByTypeResponse) ((ClientSuccessResult) response.getBody()).getContent())
                        .getBlacklistState());
        assertEquals(
                WHITELIST_EXPIRATION_DATE,
                ((ClientInformationByTypeResponse) ((ClientSuccessResult) response.getBody()).getContent())
                        .getWhitelistExpirationDate());
    }

    @Test
    public void shouldReturnOkGetClientByTypeEmailWithZeroSecondsAcceptance() {
        ClientAcceptance acceptance = new ClientAcceptance();
        acceptance.setDocumentAcceptancesTimestamp(LocalDateTime.parse("2020-08-14T16:15"));
        clientEntity.setAcceptances(acceptance);
        when(clientsRepository.findByEmailAddress(any(String.class))).thenReturn(clientEntity);
        ResponseEntity response =
                testedClass.getClientByType(new HttpHeaders(), SEARCH_TYPE_EMAIL, NEW_EMAIL);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals("2020-08-14T16:15:00", ((ClientInformationByTypeResponse) ((ClientSuccessResult) response.getBody()).getContent())
                .getDocumentAcceptancesTimestamp());
    }

    @Test
    public void shouldReturnOkGetClientByTypeInternalEmailSinceClientExists() {
        when(clientsRepository.findByEmailAddress(any(String.class))).thenReturn(clientEntity);
        ResponseEntity response =
                testedClass.getClientByTypeInternal(new HttpHeaders(), SEARCH_TYPE_EMAIL, NEW_EMAIL);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void shouldReturnOkGetClientByTypeEmailSinceClientDoesNotExists() {
        when(clientsRepository.findByEmailAddress(any(String.class))).thenReturn(null);
        ResponseEntity response =
                testedClass.getClientByType(new HttpHeaders(), SEARCH_TYPE_EMAIL, NEW_EMAIL);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void shouldReturnOkGetClientByTypeInternalEmailSinceClientDoesNotExists() {
        when(clientsRepository.findByEmailAddress(any(String.class))).thenReturn(null);
        ResponseEntity response =
                testedClass.getClientByTypeInternal(new HttpHeaders(), SEARCH_TYPE_EMAIL, NEW_EMAIL);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void shouldReturnOkGetClientByTypeIdClientSinceClientExists() {
        when(clientsRepository.findByIdClient(any(String.class))).thenReturn(Optional.of(clientEntity));
        ResponseEntity response =
                testedClass.getClientByType(new HttpHeaders(), SEARCH_TYPE_ID_CLIENT, ID_CLIENT);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void shouldReturnOkGetClientByTypeInternalIdClientSinceClientExists() {
        when(clientsRepository.findByIdClient(any(String.class))).thenReturn(Optional.of(clientEntity));
        ResponseEntity response =
                testedClass.getClientByTypeInternal(new HttpHeaders(), SEARCH_TYPE_ID_CLIENT, ID_CLIENT);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void shouldReturnOkGetClientByTypeIdClientSinceClientDoesNotExists() {
        when(clientsRepository.findByIdClient(any(String.class))).thenReturn(null);
        ResponseEntity response =
                testedClass.getClientByType(new HttpHeaders(), SEARCH_TYPE_ID_CLIENT, ID_CLIENT);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void shouldReturnOkGetClientByTypeInternalIdClientSinceClientDoesNotExists() {
        when(clientsRepository.findByIdClient(any(String.class))).thenReturn(null);
        ResponseEntity response =
                testedClass.getClientByTypeInternal(new HttpHeaders(), SEARCH_TYPE_ID_CLIENT, ID_CLIENT);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void shouldReturnClientNotFoundVerifyEmailClientInformationSinceClientDoesNotExists()
            throws ProviderException {
        when(clientsRepository.findByEmailAddress(verifyEmailRequest.getEmail())).thenReturn(null);
        ResponseEntity response =
                testedClass.verifyEmailClientInformation(new HttpHeaders(), verifyEmailRequest.getEmail());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void shouldReturnInternalServerErrorVerifyEmailClientInformationSinceDBConnectionError()
            throws ProviderException {
        when(clientsRepository.findByEmailAddress(verifyEmailRequest.getEmail()))
                .thenThrow(new SdkClientException("Database Error Connection"));
        ResponseEntity response =
                testedClass.verifyEmailClientInformation(new HttpHeaders(), verifyEmailRequest.getEmail());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void shouldReturnTrueLoginIsUnauthorized() {
        when(cognitoProperties.getClientapp_id()).thenReturn("fakeMyPoolId");
        when(cognitoProperties.getAwsCognitoIdentityProvider()).thenReturn(awsCognitoIdentityProvider);
        when(awsCognitoIdentityProvider.initiateAuth(any(InitiateAuthRequest.class)))
                .thenThrow(new NotAuthorizedException(""));
        when(clientsRepository.findByIdClientAndIdCard(any(String.class), anyString()))
                .thenReturn(Optional.of(clientEntity));
        when(clientsRepository.findByEmailAddress(any(String.class))).thenReturn(clientEntity);
        when(loginAttemptsRepository.findByIdClient(any(UUID.class))).thenReturn(loginAttemptsOpt);
        when(loginAttemptsService.saveLoginAttempt(clientEntity.getIdClient(), false))
                .thenReturn(failedAttempt);
        when(loginAttemptsService.getAttemptTimeFromAttemptEntity(any(AttemptEntity.class)))
                .thenReturn(attemptTimeResult);
        when(loginAttemptsService.getAttemptTimeResult(any(AttemptTimeResult.class)))
                .thenReturn(ATTEMPT_JSON);
        ResponseEntity response = testedClass.login(new HttpHeaders(), signUp);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(loginAttemptsService, times(1))
                .getAttemptTimeResult(attemptTimeResultArgumentCaptor.capture());
        assertEquals(Boolean.TRUE, attemptTimeResultArgumentCaptor.getValue().getMaxAttempt());
    }

    @Test
    public void shouldReturnFalseLoginIsUnauthorized() {
        when(cognitoProperties.getClientapp_id()).thenReturn("fakeMyPoolId");
        when(cognitoProperties.getAwsCognitoIdentityProvider()).thenReturn(awsCognitoIdentityProvider);
        when(awsCognitoIdentityProvider.initiateAuth(any(InitiateAuthRequest.class)))
                .thenThrow(new NotAuthorizedException(""));
        when(clientsRepository.findByIdClientAndIdCard(any(String.class), anyString()))
                .thenReturn(Optional.of(clientEntity));
        when(clientsRepository.findByEmailAddress(any(String.class))).thenReturn(clientEntity);
        when(loginAttemptsRepository.findByIdClient(any(UUID.class))).thenReturn(loginAttemptsOpt);
        when(loginAttemptsService.saveLoginAttempt(clientEntity.getIdClient(), false))
                .thenReturn(failedAttempt);
        when(loginAttemptsService.getAttemptTimeFromAttemptEntity(any(AttemptEntity.class)))
                .thenReturn(attemptTimeResultFalse);
        when(loginAttemptsService.getAttemptTimeResult(any(AttemptTimeResult.class)))
                .thenReturn(ATTEMPT_JSON);
        ResponseEntity response = testedClass.login(new HttpHeaders(), signUp);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(loginAttemptsService, times(1))
                .getAttemptTimeResult(attemptTimeResultArgumentCaptor.capture());
        assertEquals(Boolean.FALSE, attemptTimeResultArgumentCaptor.getValue().getMaxAttempt());
    }

    @Test
    public void shouldReturnClosingMethodsWithInterestAccrued() throws ProviderException {
        List<String> expectedBalanceClosureMethods = new ArrayList<>();
        expectedBalanceClosureMethods.add(BalanceClosureMethods.LULO.name());
        expectedBalanceClosureMethods.add(BalanceClosureMethods.DONATION.name());

        String idClient = "123456789";
        clientEntity.setIdCbs("102030");

        List<LoanDetail> loanDetails = new ArrayList<>();
        List<GetAccountResponse> accounts = getGetAccountResponseWithInterestAccrued();
        LoanDetailRetrofitResponse loanDetailRetrofitResponse = new LoanDetailRetrofitResponse();
        loanDetailRetrofitResponse.setContent(loanDetails);

        when(clientsRepository.findByIdClient(any(String.class))).thenReturn(Optional.of(clientEntity));
        when(flexibilitySdk.getAccountsByClientId(any(GetAccountRequest.class))).thenReturn(accounts);
        when(getLoanDetailOperationsService.getCreditsProducts(any(HashMap.class), any(String.class)))
                .thenReturn(loanDetailRetrofitResponse);
        when(flexibilitySdk.getLoanByLoanAccountId(any(GetLoanRequest.class)))
                .thenReturn(null);
        when(transactionsPort.hasPendingTransactions(any(), any()))
                .thenReturn(buildNotPendingTransactionResponses());

        ResponseEntity<ClientResult> response =
                testedClass.getProductsBasicInfoByClient(new HttpHeaders(), idClient);
        ClientSuccessResult result = (ClientSuccessResult) response.getBody();
        ProductsBasicInfo productsBasicInfo = (ProductsBasicInfo) result.getContent();

        assertEquals(HttpStatus.OK.name(), response.getStatusCode().name());
        assertEquals(1, productsBasicInfo.getSavingsAccounts().size());
        assertEquals(true, productsBasicInfo.isAllSavingsAccountsCloseable());
        assertEquals(true, productsBasicInfo.getSavingsAccounts().get(0).isSavingAccountClosable());
        MatcherAssert.assertThat(
                productsBasicInfo.getAvailableClosingMethods(),
                CoreMatchers.is(expectedBalanceClosureMethods));

    }
}
