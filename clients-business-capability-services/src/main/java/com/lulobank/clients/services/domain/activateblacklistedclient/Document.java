package com.lulobank.clients.services.domain.activateblacklistedclient;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Document {
    private String documentType;
    private String idCard;
    private String expeditionDate;
}
