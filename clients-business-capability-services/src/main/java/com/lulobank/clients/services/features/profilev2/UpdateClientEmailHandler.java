package com.lulobank.clients.services.features.profilev2;

import com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException;
import com.lulobank.clients.sdk.operations.dto.ClientResult;
import com.lulobank.clients.sdk.operations.dto.ClientsFailure;
import com.lulobank.clients.sdk.operations.dto.ClientsFailureResult;
import com.lulobank.clients.services.exception.ValidateRequestException;
import com.lulobank.clients.services.features.profile.action.MessageToSQSCustomerEmailAddressUpdated;
import com.lulobank.clients.services.features.profile.action.MessageToSQSNotificationEmailUpdated;
import com.lulobank.clients.services.features.profile.model.UpdateEmailClientResponse;
import com.lulobank.clients.services.features.profilev2.model.UpdateClientEmailRequest;
import com.lulobank.clients.services.features.profilev2.model.UpdateEmailNotification;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.outboundadapters.repository.ClientsRepository;
import com.lulobank.clients.services.ports.out.IdentityProviderService;
import com.lulobank.clients.services.ports.out.MessageService;
import com.lulobank.clients.services.utils.LogMessages;
import com.lulobank.core.Response;
import flexibility.client.connector.ProviderException;
import flexibility.client.models.request.GetClientRequest;
import flexibility.client.sdk.FlexibilitySdk;
import io.vavr.concurrent.Future;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.owasp.encoder.Encode;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.http.ResponseEntity;

import java.util.NoSuchElementException;

import static com.lulobank.clients.services.features.profilev2.model.UpdateEmailMapper.UPDATE_EMAIL_MAPPER;
import static com.lulobank.clients.services.utils.ClientErrorResultsEnum.NEW_EMAIL_ALREADY_EXIST;
import static com.lulobank.clients.services.utils.LogMessages.ACTION_ON_PROVIDER_FAILED;
import static com.lulobank.clients.services.utils.LogMessages.EMAIL_UPDATE_ON_FLEXIBILITY_FAILED;
import static com.lulobank.clients.services.utils.LogMessages.EMAIL_UPDATE_SUCCESSFUL;
import static com.lulobank.clients.services.utils.LogMessages.ERROR_NOTIFYING_EMAIL_UPDATE;
import static com.lulobank.clients.services.utils.LogMessages.ERROR_UPDATING_EMAIL;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
public class UpdateClientEmailHandler {
  private final MessageToSQSNotificationEmailUpdated sqsNotificationEmailUpdateSender;
  private final MessageToSQSCustomerEmailAddressUpdated sqsCustomerEmailAddressUpdateSender;
  private final ClientsRepository clientsRepository;
  private final IdentityProviderService identityProviderService;
  private final MessageService messageQueuesService;
  private final FlexibilitySdk flexibilitySdk;

  public UpdateClientEmailHandler(ClientsRepository clientsRepository, IdentityProviderService identityProviderService,
                                  QueueMessagingTemplate queueMessagingTemplate, String sqsNotificationEndPoint,
                                  String sqsCustomerEndPoint, MessageService messageQueuesService, FlexibilitySdk flexibilitySdk) {
    this.clientsRepository = clientsRepository;
    this.identityProviderService = identityProviderService;
    this.sqsNotificationEmailUpdateSender = new MessageToSQSNotificationEmailUpdated(queueMessagingTemplate, sqsNotificationEndPoint);
    this.sqsCustomerEmailAddressUpdateSender = new MessageToSQSCustomerEmailAddressUpdated(queueMessagingTemplate, sqsCustomerEndPoint);
    this.messageQueuesService = messageQueuesService;
    this.flexibilitySdk = flexibilitySdk;
  }

  public ResponseEntity<ClientResult> handle(final UpdateClientEmailRequest updateEmailRequest) {
    return Try.of(clientsRepository.findByIdClient(updateEmailRequest.getIdClient())::get)
        .andThenTry(clientEntity -> updateClientOnIdentityProvider(updateEmailRequest, clientEntity))
        .andThenTry(clientEntity -> updateClientOnMambu(updateEmailRequest, clientEntity))
        .map(clientEntity -> updateClientOnClientsTable(updateEmailRequest, clientEntity))
        .andThenTry(client -> sendUpdateEmailNotifications(updateEmailRequest, client))
        .map(this::okResponse)
        .transform(this::handleErrors)
        .get();
  }

  private void updateClientOnIdentityProvider(UpdateClientEmailRequest updateEmailRequest, ClientEntity clientEntity) {
    Option.of(clientsRepository.findByEmailAddress(updateEmailRequest.getNewEmail())).toTry()
        .peek(client -> {
          throw new ValidateRequestException(NEW_EMAIL_ALREADY_EXIST.name(), HttpStatus.SC_PRECONDITION_FAILED);
        })
        .recover(NoSuchElementException.class, clientEntity)
        .andThenTry(email -> identityProviderService.updateUserEmail(clientEntity.getIdKeycloak(), updateEmailRequest.getNewEmail()))
        .onFailure(e -> log.error(ACTION_ON_PROVIDER_FAILED.getMessage(), "Email update", Encode.forJava(clientEntity.getIdClient()), e.getMessage()))
        .get();
  }

  private void updateClientOnMambu(UpdateClientEmailRequest updateEmailRequest, ClientEntity clientEntity) {
    Try.of(GetClientRequest::new)
        .peek(getClientRequest -> getClientRequest.setClientId(clientEntity.getIdCbs()))
        .mapTry(flexibilitySdk::getClientById)
        .map(UPDATE_EMAIL_MAPPER::toFlexibilityRequest)
        .andThenTry(updateClientRequest -> updateClientRequest.setEmail(updateEmailRequest.getNewEmail()))
        .mapTry(flexibilitySdk::updateClient)
        .onFailure(ProviderException.class, e -> log.error(EMAIL_UPDATE_ON_FLEXIBILITY_FAILED.getMessage(), Encode.forJava(clientEntity.getIdClient()), e.getMessage()))
        .get();
  }

  private ClientEntity updateClientOnClientsTable(UpdateClientEmailRequest updateEmailRequest, ClientEntity client) {
    client.setEmailAddress(updateEmailRequest.getNewEmail());
    client.setEmailVerified(Boolean.FALSE);
    return clientsRepository.save(client);
  }

  private void sendUpdateEmailNotifications(UpdateClientEmailRequest updateEmailRequest, ClientEntity client) {
    Response<UpdateEmailClientResponse> sqsRequestPart = Option.of(UpdateEmailNotification.builder())
        .map(builder -> builder.idCard(client.getIdCard())
            .emailAddress(updateEmailRequest.getNewEmail()))
        .map(UpdateEmailNotification.UpdateEmailNotificationBuilder::build)
        .map(UPDATE_EMAIL_MAPPER::toSqsRequest)
        .map(Response::new)
        .get();

    Future.run(() -> {
      sqsNotificationEmailUpdateSender.run(sqsRequestPart, UPDATE_EMAIL_MAPPER.toSqsRequest(updateEmailRequest));
      sqsCustomerEmailAddressUpdateSender.run(sqsRequestPart, UPDATE_EMAIL_MAPPER.toSqsRequest(updateEmailRequest));
      messageQueuesService.sendUpdateEmailMessage(updateEmailRequest);
    }).onFailure(e -> log.error(ERROR_NOTIFYING_EMAIL_UPDATE.getMessage(), updateEmailRequest.getNewEmail(), Encode.forJava(client.getIdClient()), e.getMessage(), e));
  }

  private ResponseEntity<ClientResult> okResponse(ClientEntity client) {
    log.info(EMAIL_UPDATE_SUCCESSFUL.getMessage(), client.getIdClient(), client.getEmailAddress());
    return ResponseEntity.ok().build();
  }

  private Try<ResponseEntity<ClientResult>> handleErrors(Try<ResponseEntity<ClientResult>> responseEntities) {
    return responseEntities
        .onFailure(AmazonDynamoDBException.class, e -> log.error(LogMessages.DYNAMO_ERROR_EXCEPTION.getMessage(), e.getMessage()))
        .onFailure(e -> log.error(ERROR_UPDATING_EMAIL.getMessage(), e.getMessage(), e))
        .recover(ValidateRequestException.class, vre -> ResponseEntity
            .status(vre.getCode())
            .body(new ClientsFailureResult<>(new ClientsFailure(vre.getFailure()))))
        .recover(Exception.class, e -> ResponseEntity
            .status( HttpStatus.SC_INTERNAL_SERVER_ERROR)
            .body(new ClientsFailureResult<>(new ClientsFailure(INTERNAL_SERVER_ERROR.name()))));
  }
}