package com.lulobank.clients.services.features.onboardingclients.action;

import static com.lulobank.clients.services.utils.SQSUtil.RETRY_COUNT_HEADER;
import static org.springframework.cloud.aws.messaging.core.SqsMessageHeaders.SQS_DELAY_HEADER;

import com.lulobank.clients.services.events.ClientVerificationResult;
import com.lulobank.clients.services.outboundadapters.SendMessageToSQS;
import com.lulobank.core.Response;
import com.lulobank.core.events.Event;
import com.lulobank.core.utils.EventUtils;
import java.util.Map;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;

public class MessageToNotifySQSClients extends SendMessageToSQS<ClientVerificationResult> {
  public MessageToNotifySQSClients(
      QueueMessagingTemplate queueMessagingTemplate, String sqsEndPoint) {
    super(queueMessagingTemplate, sqsEndPoint);
  }

  @Override
  public Event buildEvent(Response response, ClientVerificationResult clientVerificationResult) {
    Event event = new EventUtils<ClientVerificationResult>().getEvent(clientVerificationResult);
    event.setId(String.valueOf(response.getContent()));
    return event;
  }

  @Override
  public void setAdditionalHeaders(
      Map<String, Object> headers, ClientVerificationResult clientVerificationResult) {
    headers.put(RETRY_COUNT_HEADER, clientVerificationResult.getRetryCount());
    headers.put(SQS_DELAY_HEADER, clientVerificationResult.getDelayInSeconds());
  }
}
