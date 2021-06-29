package com.lulobank.clients.services.actions;

import com.lulobank.clients.sdk.operations.dto.economicinformation.ClientEconomicInformation;
import com.lulobank.clients.services.events.IdentityInformation;
import com.lulobank.clients.services.mapper.IdentityInformationMapper;
import com.lulobank.clients.services.outboundadapters.SendMessageToSQS;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.core.Response;
import com.lulobank.core.events.Event;
import com.lulobank.core.utils.EventUtils;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

import static com.lulobank.clients.sdk.operations.util.CheckPoints.FINISH_ON_BOARDING;

import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;

@Slf4j
public class MessageToSQSIdentityInformation extends SendMessageToSQS<ClientEconomicInformation> {

    public MessageToSQSIdentityInformation(QueueMessagingTemplate queueMessagingTemplate, String sqsEndPoint) {
        super(queueMessagingTemplate, sqsEndPoint);
    }

    @Override
    public Event buildEvent(Response response, ClientEconomicInformation request) {
        return Try.of(() -> response)
                .map(resp -> (ClientEntity) response.getContent())
                .peek(entity -> log.info("Validating OnBoardingStatus: identityProcessed = {}", entity.getOnBoardingStatus().getCheckpoint()))
                .filter(entity -> entity.getOnBoardingStatus().getCheckpoint().equalsIgnoreCase(FINISH_ON_BOARDING.name()))
                .peek(resp -> log.info("Sending identity information event: {}", request.getIdClient()))
                .map(IdentityInformationMapper.INSTANCE::identityInformationFromClientEntity)
                .map(info -> new EventUtils<IdentityInformation>().getEvent(info))
                .peek(event -> event.setId(request.getIdClient()))
                .onFailure(e -> log.info("Error sending identity information event: {}", e.getMessage()))
                .getOrNull();
    }
}