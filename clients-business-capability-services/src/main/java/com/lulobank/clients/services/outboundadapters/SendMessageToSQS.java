package com.lulobank.clients.services.outboundadapters;

import static com.lulobank.clients.services.utils.LogMessages.EVENT_NOT_FORMAT;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lulobank.clients.sdk.operations.dto.AbstractCommandFeatures;
import com.lulobank.clients.services.utils.LogMessages;
import com.lulobank.core.Command;
import com.lulobank.core.Response;
import com.lulobank.core.actions.Action;
import com.lulobank.core.events.Event;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import lombok.CustomLog;
import org.owasp.encoder.Encode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;

@CustomLog
public abstract class SendMessageToSQS<T extends Command> implements Action<Response, T> {

  private QueueMessagingTemplate queueMessagingTemplate;
  private String sqsEndPoint;

  @Autowired
  public SendMessageToSQS(QueueMessagingTemplate queueMessagingTemplate, String sqsEndPoint) {
    this.sqsEndPoint = sqsEndPoint;
    this.queueMessagingTemplate = queueMessagingTemplate;
  }

  public abstract Event buildEvent(Response response, T command);

  @Override
  public void run(Response response, T command) {
    ObjectMapper objectMapper = new ObjectMapper();
    Event event = this.buildEvent(response, command);
    if (Objects.nonNull(event)) {
      Map<String, Object> headers = getMessageHeaders(command);
      setAdditionalHeaders(headers, command);
      queueMessagingTemplate.convertAndSend(sqsEndPoint, event, headers);
      try {
        String jsonEvent = objectMapper.writeValueAsString(event);
        log.info(
            LogMessages.EVENT_SENT_TO_SQS.getMessage(),
            Encode.forJava(event.getId()),
            event.getEventType(),
            sqsEndPoint,
            jsonEvent);
      } catch (JsonProcessingException e) {
        log.error(EVENT_NOT_FORMAT.getMessage(), event.getEventType());
      }
    }
  }

  private Map<String, Object> getMessageHeaders(T command) {
    if (command instanceof AbstractCommandFeatures) {
      return ((AbstractCommandFeatures) command)
          .getAuthorizationHeader().entrySet().stream()
              .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
    return new HashMap<>();
  }

  public void setAdditionalHeaders(Map<String, Object> headers, T command) {}
}
