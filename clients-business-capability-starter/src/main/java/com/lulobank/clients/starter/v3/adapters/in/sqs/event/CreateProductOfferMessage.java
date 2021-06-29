package com.lulobank.clients.starter.v3.adapters.in.sqs.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateProductOfferMessage {
    private String idClient;
    private String type;
    private Integer value;
}
