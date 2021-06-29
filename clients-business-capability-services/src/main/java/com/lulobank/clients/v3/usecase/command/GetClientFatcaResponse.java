package com.lulobank.clients.v3.usecase.command;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class GetClientFatcaResponse {
    private final boolean fatcaResponsibility;
    private final String countryCode;
    private final String countryName;
    private final String tin;
    private final String tinObservation;
    private final LocalDateTime declaredDate;
    private final String status;
    private final String birthPlace;
    private final String birthDate;
    private final String address;
    private final String addressComplement;
    private final String city;
}
