package com.lulobank.clients.starter.v3.adapters.in.sqs.handler;

import com.lulobank.clients.services.domain.activateselfcertifiedpepclient.ActivatePepClientRequest;
import com.lulobank.clients.services.domain.activateselfcertifiedpepclient.ClientPersonalInformation;
import com.lulobank.clients.services.domain.activateselfcertifiedpepclient.Document;
import com.lulobank.clients.starter.v3.adapters.in.sqs.event.ActivateSelfCertifiedPEPClient;
import com.lulobank.clients.v3.adapters.port.in.activateselfcertifiedpepclient.ActivatePepClientPort;
import com.lulobank.events.api.EventHandler;
import io.vavr.control.Try;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;

@CustomLog
@RequiredArgsConstructor
public class ActivatePepClientHandler implements EventHandler<ActivateSelfCertifiedPEPClient> {

    private final ActivatePepClientPort activatePEPClientPort;


    @Override
    public Try<Void> execute(ActivateSelfCertifiedPEPClient event) {
        return Try.of(() -> buildRequest(event))
                .flatMap(activatePEPClientPort::execute)
                .onSuccess(success -> log.info("ActivateSelfCertifiedPEPClient Event was successful process , idCard {} ",
                        event.getClientPersonalInformation().getDocument().getCardId()))
                .onFailure(error -> log.info("ActivateSelfCertifiedPEPClient Event was failed , idCard {}, msg : {} ",
                        event.getClientPersonalInformation().getDocument().getCardId(), error.getMessage(), error));
    }

    private ActivatePepClientRequest buildRequest(ActivateSelfCertifiedPEPClient event) {
        return ActivatePepClientRequest.builder()
                .whitelisted(event.isWhitelisted())
                .clientPersonalInformation(ClientPersonalInformation.builder()
                        .document(Document.builder()
                                .documentType(event.getClientPersonalInformation().getDocument().getDocumentType())
                                .cardId(event.getClientPersonalInformation().getDocument().getCardId())
                                .build())
                        .build())
                .whitelistExpirationDate(StringUtils.isEmpty(event.getWhitelistExpirationDate()) ? null :
                        LocalDateTime.parse(event.getWhitelistExpirationDate()))
                .build();
    }

    @Override
    public Class<ActivateSelfCertifiedPEPClient> eventClass() {
        return ActivateSelfCertifiedPEPClient.class;
    }
}
