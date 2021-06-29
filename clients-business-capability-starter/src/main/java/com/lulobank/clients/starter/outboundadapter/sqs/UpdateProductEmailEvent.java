package com.lulobank.clients.starter.outboundadapter.sqs;

import com.lulobank.clients.services.events.EventMapperV2;
import com.lulobank.clients.services.events.EventV2;
import com.lulobank.clients.services.features.profilev2.model.UpdateClientEmailRequest;
import com.lulobank.clients.services.features.profilev2.model.UpdateProductEmailMessage;

public class UpdateProductEmailEvent extends SqsIntegration<UpdateClientEmailRequest, UpdateProductEmailMessage> {

  public UpdateProductEmailEvent(String endpoint) {
    super(endpoint);
  }

  @Override
  public EventV2<UpdateProductEmailMessage> map(UpdateClientEmailRequest event) {
    return EventMapperV2.of(new UpdateProductEmailMessage()
        .setNewEmail(event.getNewEmail())
        .setIdClient(event.getIdClient()));
  }
}
