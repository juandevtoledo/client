package com.lulobank.clients.services.features.login;

import static com.lulobank.clients.services.utils.CheckPointsHelper.getCheckPointFromClientEntity;
import static com.lulobank.clients.services.utils.CheckPointsHelper.isNoneCheckPoint;
import static com.lulobank.clients.services.utils.LogMessages.USER_NOT_AUTORIZED;

import com.amazonaws.services.cognitoidp.model.AuthFlowType;
import com.amazonaws.services.cognitoidp.model.InitiateAuthRequest;
import com.amazonaws.services.cognitoidp.model.InitiateAuthResult;
import com.amazonaws.services.cognitoidp.model.NotAuthorizedException;
import com.lulobank.clients.sdk.operations.util.CheckPoints;
import com.lulobank.clients.services.ILoginAttempts;
import com.lulobank.clients.services.features.login.model.AttemptTimeResult;
import com.lulobank.clients.services.features.login.model.SignUp;
import com.lulobank.clients.services.features.login.model.SignUpResult;
import com.lulobank.clients.services.features.login.model.TokensSignUp;
import com.lulobank.clients.services.outboundadapters.model.AttemptEntity;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.outboundadapters.repository.ClientsRepository;
import com.lulobank.clients.services.utils.CognitoProperties;
import com.lulobank.clients.services.utils.LogMessages;
import com.lulobank.clients.services.utils.LoginErrorResultsEnum;
import com.lulobank.core.Handler;
import com.lulobank.core.Response;
import com.lulobank.core.validations.ValidationResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.owasp.encoder.Encode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

public class ClientLoginHandler implements Handler<Response<SignUpResult>, SignUp> {

  private static final Logger logger = LoggerFactory.getLogger(ClientLoginHandler.class);
  public static final String USERNAME = "USERNAME";
  public static final String PASSWORD = "PASSWORD";

  private CognitoProperties cognitoProperties;

  private ClientsRepository clientsRepository;

  private ILoginAttempts loginAttemptsService;

  public ClientLoginHandler(
      CognitoProperties cognitoProperties,
      ClientsRepository clientsRepository,
      ILoginAttempts loginAttemptsService) {
    this.cognitoProperties = cognitoProperties;
    this.clientsRepository = clientsRepository;
    this.loginAttemptsService = loginAttemptsService;
  }

  @Override
  public Response<SignUpResult> handle(SignUp signUp) {
    InitiateAuthRequest initiateAuthRequest =
        initiateUserWithUserPasswordAuthRequest(signUp.getUsername(), signUp.getPassword());
    ClientEntity clientEntity = getInfoUserlogin(signUp);
    Response response = null;
    if (Optional.ofNullable(clientEntity).isPresent()) {
      try {
        if (loginAttemptsService.isBlockedLogin(clientEntity.getIdClient())) {
          response = getResponseForUserBlocked(clientEntity);
        } else {
          response = getResponseForUserOk(initiateAuthRequest, clientEntity);
        }
      } catch (NotAuthorizedException e) {
        response = getResponseForNotAuthorized(clientEntity, e);
      }
    } else {
      logger.error(LogMessages.CLIENT_NOT_FOUND_IN_DB_EXCEPTION.name(), Encode.forJava(signUp.getUsername()));
      response =
          new Response<>(
              getValidationError(
                  new ValidationResult(
                      LoginErrorResultsEnum.COGNITO_NOT_FOUND.name(), signUp.getUsername())));
    }
    return response;
  }

  @NotNull
  private Response getResponseForNotAuthorized(
      ClientEntity clientEntity, NotAuthorizedException e) {
    Response response;
    logger.error(USER_NOT_AUTORIZED.name(), e.getMessage(), e);
    AttemptEntity attemptEntity =
        loginAttemptsService.saveLoginAttempt(clientEntity.getIdClient(), false);
    AttemptTimeResult attemptTimeResult =
        loginAttemptsService.getAttemptTimeFromAttemptEntity(attemptEntity);
    String attemptTimeResultJson = loginAttemptsService.getAttemptTimeResult(attemptTimeResult);
    if (attemptEntity.getPenalty() == -1) {
      response =
          new Response<>(
              getValidationError(
                  new ValidationResult(
                      LoginErrorResultsEnum.USER_BLOCKED.name(),
                      String.valueOf(HttpStatus.FORBIDDEN.value()))));
    } else {
      response =
          new Response<>(
              getValidationError(
                  new ValidationResult(
                      LoginErrorResultsEnum.COGNITO_NOT_AUTHORIZED.name(), attemptTimeResultJson)));
    }
    return response;
  }

  @NotNull
  private Response getResponseForUserOk(
      InitiateAuthRequest initiateAuthRequest, ClientEntity clientEntity) {
    String lastSuccesfulLoginAttemptDate;
    Response response;
    InitiateAuthResult initiateAuthResult =
        cognitoProperties.getAwsCognitoIdentityProvider().initiateAuth(initiateAuthRequest);
    lastSuccesfulLoginAttemptDate =
        loginAttemptsService.getLastSuccessfulLoginAttemptDate(clientEntity.getIdClient());
    loginAttemptsService.saveLoginAttempt(clientEntity.getIdClient(), true);

    SignUpResult signUpResult =
        new SignUpResult(
            new TokensSignUp(
                initiateAuthResult.getAuthenticationResult().getIdToken(),
                initiateAuthResult.getAuthenticationResult().getRefreshToken(),
                initiateAuthResult.getAuthenticationResult().getAccessToken()));
    signUpResult.setPreviousSuccessfulLogin(lastSuccesfulLoginAttemptDate);
    setClientInfo(signUpResult, clientEntity);
    response = new Response<>(signUpResult);
    return response;
  }

  @NotNull
  private Response getResponseForUserBlocked(ClientEntity clientEntity) {
    AttemptEntity attemptEntity;
    Response response;
    AttemptTimeResult attemptTimeResult;
    String attemptTimeResultJson;
    attemptEntity = loginAttemptsService.getLastDateFailedAttempt(clientEntity.getIdClient());

    if (attemptEntity.getPenalty() == -1) {
      response =
          new Response<>(
              getValidationError(
                  new ValidationResult(
                      LoginErrorResultsEnum.USER_BLOCKED.name(),
                      String.valueOf(HttpStatus.FORBIDDEN.value()))));
    } else {
      attemptTimeResult = loginAttemptsService.getAttemptTimeFromAttemptEntity(attemptEntity);
      attemptTimeResultJson = loginAttemptsService.getAttemptTimeResult(attemptTimeResult);

      response =
          new Response<>(
              getValidationError(
                  new ValidationResult(
                      LoginErrorResultsEnum.COGNITO_NOT_AUTHORIZED.name(), attemptTimeResultJson)));
    }
    return response;
  }

  private InitiateAuthRequest initiateUserWithUserPasswordAuthRequest(
      String username, String password) {

    InitiateAuthRequest initiateAuthRequest = new InitiateAuthRequest();
    initiateAuthRequest.setAuthFlow(AuthFlowType.USER_PASSWORD_AUTH);
    initiateAuthRequest.setClientId(cognitoProperties.getClientapp_id());
    initiateAuthRequest.addAuthParametersEntry(USERNAME, username);
    initiateAuthRequest.addAuthParametersEntry(PASSWORD, password);
    return initiateAuthRequest;
  }

  private void setClientInfo(SignUpResult signUpResult, ClientEntity clientEntity) {
    signUpResult.setIdClient(clientEntity.getIdClient());
    signUpResult.setEmail(clientEntity.getEmailAddress());
    signUpResult.setName(StringUtils.defaultString(clientEntity.getName()));
    signUpResult.setLastName(StringUtils.defaultString(clientEntity.getLastName()));
    signUpResult.setIdCard(StringUtils.defaultString(clientEntity.getIdCard()));
    signUpResult.setPhoneNumber(clientEntity.getPhoneNumber());
    signUpResult.setEmailVerified(clientEntity.getEmailVerified());
    CheckPoints checkPoints = getCheckPointFromClientEntity.apply(clientEntity);
    setOnboardingInformation(checkPoints, clientEntity, signUpResult);
  }

  private List<ValidationResult> getValidationError(ValidationResult e) {
    List<ValidationResult> errors = new ArrayList<>();
    errors.add(e);
    return errors;
  }

  public ClientEntity getInfoUserlogin(SignUp signUp) {
    return clientsRepository.findByEmailAddress(signUp.getUsername());
  }

  public void setOnboardingInformation(
      CheckPoints checkPoints, ClientEntity clientEntity, SignUpResult signUpResult) {
    signUpResult.setCheckpoint(checkPoints.name());
    if (isNoneCheckPoint.negate().test(checkPoints)) {
      signUpResult.setProductSelected(clientEntity.getOnBoardingStatus().getProductSelected());
    }
  }
}
