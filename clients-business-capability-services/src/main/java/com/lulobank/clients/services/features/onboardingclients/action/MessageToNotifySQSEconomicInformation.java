package com.lulobank.clients.services.features.onboardingclients.action;

import com.lulobank.clients.sdk.operations.dto.economicinformation.ClientEconomicInformation;
import com.lulobank.clients.services.events.EconomicInformationEvent;
import com.lulobank.clients.services.outboundadapters.SendMessageToSQS;
import com.lulobank.clients.services.utils.ConverterObjectUtils;
import com.lulobank.core.Response;
import com.lulobank.core.events.Event;
import com.lulobank.core.utils.EventUtils;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;

import java.util.Objects;

@Slf4j
public class MessageToNotifySQSEconomicInformation extends SendMessageToSQS<ClientEconomicInformation> {

    public MessageToNotifySQSEconomicInformation(QueueMessagingTemplate queueMessagingTemplate, String sqsEndPoint) {
        super(queueMessagingTemplate, sqsEndPoint);
    }

    @Override
    public Event buildEvent(Response response, ClientEconomicInformation request) {
        return Try.of(() -> request)
                .filter(Objects::nonNull)
                .peek(req -> log.info("Sending identity information event: {}", req.getIdClient()))
                .map(ConverterObjectUtils::getEconomicInfoEvent)
                .map(economic -> new EventUtils<EconomicInformationEvent>().getEvent(economic))
                .peek(event -> event.setId(request.getIdClient()))
                .getOrNull();
    }
}
