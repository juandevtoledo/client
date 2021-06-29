package com.lulobank.clients.services.utils;

import com.amazonaws.services.cognitoidp.model.AdminSetUserPasswordRequest;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.AuthFlowType;
import com.amazonaws.services.cognitoidp.model.InitiateAuthRequest;
import com.amazonaws.services.cognitoidp.model.SignUpRequest;
import com.amazonaws.services.cognitoidp.model.SignUpResult;
import com.lulobank.clients.services.features.recoverpassword.model.RecoverPasswordUpdate;
import com.lulobank.clients.services.outboundadapters.ClientsOutboundAdapter;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class CognitoHelper {

  public static final BiConsumer<ClientEntity, ClientsOutboundAdapter> createCognitoUser =
      (clientEntity, clientsOutboundAdapter) -> {
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setClientId(clientsOutboundAdapter.getCognitoProperties().getClientapp_id());
        signUpRequest.setUsername(clientEntity.getEmailAddress());
        signUpRequest.setPassword(clientEntity.getQualityCode());
        List<AttributeType> list = new ArrayList<>();
        AttributeType attributeType = new AttributeType();
        attributeType.setName("phone_number");
        attributeType.setValue("+" + clientEntity.getPhonePrefix() + clientEntity.getPhoneNumber());
        list.add(attributeType);
        AttributeType attributeType3 = new AttributeType();
        attributeType3.setName("custom:idClient");
        attributeType3.setValue(clientEntity.getIdClient());
        list.add(attributeType3);
        signUpRequest.setUserAttributes(list);
        SignUpResult result =
            clientsOutboundAdapter
                .getCognitoProperties()
                .getAwsCognitoIdentityProvider()
                .signUp(signUpRequest);
        clientEntity.setIdCognito(result.getUserSub());
      };

  public static final InitiateAuthRequest initiateUserWithUserPasswordAuthRequest(
      String username, String password, ClientsOutboundAdapter clientsOutboundAdapter) {

    InitiateAuthRequest initiateAuthRequest = new InitiateAuthRequest();
    initiateAuthRequest.setAuthFlow(AuthFlowType.USER_PASSWORD_AUTH);
    initiateAuthRequest.setClientId(
        clientsOutboundAdapter.getCognitoProperties().getClientapp_id());
    initiateAuthRequest.addAuthParametersEntry("USERNAME", username);
    initiateAuthRequest.addAuthParametersEntry("PASSWORD", password);
    return initiateAuthRequest;
  }

  public static final BiConsumer<ClientsOutboundAdapter, RecoverPasswordUpdate>
      recoverPasswordClient =
          (clientsOutboundAdapter, recoverPasswordUpdate) -> {
            AdminSetUserPasswordRequest adminSetUserPasswordRequest =
                new AdminSetUserPasswordRequest();
            adminSetUserPasswordRequest.setPassword(recoverPasswordUpdate.getNewPassword());
            adminSetUserPasswordRequest.setPermanent(true);
            adminSetUserPasswordRequest.setUsername(recoverPasswordUpdate.getEmailAddress());
            adminSetUserPasswordRequest.setUserPoolId(
                clientsOutboundAdapter.getCognitoProperties().getPool_id());
            clientsOutboundAdapter
                .getCognitoProperties()
                .getAwsCognitoIdentityProvider()
                .adminSetUserPassword(adminSetUserPasswordRequest);
          };
}
