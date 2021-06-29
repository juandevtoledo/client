package com.lulobank.clients.v3.adapters.port.out.digitalevidence.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AcceptancesDocumentRequest {
    private final String documentAcceptancesTimestamp;
}
