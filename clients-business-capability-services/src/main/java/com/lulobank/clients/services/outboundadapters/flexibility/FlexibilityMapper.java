package com.lulobank.clients.services.outboundadapters.flexibility;

import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import flexibility.client.models.Gender;
import flexibility.client.models.request.CreateClientRequest;
import flexibility.client.util.CreateClientRequestBuilder;

public class FlexibilityMapper {
  private FlexibilityMapper() {
    throw new IllegalStateException();
  }

  public static CreateClientRequest createClientRequestFromClientEntity(ClientEntity clientEntity) {
    return CreateClientRequestBuilder.createClientRequestBuilder()
        .withEmail(clientEntity.getEmailAddress())
        .withFirstName(clientEntity.getName())
        .withGender(Gender.valueOf(clientEntity.getGender()))
        .withLastName(clientEntity.getLastName())
        .withMobileNumber(clientEntity.getPhoneNumber())
        .withPhoneNumber(clientEntity.getPhoneNumber())
        .setDocumentIssuedAt(clientEntity.getDateOfIssue())
        .build();
  }
}
