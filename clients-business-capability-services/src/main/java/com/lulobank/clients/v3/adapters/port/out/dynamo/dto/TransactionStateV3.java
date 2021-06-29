package com.lulobank.clients.v3.adapters.port.out.dynamo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionStateV3 {
    private Integer id;
    private String stateName;
}
