package com.lulobank.clients.services.domain.productoffers;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductOffer {
    private final String idProductOffer;
    private final String state;
    private final String type;
    private final Integer expiredDays;
    private final String description;
    private final String additionalInfo;
    private final Integer value;
}
