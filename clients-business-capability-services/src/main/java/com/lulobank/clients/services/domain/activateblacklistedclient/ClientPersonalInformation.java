package com.lulobank.clients.services.domain.activateblacklistedclient;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ClientPersonalInformation {
    private Document document;
}
