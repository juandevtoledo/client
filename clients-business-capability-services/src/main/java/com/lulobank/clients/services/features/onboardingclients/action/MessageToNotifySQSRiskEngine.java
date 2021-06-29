package com.lulobank.clients.services.features.onboardingclients.action;

import static com.lulobank.clients.services.utils.ConverterObjectUtils.getIdentityInfoEvent;

import com.lulobank.clients.services.events.IdentityInformation;
import com.lulobank.clients.services.features.riskengine.model.ClientCreated;
import com.lulobank.clients.services.outboundadapters.SendMessageToSQS;
import com.lulobank.core.Response;
import com.lulobank.core.events.Event;
import com.lulobank.core.utils.EventUtils;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;

public class MessageToNotifySQSRiskEngine extends SendMessageToSQS<IdentityInformation> {
  public MessageToNotifySQSRiskEngine(
      QueueMessagingTemplate queueMessagingTemplate, String sqsEndPoint) {
    super(queueMessagingTemplate, sqsEndPoint);
  }

  @Override
  public Event buildEvent(Response response, IdentityInformation identityInformation) {
    ClientCreated clientCreated = (ClientCreated) response.getContent();
    Event event =
        new EventUtils<IdentityInformation>().getEvent(getIdentityInfoEvent(identityInformation));
    event.setId(clientCreated.getUserId());
    return event;
  }
}
