package com.lulobank.clients.services.actions;

import com.amazonaws.services.cognitoidp.model.AdminConfirmSignUpRequest;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.SignUpRequest;
import com.amazonaws.services.cognitoidp.model.SignUpResult;
import com.lulobank.clients.services.features.onboardingclients.model.CreateClientRequest;
import com.lulobank.clients.services.features.onboardingclients.model.CreateClientResponse;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.outboundadapters.repository.ClientsRepository;
import com.lulobank.clients.services.utils.CognitoProperties;
import com.lulobank.core.Response;
import com.lulobank.core.actions.Action;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateCognitoClientAction
    implements Action<Response<CreateClientResponse>, CreateClientRequest> {
  private CognitoProperties cognitoProperties;
  private ClientsRepository clientsRepository;
  private Logger logger = LoggerFactory.getLogger(CreateCognitoClientAction.class);

  public CreateCognitoClientAction(
      CognitoProperties cognitoProperties, ClientsRepository clientsRepository) {
    this.cognitoProperties = cognitoProperties;
    this.clientsRepository = clientsRepository;
  }

  @Override
  public void run(Response<CreateClientResponse> response, CreateClientRequest command) {
    try {
      String idCognito = createCognitoUser(response, command);
      confirmUserCognito(command.getEmail().getAddress());
      ClientEntity clientEntity =
          clientsRepository.findByIdClient(response.getContent().getUserId()).orElse(null);

      Optional<ClientEntity> optional = Optional.ofNullable(clientEntity);
      optional.ifPresent(
          objClientEntity -> {
            objClientEntity.setIdCognito(idCognito);
            clientsRepository.save(objClientEntity);
          });
    } catch (Exception e) {
      logger.error("Error creating cognito user {} {}", e.getMessage(), e);
    }
  }

  private void confirmUserCognito(String newEmail) {

    AdminConfirmSignUpRequest adminConfirmSignUpRequest = new AdminConfirmSignUpRequest();
    adminConfirmSignUpRequest.setUsername(newEmail);
    adminConfirmSignUpRequest.setUserPoolId(cognitoProperties.getPool_id());
    cognitoProperties.getAwsCognitoIdentityProvider().adminConfirmSignUp(adminConfirmSignUpRequest);
  }

  private String createCognitoUser(
      Response<CreateClientResponse> response, CreateClientRequest command) {
    // TODO: Deuda tecnica: Tenemos que diseñar esto de forma que avise al Front si fallo la
    // creación de la cuenta en Cognito y/o hacer 3 reintentos y avisar a operaciones:
    SignUpRequest signUpRequest = new SignUpRequest();
    signUpRequest.setClientId(cognitoProperties.getClientapp_id());
    signUpRequest.setUsername(command.getEmail().getAddress());
    signUpRequest.setPassword(command.getPassword());
    List<AttributeType> list = new ArrayList<>();
    AttributeType attributeType = new AttributeType();
    attributeType.setName("phone_number");
    attributeType.setValue("+" + command.getPhone().getPrefix() + command.getPhone().getNumber());
    list.add(attributeType);
    AttributeType attributeType3 = new AttributeType();
    attributeType3.setName("custom:idClient");
    attributeType3.setValue(response.getContent().getUserId());
    list.add(attributeType3);
    signUpRequest.setUserAttributes(list);
    SignUpResult result = cognitoProperties.getAwsCognitoIdentityProvider().signUp(signUpRequest);
    return result.getUserSub();
  }
}
