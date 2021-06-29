package com.lulobank.clients.services.features.reporting.action;

import com.lulobank.clients.services.events.NewReportEvent;
import com.lulobank.clients.services.features.reporting.model.GenerateClientReportStatement;
import com.lulobank.clients.services.outboundadapters.SendMessageToSQS;
import com.lulobank.core.Response;
import com.lulobank.core.events.Event;
import com.lulobank.core.utils.EventUtils;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;

public class SendMessageToSQSReporting extends SendMessageToSQS<GenerateClientReportStatement> {
  public SendMessageToSQSReporting(
      QueueMessagingTemplate queueMessagingTemplate, String sqsEndPoint) {
    super(queueMessagingTemplate, sqsEndPoint);
  }

  @Override
  public Event buildEvent(
      Response response, GenerateClientReportStatement generateClientReportStatement) {
    NewReportEvent newReportEvent = (NewReportEvent) response.getContent();
    Event event = new EventUtils<NewReportEvent>().getEvent(newReportEvent);
    event.setId(newReportEvent.getIdClient());
    return event;
  }
}
