package com.lulobank.clients.v3.adapters.port.out.dynamo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RiskOfferV3 {
    private String idProductOffer;
    private LocalDateTime offerDate;
    private OfferState state;
    private String type;
    private Integer expiredDays;
    private String description;
    private String additionalInfo;
    private Integer value;
}
