package com.lulobank.clients.v3.usecase.command;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UpdatePhoneNumber {
    private final String idClient;
    private final String newPhoneNumber;
    private final Integer countryCode;
}

