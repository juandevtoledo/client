package com.lulobank.clients.services.features.exceptionhandlers;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorHandler {
    private String failure;
    private Integer code;
    private String detail;
}
