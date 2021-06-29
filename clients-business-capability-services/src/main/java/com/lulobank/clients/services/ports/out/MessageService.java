package com.lulobank.clients.services.ports.out;

import com.lulobank.clients.services.features.profilev2.model.UpdateClientEmailRequest;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.v3.adapters.port.out.messaging.dto.NotificationDisabledTypeMessage;

public interface MessageService {
    void sendDigitalEvidenceMessage(String event);

    void sendUpdateEmailMessage(UpdateClientEmailRequest updateEmailRequest);

    void sendNotificationUpdateAddress(ClientEntity clientEntity);

    void sendNotificationDisabled(NotificationDisabledTypeMessage event);
}
