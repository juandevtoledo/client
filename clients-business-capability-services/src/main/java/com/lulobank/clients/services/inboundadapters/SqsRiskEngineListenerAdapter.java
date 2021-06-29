package com.lulobank.clients.services.inboundadapters;

import static com.lulobank.clients.services.utils.LogMessages.CLIENT_NOT_FOUND_IN_DB_EXCEPTION;
import static org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy.ALWAYS;

import com.lulobank.clients.services.exception.ClientNotFoundException;
import com.lulobank.clients.services.features.riskscoreresponse.RiskScoringResultEvent;
import com.lulobank.clients.services.features.riskscoreresponse.RiskScoringResultEventFactory;
import com.lulobank.clients.services.utils.EventUtils;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.owasp.encoder.Encode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class SqsRiskEngineListenerAdapter {
  public static final String PACKAGE_SERVICES = "com.lulobank.clients.services";
  private RiskScoringResultEventFactory riskScoringResultEventFactory;

  @Autowired
  public SqsRiskEngineListenerAdapter(RiskScoringResultEventFactory riskScoringResultEventFactory) {
    this.riskScoringResultEventFactory = riskScoringResultEventFactory;
  }

  @SqsListener(value = "${cloud.aws.sqs.client-riskresponse-events}", deletionPolicy = ALWAYS)
  public void getRiskResponse(
      @Headers Map<String, Object> headers, @Payload final String eventString) {
    Optional.ofNullable(EventUtils.getEvent(eventString, PACKAGE_SERVICES))
        .ifPresent(
            event -> {
              try {
                log.info("Received getRiskResponse event message {}", event.getPayload());
                RiskScoringResultEvent riskScoringResultEvent =
                    riskScoringResultEventFactory.createHandler(event);
                riskScoringResultEvent.apply(event);
              } catch (UnsupportedOperationException ex) {
                log.error(ex.getMessage(), ex);
              } catch (ClientNotFoundException ex) {
                log.error(CLIENT_NOT_FOUND_IN_DB_EXCEPTION.getMessage(), Encode.forJava(event.getId()));
              }
            });
  }
}
