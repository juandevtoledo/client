package com.lulobank.clients.services.domain.activateselfcertifiedpepclient;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Document {
    private String documentType;
    private String cardId;
}
