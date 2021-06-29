package com.lulobank.clients.services.features.profile;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.cognitoidp.model.AWSCognitoIdentityProviderException;
import com.amazonaws.services.cognitoidp.model.AdminDeleteUserRequest;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.SignUpRequest;
import com.amazonaws.services.cognitoidp.model.SignUpResult;
import com.amazonaws.services.cognitoidp.model.UsernameExistsException;
import com.lulobank.clients.sdk.operations.dto.ClientsFailureResult;
import com.lulobank.clients.sdk.operations.dto.ClientResult;
import com.lulobank.clients.sdk.operations.dto.ClientSuccessResult;
import com.lulobank.clients.services.features.profile.model.UpdateEmailClientRequest;
import com.lulobank.clients.services.features.profile.model.UpdateEmailClientResponse;
import com.lulobank.clients.services.outboundadapters.SendMessageToSQS;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.outboundadapters.repository.ClientsRepository;
import com.lulobank.clients.services.utils.ClientErrorResultsEnum;
import com.lulobank.clients.services.utils.CognitoErrorReultsEnum;
import com.lulobank.clients.services.utils.CognitoProperties;
import com.lulobank.clients.sdk.operations.dto.ClientsFailure;
import com.lulobank.clients.services.utils.LogMessages;
import com.lulobank.core.Response;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.apache.commons.lang3.LocaleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class UpdateClientEmailHandler {
  private static final Logger LOGGER = LoggerFactory.getLogger(UpdateClientEmailHandler.class);

  private final SendMessageToSQS<UpdateEmailClientRequest> sqsNotificationEmailUpdateSender;
  private final SendMessageToSQS<UpdateEmailClientRequest> sqsCustomerEmailAddressUpdateSender;
  private final ClientsRepository clientsRepository;
  private final CognitoProperties cognitoProperties;

  public UpdateClientEmailHandler(
      ClientsRepository clientsRepository,
      CognitoProperties cognitoProperties,
      SendMessageToSQS<UpdateEmailClientRequest> sqsNotificationEmailUpdateSender,
      SendMessageToSQS<UpdateEmailClientRequest> sqsCustomerEmailAddressUpdateSender) {
    this.clientsRepository = clientsRepository;
    this.cognitoProperties = cognitoProperties;
    this.sqsNotificationEmailUpdateSender = sqsNotificationEmailUpdateSender;
    this.sqsCustomerEmailAddressUpdateSender = sqsCustomerEmailAddressUpdateSender;
  }

  public ResponseEntity<ClientResult> handle(final UpdateEmailClientRequest updateEmailRequest) {
    return Option.ofOptional(clientsRepository.findByIdClient(updateEmailRequest.getIdClient()))
        .toTry()
        .map(clientEntity -> updateClientEmailInCognitoAndTable(updateEmailRequest, clientEntity))
        .map(updateEmailResponse -> sendResponseNotifications(updateEmailRequest, updateEmailResponse))
        .map(ResponseEntity::ok)
        .transform(this::handleExceptions)
        .get();
  }

  private UpdateEmailClientResponse updateClientEmailInCognitoAndTable(UpdateEmailClientRequest updateEmailRequest, ClientEntity clientEntity) {
    String newEmail = updateEmailRequest.getNewEmail().toLowerCase(LocaleUtils.toLocale("es_CO"));
    String newCognitoId = createNewEmailUserCognito(
        newEmail,
        clientEntity.getPhonePrefix() + clientEntity.getPhoneNumber(),
        updateEmailRequest.getIdClient(),
        updateEmailRequest.getPassword());
    deleteCognitoUser(updateEmailRequest.getOldEmail());
    clientEntity.setEmailAddress(newEmail);
    clientEntity.setEmailVerified(Boolean.FALSE);
    clientEntity.setIdCognito(newCognitoId);
    UpdateEmailClientResponse updateEmailClientResponse = new UpdateEmailClientResponse();
    updateEmailClientResponse.setIdCard(clientEntity.getIdCard());
    updateEmailClientResponse.setEmailAddress(newEmail);
    clientsRepository.save(clientEntity);
    return updateEmailClientResponse;
  }

  private String createNewEmailUserCognito(String email, String phoneNumber, String idClient, String password) {
    SignUpRequest signUpRequest = (new SignUpRequest())
        .withClientId(cognitoProperties.getClientapp_id())
        .withUsername(email)
        .withPassword(password);
    List<AttributeType> attributeTypeList = new ArrayList<>();
    AttributeType attributeType3 = new AttributeType();
    attributeType3.setName("custom:idClient");
    attributeType3.setValue(idClient);
    attributeTypeList.add(attributeType3);
    AttributeType attributeType = new AttributeType();
    attributeType.setName("phone_number");
    attributeType.setValue("+" + phoneNumber);
    attributeTypeList.add(attributeType);
    signUpRequest.setUserAttributes(attributeTypeList);
    SignUpResult result = cognitoProperties.getAwsCognitoIdentityProvider().signUp(signUpRequest);
    return result.getUserSub();
  }

  private ClientResult sendResponseNotifications(UpdateEmailClientRequest updateEmailRequest, UpdateEmailClientResponse updateEmailResponse) {
    Stream.of(this.sqsNotificationEmailUpdateSender, this.sqsCustomerEmailAddressUpdateSender)
        .forEach(sqs -> sqs.run(new Response<UpdateEmailClientResponse>(updateEmailResponse), updateEmailRequest));
    return new ClientSuccessResult<>(updateEmailResponse);
  }

  private Try<ResponseEntity<ClientResult>> handleExceptions(Try<ResponseEntity<ClientResult>> responseEntities) {
    return responseEntities
        .onFailure(UsernameExistsException.class, e -> LOGGER.error(LogMessages.COGNITO_ERROR_EXCEPTION.getMessage(), e.getMessage(), e))
        .recover(UsernameExistsException.class, errorResponse(ClientErrorResultsEnum.EMAIL_EXIST_IN_DB.name(), HttpStatus.PRECONDITION_FAILED.value()))
        .onFailure(AWSCognitoIdentityProviderException.class, e -> LOGGER.error(LogMessages.COGNITO_ERROR_EXCEPTION.getMessage(), e.getMessage(), e))
        .recover(AWSCognitoIdentityProviderException.class, errorResponse(CognitoErrorReultsEnum.COGNITO_ERROR.name(), HttpStatus.INTERNAL_SERVER_ERROR.value()))
        .onFailure(SdkClientException.class, e -> LOGGER.error(LogMessages.DYNAMO_ERROR_EXCEPTION.getMessage(), e.getMessage(), e))
        .recover(SdkClientException.class, errorResponse(ClientErrorResultsEnum.INTERNAL_SERVER_ERROR.name(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
  }

  private void deleteCognitoUser(String email) {
    AdminDeleteUserRequest adminDeleteUserRequest = (new AdminDeleteUserRequest())
        .withUserPoolId(cognitoProperties.getPool_id())
        .withUsername(email);
    cognitoProperties.getAwsCognitoIdentityProvider().adminDeleteUser(adminDeleteUserRequest);
  }

  public static ResponseEntity<ClientResult> errorResponse(String message, int code) {
    return ResponseEntity.status(code).body(new ClientsFailureResult<>(new ClientsFailure(message)));
  }

}

