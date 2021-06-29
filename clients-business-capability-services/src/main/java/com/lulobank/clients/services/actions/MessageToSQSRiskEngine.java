package com.lulobank.clients.services.actions;

import com.lulobank.clients.services.domain.DocumentType;
import com.lulobank.clients.services.events.IdentityInformation;
import com.lulobank.clients.services.features.riskengine.model.ClientCreated;
import com.lulobank.clients.services.features.riskengine.model.ClientWithIdCardInformation;
import com.lulobank.clients.services.outboundadapters.SendMessageToSQS;
import com.lulobank.core.Response;
import com.lulobank.core.events.Event;
import com.lulobank.core.utils.EventUtils;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;

public class MessageToSQSRiskEngine extends SendMessageToSQS<ClientWithIdCardInformation> {
  public MessageToSQSRiskEngine(QueueMessagingTemplate queueMessagingTemplate, String sqsEndPoint) {
    super(queueMessagingTemplate, sqsEndPoint);
  }

  @Override
  public Event buildEvent(
      Response response, ClientWithIdCardInformation clientWithIdCardInformation) {
    ClientCreated clientCreated = (ClientCreated) response.getContent();
    Event event =
        new EventUtils<IdentityInformation>()
            .getEvent(
                IdentityInformation.builder()
                    .documentNumber(clientWithIdCardInformation.getIdCard())
                    .expeditionDate(clientWithIdCardInformation.getDateOfIssue())
                    .documentType(DocumentType.CC.name())
                    .build());
    event.setId(clientCreated.getUserId());
    return event;
  }
}
