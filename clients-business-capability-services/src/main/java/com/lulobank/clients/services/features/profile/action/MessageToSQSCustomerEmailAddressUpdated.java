package com.lulobank.clients.services.features.profile.action;

import com.lulobank.clients.services.features.profile.model.UpdateEmailClientRequest;
import com.lulobank.clients.services.features.profile.model.UpdateEmailClientResponse;
import com.lulobank.clients.services.outboundadapters.SendMessageToSQS;
import com.lulobank.core.Response;
import com.lulobank.core.events.Event;
import com.lulobank.core.utils.EventUtils;
import com.lulobank.customerservice.sdk.events.ZendeskUserUpdate;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;

public class MessageToSQSCustomerEmailAddressUpdated
    extends SendMessageToSQS<UpdateEmailClientRequest> {

  public MessageToSQSCustomerEmailAddressUpdated(
      QueueMessagingTemplate queueMessagingTemplate, String sqsEndPoint) {
    super(queueMessagingTemplate, sqsEndPoint);
  }

  @Override
  public Event<ZendeskUserUpdate> buildEvent(Response response, UpdateEmailClientRequest command) {
    return new EventUtils<ZendeskUserUpdate>()
        .getEvent(generateEvent((UpdateEmailClientResponse) response.getContent(), command));
  }

  private ZendeskUserUpdate generateEvent(
      UpdateEmailClientResponse response, UpdateEmailClientRequest request) {
    ZendeskUserUpdate zendeskUserUpdate = new ZendeskUserUpdate();
    zendeskUserUpdate.setIdCard(response.getIdCard());
    zendeskUserUpdate.setEmailAddress(request.getNewEmail());
    return zendeskUserUpdate;
  }
}
