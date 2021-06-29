package com.lulobank.clients.services.features.onboardingclients.action;

import static com.lulobank.clients.services.utils.SQSUtil.RETRY_COUNT_HEADER;
import static org.springframework.cloud.aws.messaging.core.SqsMessageHeaders.SQS_DELAY_HEADER;

import com.lulobank.clients.services.events.RiskScoringResult;
import com.lulobank.clients.services.outboundadapters.SendMessageToSQS;
import com.lulobank.core.Response;
import com.lulobank.core.events.Event;
import com.lulobank.core.utils.EventUtils;
import java.util.Map;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;

public class MessageToNotifySQSRiskEngineResponse extends SendMessageToSQS<RiskScoringResult> {
  public MessageToNotifySQSRiskEngineResponse(
      QueueMessagingTemplate queueMessagingTemplate, String sqsEndPoint) {
    super(queueMessagingTemplate, sqsEndPoint);
  }

  @Override
  public Event buildEvent(Response response, RiskScoringResult riskScoringResult) {
    Event event = new EventUtils<RiskScoringResult>().getEvent(riskScoringResult);
    event.setId(String.valueOf(response.getContent()));
    return event;
  }

  @Override
  public void setAdditionalHeaders(
      Map<String, Object> headers, RiskScoringResult riskScoringResult) {
    headers.put(RETRY_COUNT_HEADER, riskScoringResult.getRetryCount());
    headers.put(SQS_DELAY_HEADER, riskScoringResult.getDelayInSeconds());
  }
}
