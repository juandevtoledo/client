package com.lulobank.clients.services.domain.riskEngineResultEventv2Message;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PreApprovedOffer {
    private String idClient;
    private Integer value;
}
