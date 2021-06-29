package com.lulobank.clients.starter.outboundadapter.sqs;

import com.lulobank.clients.services.features.profilev2.model.UpdateClientEmailRequest;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.ports.out.MessageService;
import com.lulobank.clients.v3.adapters.port.out.messaging.dto.NotificationDisabledTypeMessage;
import io.vavr.control.Option;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;

import java.util.List;

public class QueueServiceAdapter implements MessageService {

    private final QueueMessagingTemplate queueMessagingTemplate;
    private final DigitalEvidenceEvent digitalEvidenceEvent;
    private final UpdateClientAddressNotificationEvent updateClientAddressNotificationEvent;
    private final NotificationDisabledEvent notificationDisabledEvent;
    private final List<UpdateProductEmailEvent> updateProductEmailEventList;
    public QueueServiceAdapter(QueueMessagingTemplate queueMessagingTemplate,
                               DigitalEvidenceEvent digitalEvidenceEvent,
                               List<UpdateProductEmailEvent> updateProductEmailEventList,
                               UpdateClientAddressNotificationEvent updateClientAddressNotificationEvent,
                               NotificationDisabledEvent notificationDisabledEvent) {
        this.queueMessagingTemplate = queueMessagingTemplate;
        this.digitalEvidenceEvent = digitalEvidenceEvent;
        this.updateClientAddressNotificationEvent = updateClientAddressNotificationEvent;
        this.notificationDisabledEvent = notificationDisabledEvent;
        this.updateProductEmailEventList = updateProductEmailEventList;
    }

    @Override
    public void sendDigitalEvidenceMessage(String event) {
        Option.of(event)
                .peek(e->digitalEvidenceEvent.send(e,
                        r->queueMessagingTemplate.convertAndSend(r.getEndpoint(), r.getEvent())));
    }

    @Override
    public void sendUpdateEmailMessage(UpdateClientEmailRequest updateEmailRequest){
        updateProductEmailEventList.forEach( updateEmailEvent ->
                Option.of(updateEmailRequest)
                        .peek(e -> updateEmailEvent.send(e,
                                r -> queueMessagingTemplate.convertAndSend(r.getEndpoint(), r.getEvent()))));
    }

    @Override
    public void sendNotificationUpdateAddress(ClientEntity clientEntity) {
        Option.of(clientEntity)
                .peek(e -> updateClientAddressNotificationEvent.send(e,
                        r -> queueMessagingTemplate.convertAndSend(r.getEndpoint(), r.getEvent())));
    }

    @Override
    public void sendNotificationDisabled(NotificationDisabledTypeMessage event){
        Option.of(event)
                .peek(e-> notificationDisabledEvent.send(e,
                        r-> queueMessagingTemplate.convertAndSend(r.getEndpoint(), r.getEvent())));
    }

}
