package com.lulobank.clients.starter.adapter.in.zendesk.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Product {
    private String productType;
    private String productNumber;
    private String status;
    private String created;
}
