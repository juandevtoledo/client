package com.lulobank.clients.services.actions;

import com.lulobank.clients.sdk.operations.dto.economicinformation.ClientEconomicInformation;
import static com.lulobank.clients.sdk.operations.util.CheckPoints.FINISH_ON_BOARDING;
import com.lulobank.clients.services.events.EconomicInformationEvent;
import com.lulobank.clients.services.outboundadapters.SendMessageToSQS;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.core.Response;
import com.lulobank.core.events.Event;
import com.lulobank.core.utils.EventUtils;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;

import static com.lulobank.clients.services.utils.ConverterObjectUtils.getEconomicInfoEvent;

@Slf4j
public class MessageToSQSEconomicInformation extends SendMessageToSQS<ClientEconomicInformation> {

    public MessageToSQSEconomicInformation(QueueMessagingTemplate queueMessagingTemplate, String sqsEndPoint) {
        super(queueMessagingTemplate, sqsEndPoint);
    }

    @Override
    public Event buildEvent(Response response, ClientEconomicInformation request) {
        return Try.of(() -> response)
                .map(resp -> (ClientEntity) response.getContent())
                .peek(entity -> log.info("Validating OnBoardingStatus: identityProcessed = {}", entity.getOnBoardingStatus().getCheckpoint()))
                .filter(entity -> entity.getOnBoardingStatus().getCheckpoint().equalsIgnoreCase(FINISH_ON_BOARDING.name()))
                .peek(entity -> log.info("Sending economic information event: {}", request.getIdClient()))
                .map(entity -> getEconomicInfoEvent(request))
                .map(economic -> new EventUtils<EconomicInformationEvent>().getEvent(economic))
                .peek(event -> event.setId(request.getIdClient()))
                .getOrNull();
    }
}