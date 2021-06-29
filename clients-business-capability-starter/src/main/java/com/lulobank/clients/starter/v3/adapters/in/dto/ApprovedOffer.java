package com.lulobank.clients.starter.v3.adapters.in.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ApprovedOffer {
    private String idProductOffer;
    private String state;
    private String type;
    private Integer expiredDays;
    private String description;
    private String additionalInfo;
    private Integer value;
}
