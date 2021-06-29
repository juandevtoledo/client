package com.lulobank.clients.services.actions;

import static com.lulobank.clients.services.utils.SQSUtil.RETRY_COUNT_HEADER;
import static org.springframework.cloud.aws.messaging.core.SqsMessageHeaders.SQS_DELAY_HEADER;

import com.lulobank.clients.services.events.CheckIdentityBiometric;
import com.lulobank.clients.services.outboundadapters.SendMessageToSQS;
import com.lulobank.core.Response;
import com.lulobank.core.events.Event;
import com.lulobank.core.utils.EventUtils;
import java.util.Map;
import java.util.UUID;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;

public class MessageToSQSCheckBiometricIdentity extends SendMessageToSQS<CheckIdentityBiometric> {
  public MessageToSQSCheckBiometricIdentity(
      QueueMessagingTemplate queueMessagingTemplate, String sqsEndPoint) {
    super(queueMessagingTemplate, sqsEndPoint);
  }

  @Override
  public Event buildEvent(Response response, CheckIdentityBiometric command) {
    Event event = new EventUtils<CheckIdentityBiometric>().getEvent(command);
    event.setId(UUID.randomUUID().toString());
    return event;
  }

  @Override
  public void setAdditionalHeaders(Map<String, Object> headers, CheckIdentityBiometric command) {
    headers.put(RETRY_COUNT_HEADER, command.getRetryCount());
    headers.put(SQS_DELAY_HEADER, command.getDelayInSeconds());
  }
}
