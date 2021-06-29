package com.lulobank.clients.services.inboundadapters;

import static com.lulobank.core.utils.ValidatorUtils.getHttpStatusByCode;

import com.lulobank.clients.sdk.operations.dto.ClientErrorResult;
import com.lulobank.clients.sdk.operations.dto.ClientResult;
import com.lulobank.clients.sdk.operations.dto.ClientSuccessResult;
import com.lulobank.clients.services.ILoginAttempts;
import com.lulobank.clients.services.features.changepassword.ChangePasswordHandler;
import com.lulobank.clients.services.features.changepassword.ValidateNewPasswordValidator;
import com.lulobank.clients.services.features.changepassword.ValidatePasswordHandler;
import com.lulobank.clients.services.features.changepassword.ValidatePasswordValidator;
import com.lulobank.clients.services.features.changepassword.actions.MessageToSQSNotificationPasswordUpdated;
import com.lulobank.clients.services.features.changepassword.actions.UpdatePasswordDynamoClientAction;
import com.lulobank.clients.services.features.changepassword.model.NewPasswordRequest;
import com.lulobank.clients.services.features.changepassword.model.Password;
import com.lulobank.clients.services.features.recoverpassword.ClientRecoverPasswordHandler;
import com.lulobank.clients.services.features.recoverpassword.ClientUpdatePasswordRecoverHandler;
import com.lulobank.clients.services.features.recoverpassword.action.RecoverPasswordRetrofitClientAction;
import com.lulobank.clients.services.features.recoverpassword.model.ClientWithIdCard;
import com.lulobank.clients.services.features.recoverpassword.model.RecoverPasswordEmailClient;
import com.lulobank.clients.services.features.recoverpassword.model.RecoverPasswordUpdate;
import com.lulobank.clients.services.features.recoverpassword.model.RecoverPasswordUpdated;
import com.lulobank.clients.services.features.recoverpassword.validators.ClientWithIdCardValidator;
import com.lulobank.clients.services.features.recoverpassword.validators.RecoverPasswordUpdateValidator;
import com.lulobank.clients.services.outboundadapters.ClientsOutboundAdapter;
import com.lulobank.clients.services.outboundadapters.SendMessageToSQS;
import com.lulobank.clients.services.outboundadapters.repository.ClientsRepository;
import com.lulobank.clients.services.utils.ClientErrorResultsEnum;
import com.lulobank.clients.services.utils.CognitoProperties;
import com.lulobank.clients.services.utils.LoginErrorResultsEnum;
import com.lulobank.core.Response;
import com.lulobank.core.actions.Action;
import com.lulobank.core.crosscuttingconcerns.PostActionsDecoratorHandler;
import com.lulobank.core.crosscuttingconcerns.ValidatorDecoratorHandler;
import com.lulobank.core.validations.Validator;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RefreshScope
@RestController
@RequestMapping("/")
@CrossOrigin(origins = "*")
public class PasswordAdapter {
  private ClientsRepository clientsRepository;
  private CognitoProperties cognitoProperties;
  private ILoginAttempts loginAttemptsService;
  private ClientsOutboundAdapter clientsOutboundAdapter;

  @Value("${cloud.aws.sqs.client-alerts-events}")
  private String sqsNotificationEndPoint;

  @Autowired
  public PasswordAdapter(
      ClientsRepository clientsRepository,
      CognitoProperties cognitoProperties,
      ILoginAttempts loginAttemptsService,
      ClientsOutboundAdapter clientsOutboundAdapter) {
    this.clientsRepository = clientsRepository;
    this.cognitoProperties = cognitoProperties;
    this.loginAttemptsService = loginAttemptsService;
    this.clientsOutboundAdapter = clientsOutboundAdapter;
  }

  @PostMapping(value = "/password/validate", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity validatePassword(
      @RequestHeader final HttpHeaders headers, @RequestBody final Password password) {
    return getResponseEntityValidatePassword(password);
  }

  @PostMapping(value = "/password/validateInternal", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity validatePasswordInternal(
      @RequestHeader final HttpHeaders headers, @RequestBody final Password password) {
    return getResponseEntityValidatePassword(password);
  }

  @NotNull
  private ResponseEntity getResponseEntityValidatePassword(@RequestBody Password password) {
    List<Validator<Password>> validators = new ArrayList<>();
    validators.add(new ValidatePasswordValidator(clientsRepository, loginAttemptsService));
    Response<Password> response =
        new ValidatorDecoratorHandler<>(new ValidatePasswordHandler(), validators).handle(password);
    if (Boolean.FALSE.equals(response.getHasErrors())) {
      return ResponseEntity.ok().build();
    }
    if (response.getErrors().stream()
        .anyMatch(
            x -> LoginErrorResultsEnum.COGNITO_NOT_AUTHORIZED.name().equals(x.getFailure()))) {
      return new ResponseEntity<>(new Response<>(response.getErrors()), HttpStatus.UNAUTHORIZED);
    }
    if (response.getErrors().stream()
        .anyMatch(x -> LoginErrorResultsEnum.USER_BLOCKED.name().equals(x.getFailure()))) {
      return new ResponseEntity<>(new Response<>(response.getErrors()), HttpStatus.FORBIDDEN);
    }
    if (response.getErrors().stream()
        .anyMatch(
            x -> ClientErrorResultsEnum.INTERNAL_SERVER_ERROR.name().equals(x.getFailure()))) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
  }

  @PostMapping(value = "/password/update", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity recoverUpdatePassword(
      @RequestHeader final HttpHeaders headers,
      @RequestBody final NewPasswordRequest newPasswordRequest) {
    newPasswordRequest.setHttpHeaders(headers.toSingleValueMap());
    List<Validator<NewPasswordRequest>> validators = new ArrayList<>();
    validators.add(new ValidateNewPasswordValidator(clientsRepository));
    List<Action<Response<Password>, NewPasswordRequest>> actions = new ArrayList<>();
    actions.add(new UpdatePasswordDynamoClientAction(clientsRepository));
    actions.add(
        (SendMessageToSQS)
            new MessageToSQSNotificationPasswordUpdated(
                clientsOutboundAdapter.getQueueMessagingTemplate(), sqsNotificationEndPoint));
    Response<Password> response =
        new ValidatorDecoratorHandler<>(
                new PostActionsDecoratorHandler<>(
                    new ChangePasswordHandler(cognitoProperties, clientsRepository), actions),
                validators)
            .handle(newPasswordRequest);
    if (Boolean.TRUE.equals(response.getHasErrors())) {
      return new ResponseEntity<>(
          new ClientErrorResult(response.getErrors()),
          getHttpStatusByCode(response.getErrors().get(0).getValue()));
    }
    return ResponseEntity.ok().build();
  }

  @PostMapping(value = "/recoverPassword/sendEmail")
  public ResponseEntity<ClientResult> recoverPassword(
      @RequestHeader final HttpHeaders headers,
      @RequestBody final ClientWithIdCard clientWithIdCard) {

    clientWithIdCard.setHttpHeaders(headers.toSingleValueMap());
    List<Validator<ClientWithIdCard>> validators = new ArrayList<>();
    validators.add(new ClientWithIdCardValidator());

    List<Action<Response<RecoverPasswordEmailClient>, ClientWithIdCard>> actions =
        new ArrayList<>();
    actions.add(new RecoverPasswordRetrofitClientAction(clientsOutboundAdapter));

    Response<RecoverPasswordEmailClient> response =
        new ValidatorDecoratorHandler<>(
                new PostActionsDecoratorHandler(
                    new ClientRecoverPasswordHandler(clientsRepository), actions),
                validators)
            .handle(clientWithIdCard);
    if (Boolean.FALSE.equals(response.getHasErrors()))
      return new ResponseEntity<>(new ClientSuccessResult<>(response.getContent()), HttpStatus.OK);
    return new ResponseEntity<>(
        new ClientErrorResult(response.getErrors()),
        getHttpStatusByCode(response.getErrors().get(0).getValue()));
  }

  @PostMapping(value = "/recoverPassword/updatePassword")
  public ResponseEntity<ClientResult> recoverUpdatePassword(
      @RequestHeader final HttpHeaders headers,
      @RequestBody final RecoverPasswordUpdate recoverPasswordUpdate) {
    recoverPasswordUpdate.setHttpHeaders(headers.toSingleValueMap());
    List<Validator<RecoverPasswordUpdate>> validators = new ArrayList<>();
    validators.add(new RecoverPasswordUpdateValidator());
    Response<RecoverPasswordUpdated> response =
        new ValidatorDecoratorHandler<>(
                new ClientUpdatePasswordRecoverHandler(clientsOutboundAdapter), validators)
            .handle(recoverPasswordUpdate);
    if (Boolean.FALSE.equals(response.getHasErrors())
        && Boolean.TRUE.equals(response.getContent().getSuccessUpdated()))
      return new ResponseEntity<>(HttpStatus.OK);
    return new ResponseEntity<>(
        new ClientErrorResult(response.getErrors()),
        getHttpStatusByCode(response.getErrors().get(0).getValue()));
  }
}
