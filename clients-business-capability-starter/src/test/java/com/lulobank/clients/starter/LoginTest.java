package com.lulobank.clients.starter;

import com.amazonaws.services.cognitoidp.model.AuthenticationResultType;
import com.amazonaws.services.cognitoidp.model.InitiateAuthRequest;
import com.amazonaws.services.cognitoidp.model.InitiateAuthResult;
import com.amazonaws.services.cognitoidp.model.NotAuthorizedException;
import com.amazonaws.services.cognitoidp.model.UserNotFoundException;
import com.lulobank.clients.sdk.operations.util.CheckPoints;
import com.lulobank.clients.services.domain.StateBlackList;
import com.lulobank.clients.services.features.login.model.AttemptTimeResult;
import com.lulobank.clients.services.features.login.model.SignUp;
import com.lulobank.clients.services.outboundadapters.model.AttemptEntity;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.outboundadapters.model.LoginAttemptsEntity;
import com.lulobank.clients.services.outboundadapters.model.OnBoardingStatus;
import com.lulobank.clients.services.utils.DatesUtil;
import com.lulobank.clients.services.utils.ProductTypeEnum;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class LoginTest extends AbstractBaseIntegrationTest {
  private static final String TESTED_URL = "/login";
  private static final String PASSWORD = "123456";
  private static final String NEW_EMAIL = "newmail@mail.com";
  private static final String ID_CARD = "12345678";
  private static final String NAME = "usertest";
  private static final String LAST_NAME = "lastname_test";
  private static final int PHONE_PREFIX = 57;
  private static final String PHONE_NUMBER_2 = "3168906733";
  private static final String ADDRESS = "address_test";
  private SignUp signUp;
  private ClientEntity clientEntity;
  private ClientEntity clientEntityFound;
  private InitiateAuthResult initiateAuthResult;
  private AuthenticationResultType authenticationResultType;

  @Value("classpath:mocks/login/LoginInternalServerErrorResponse.json")
  private Resource responseInternalServerError;

  @Value("classpath:mocks/login/LoginNotFoundResponse.json")
  private Resource responseNotFound;

  @Value("classpath:mocks/login/LoginNotAuthorizedResponse.json")
  private Resource responseNotAuthorizedResponse;

  @Override
  protected void init() {
    signUp = new SignUp();
    signUp.setPassword(PASSWORD);
    signUp.setUsername(NEW_EMAIL);

    clientEntity = new ClientEntity();
    clientEntity.setIdClient(ID_CLIENT);
    clientEntity.setIdCard(ID_CARD);
    clientEntity.setIdCard(PASSWORD);
    clientEntity.setName(NAME);
    clientEntity.setLastName(LAST_NAME);
    clientEntity.setAddress(ADDRESS);
    clientEntity.setPhonePrefix(PHONE_PREFIX);
    clientEntity.setPhoneNumber(PHONE_NUMBER_2);
    clientEntity.setEmailAddress(NEW_EMAIL);
    clientEntity.setEmailVerified(Boolean.TRUE);
    clientEntity.setIdCard(ID_CARD);
    clientEntity.setBlackListState(StateBlackList.NON_BLACKLISTED.name());
    OnBoardingStatus onBoardingStatus = new OnBoardingStatus();
    onBoardingStatus.setCheckpoint(CheckPoints.ON_BOARDING.name());
    onBoardingStatus.setProductSelected(ProductTypeEnum.CREDIT_ACCOUNT.name());
    clientEntity.setOnBoardingStatus(onBoardingStatus);

    initiateAuthResult = new InitiateAuthResult();
    authenticationResultType = new AuthenticationResultType();
    authenticationResultType.setIdToken("");
    authenticationResultType.setRefreshToken("");
    initiateAuthResult.setAuthenticationResult(authenticationResultType);

    clientEntityFound = new ClientEntity();
    clientEntityFound.setIdClient(ID_CLIENT);
    clientEntityFound.setDateOfIssue(LocalDate.now());
    clientEntityFound.setIdCard(ID_CARD);
  }

  @Test
  public void shouldReturnINTERNALSERVERERRORSinceClientLoginIsCOGNITONOTAUTHORIZED()
      throws Exception {

    AttemptEntity failedAttempt = new AttemptEntity();
    failedAttempt.setPenalty(0d);
    failedAttempt.setAttemptDate(DatesUtil.getLocalDateGMT5());
    failedAttempt.setAttemptDate(failedAttempt.getAttemptDate().minus(1L, ChronoUnit.MINUTES));
    List<AttemptEntity> failedAttempts = new ArrayList<>();
    failedAttempts.add(failedAttempt);
    LoginAttemptsEntity loginAttempts = new LoginAttemptsEntity();
    loginAttempts.setIdClient(UUID.fromString("6c64b082-d327-4d45-8cb6-371879cd7497"));
    loginAttempts.setFailsAttempt(failedAttempts);
    Optional<LoginAttemptsEntity> loginAttemptsOpt = Optional.of(loginAttempts);

    when(cognitoProperties.getClientapp_id()).thenReturn("fakeMyPoolId");
    when(cognitoProperties.getAwsCognitoIdentityProvider()).thenReturn(awsCognitoIdentityProvider);
    when(awsCognitoIdentityProvider.initiateAuth(any(InitiateAuthRequest.class)))
        .thenThrow(new NotAuthorizedException(""));
    when(clientsRepository.findByIdClientAndIdCard(any(String.class), anyString()))
        .thenReturn(Optional.of(clientEntity));
    when(clientsRepository.findByEmailAddress(any(String.class))).thenReturn(clientEntity);
    when(loginAttemptsRepository.findByIdClient(any(UUID.class))).thenReturn(loginAttemptsOpt);
    when(loginAttemptsService.isBlockedLogin(any(String.class))).thenReturn(true);
    when(loginAttemptsService.getLastDateFailedAttempt(any(String.class)))
        .thenReturn(new AttemptEntity());
    when(loginAttemptsService.getAttemptTimeFromAttemptEntity(any(AttemptEntity.class)))
        .thenReturn(new AttemptTimeResult());
    when(loginAttemptsService.getAttemptTimeResult(any(AttemptTimeResult.class))).thenReturn("");

    mockMvc
        .perform(
            MockMvcRequestBuilders.post(TESTED_URL)
                .contentType(CONTENT_TYPE_JSON)
                .with(bearerToken())
                .content(objectMapper.writeValueAsString(signUp)))
        .andExpect(status().isInternalServerError())
        .andExpect(
            content()
                .json(
                    FileUtils.readFileToString(
                        responseInternalServerError.getFile(), StandardCharsets.UTF_8)));
  }

  @Test
  public void shouldReturnOKSinceClientLoginSuccess() throws Exception {
    when(clientsRepository.findByEmailAddress(any(String.class))).thenReturn(clientEntity);
    when(cognitoProperties.getClientapp_id()).thenReturn("fakeMyPoolId");
    when(cognitoProperties.getAwsCognitoIdentityProvider()).thenReturn(awsCognitoIdentityProvider);
    when(awsCognitoIdentityProvider.initiateAuth(any(InitiateAuthRequest.class)))
        .thenReturn(initiateAuthResult);
    when(clientsRepository.findByIdClientAndIdCard(any(String.class), anyString()))
        .thenReturn(Optional.of(clientEntityFound));
    when(loginAttemptsService.getLastSuccessfulLoginAttemptDate(any(String.class)))
        .thenReturn(DatesUtil.getLocalDateGMT5().withNano(0).toString());

    mockMvc
        .perform(
            MockMvcRequestBuilders.post(TESTED_URL)
                .contentType(CONTENT_TYPE_JSON)
                .with(bearerToken())
                .content(objectMapper.writeValueAsString(signUp)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("content.idClient", is(ID_CLIENT)))
        .andExpect(jsonPath("content.email", is(NEW_EMAIL)))
        .andExpect(jsonPath("content.emailVerified", is(true)))
        .andExpect(jsonPath("content.name", is(NAME)))
        .andExpect(jsonPath("content.lastName", is(LAST_NAME)))
        .andExpect(jsonPath("content.phoneNumber", is(PHONE_NUMBER_2)))
        .andExpect(jsonPath("content.checkpoint", is(CheckPoints.ON_BOARDING.name())))
        .andExpect(jsonPath("content.idCard", is(ID_CARD)))
        .andExpect(jsonPath("content.productSelected", is(ProductTypeEnum.CREDIT_ACCOUNT.name())));
  }

  @Test
  public void shouldReturnOKSinceClientLoginSuccessWithoutOnboarding() throws Exception {
    clientEntity.setOnBoardingStatus(null);
    when(clientsRepository.findByEmailAddress(any(String.class))).thenReturn(clientEntity);
    when(cognitoProperties.getClientapp_id()).thenReturn("fakeMyPoolId");
    when(cognitoProperties.getAwsCognitoIdentityProvider()).thenReturn(awsCognitoIdentityProvider);
    when(awsCognitoIdentityProvider.initiateAuth(any(InitiateAuthRequest.class)))
        .thenReturn(initiateAuthResult);
    when(clientsRepository.findByIdClientAndIdCard(any(String.class), anyString()))
        .thenReturn(Optional.of(clientEntityFound));
    when(loginAttemptsService.getLastSuccessfulLoginAttemptDate(any(String.class)))
        .thenReturn(DatesUtil.getLocalDateGMT5().withNano(0).toString());

    mockMvc
        .perform(
            MockMvcRequestBuilders.post(TESTED_URL)
                .contentType(CONTENT_TYPE_JSON)
                .content(objectMapper.writeValueAsString(signUp)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("content.idClient", is(ID_CLIENT)))
        .andExpect(jsonPath("content.email", is(NEW_EMAIL)))
        .andExpect(jsonPath("content.emailVerified", is(true)))
        .andExpect(jsonPath("content.name", is(NAME)))
        .andExpect(jsonPath("content.lastName", is(LAST_NAME)))
        .andExpect(jsonPath("content.phoneNumber", is(PHONE_NUMBER_2)))
        .andExpect(jsonPath("content.checkpoint", is(CheckPoints.NONE.name())));
  }

  @Test
  public void shouldReturnINTERNALSERVERERRORSinceClientLoginNOTFOUND() throws Exception {
    when(cognitoProperties.getClientapp_id()).thenReturn("fakeMyPoolId");
    when(cognitoProperties.getAwsCognitoIdentityProvider()).thenReturn(awsCognitoIdentityProvider);
    when(awsCognitoIdentityProvider.initiateAuth(any(InitiateAuthRequest.class)))
        .thenThrow(new NotAuthorizedException(""));
    when(clientsRepository.findByIdClientAndIdCard(any(String.class), anyString()))
        .thenReturn(Optional.of(clientEntityFound));

    mockMvc
        .perform(
            MockMvcRequestBuilders.post(TESTED_URL)
                .contentType(CONTENT_TYPE_JSON)
                .with(bearerToken())
                .content(objectMapper.writeValueAsString(signUp)))
        .andExpect(status().isUnauthorized())
        .andExpect(
            content()
                .json(
                    FileUtils.readFileToString(
                        responseNotFound.getFile(), StandardCharsets.UTF_8)));
  }

  @Test
  public void shouldReturnINTERNALSERVERERRORSinceClientLoginIsUNAUTHORIZED() throws Exception {

    AttemptEntity failedAttempt = new AttemptEntity();
    failedAttempt.setPenalty(10d);
    failedAttempt.setAttemptDate(DatesUtil.getLocalDateGMT5());
    failedAttempt.setAttemptDate(failedAttempt.getAttemptDate().minus(1L, ChronoUnit.MINUTES));
    List<AttemptEntity> failedAttempts = new ArrayList<>();
    failedAttempts.add(failedAttempt);
    LoginAttemptsEntity loginAttempts = new LoginAttemptsEntity();
    loginAttempts.setIdClient(UUID.fromString("6c64b082-d327-4d45-8cb6-371879cd7497"));
    loginAttempts.setFailsAttempt(failedAttempts);
    Optional<LoginAttemptsEntity> loginAttemptsOpt = Optional.of(loginAttempts);
    when(cognitoProperties.getClientapp_id()).thenReturn("fakeMyPoolId");
    when(cognitoProperties.getAwsCognitoIdentityProvider()).thenReturn(awsCognitoIdentityProvider);
    when(awsCognitoIdentityProvider.initiateAuth(any(InitiateAuthRequest.class)))
        .thenThrow(new NotAuthorizedException(""));
    when(clientsRepository.findByIdClientAndIdCard(any(String.class), anyString()))
        .thenReturn(Optional.of(clientEntity));
    when(clientsRepository.findByEmailAddress(any(String.class))).thenReturn(clientEntity);
    when(loginAttemptsRepository.findByIdClient(any(UUID.class))).thenReturn(loginAttemptsOpt);
    when(loginAttemptsService.saveLoginAttempt(anyString(), any(Boolean.class)))
        .thenReturn(failedAttempt);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post(TESTED_URL)
                .contentType(CONTENT_TYPE_JSON)
                .with(bearerToken())
                .content(objectMapper.writeValueAsString(signUp)))
        .andExpect(status().isUnauthorized())
        .andExpect(
            content()
                .json(
                    FileUtils.readFileToString(
                        responseNotAuthorizedResponse.getFile(), StandardCharsets.UTF_8)));
  }

  @Test
  public void shouldReturnForbiddenSinceClientLoginBlocked() throws Exception {

    AttemptEntity failedAttempt = new AttemptEntity();
    failedAttempt.setPenalty((double) -1);
    failedAttempt.setAttemptDate(DatesUtil.getLocalDateGMT5());
    failedAttempt.setAttemptDate(failedAttempt.getAttemptDate().minus(1L, ChronoUnit.MINUTES));
    List<AttemptEntity> failedAttempts = new ArrayList<>();
    failedAttempts.add(failedAttempt);
    LoginAttemptsEntity loginAttempts = new LoginAttemptsEntity();
    loginAttempts.setIdClient(UUID.fromString("6c64b082-d327-4d45-8cb6-371879cd7497"));
    loginAttempts.setFailsAttempt(failedAttempts);
    Optional<LoginAttemptsEntity> loginAttemptsOpt = Optional.of(loginAttempts);
    when(cognitoProperties.getClientapp_id()).thenReturn("fakeMyPoolId");
    when(cognitoProperties.getAwsCognitoIdentityProvider()).thenReturn(awsCognitoIdentityProvider);
    when(awsCognitoIdentityProvider.initiateAuth(any(InitiateAuthRequest.class)))
        .thenThrow(new NotAuthorizedException(""));
    when(clientsRepository.findByIdClientAndIdCard(any(String.class), anyString()))
        .thenReturn(Optional.of(clientEntity));
    when(clientsRepository.findByEmailAddress(any(String.class))).thenReturn(clientEntity);
    when(loginAttemptsRepository.findByIdClient(any(UUID.class))).thenReturn(loginAttemptsOpt);
    when(loginAttemptsService.saveLoginAttempt(anyString(), any(Boolean.class)))
        .thenReturn(failedAttempt);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post(TESTED_URL)
                .contentType(CONTENT_TYPE_JSON)
                .with(bearerToken())
                .content(objectMapper.writeValueAsString(signUp)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void shouldReturnNOTFOUNDSinceClientLoginIsNotFound() throws Exception {
    when(cognitoProperties.getClientapp_id()).thenReturn("fakeMyPoolId");
    when(cognitoProperties.getAwsCognitoIdentityProvider()).thenReturn(awsCognitoIdentityProvider);
    when(awsCognitoIdentityProvider.initiateAuth(any(InitiateAuthRequest.class)))
        .thenThrow(new UserNotFoundException(""));
    when(clientsRepository.findByIdClientAndIdCard(any(String.class), anyString()))
        .thenReturn(Optional.of(clientEntityFound));

    mockMvc
        .perform(
            MockMvcRequestBuilders.post(TESTED_URL)
                .contentType(CONTENT_TYPE_JSON)
                .with(bearerToken())
                .content(objectMapper.writeValueAsString(signUp)))
        .andExpect(status().isUnauthorized())
        .andExpect(
            content()
                .json(
                    FileUtils.readFileToString(
                        responseNotFound.getFile(), StandardCharsets.UTF_8)));
  }

  @Test
  public void shouldReturnINTERNALSERVERERRORSinceClientLogin() throws Exception {
    when(cognitoProperties.getClientapp_id()).thenReturn("fakeMyPoolId");
    when(cognitoProperties.getAwsCognitoIdentityProvider()).thenReturn(awsCognitoIdentityProvider);
    when(awsCognitoIdentityProvider.initiateAuth(any(InitiateAuthRequest.class)))
        .thenThrow(new NullPointerException(""));
    when(clientsRepository.findByIdClientAndIdCard(any(String.class), anyString()))
        .thenReturn(Optional.of(clientEntityFound));

    mockMvc
        .perform(
            MockMvcRequestBuilders.post(TESTED_URL)
                .contentType(CONTENT_TYPE_JSON)
                .with(bearerToken())
                .content(objectMapper.writeValueAsString(signUp)))
        .andExpect(status().isUnauthorized())
        .andExpect(
            content()
                .json(
                    FileUtils.readFileToString(
                        responseNotFound.getFile(), StandardCharsets.UTF_8)));
  }
}
