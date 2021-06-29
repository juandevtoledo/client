package com.lulobank.clients.starter.v3.adapters.out.sqs.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateReportMessage <T>{
    private final String idClient;
    private final String productType;
    private final String reportType;
    private final T data;

}
