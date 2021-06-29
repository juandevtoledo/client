package com.lulobank.clients.v3.usecase.command;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ClientFatcaInformation {
    private final String idClient;
    private final boolean fatcaResponsibility;
    private final String countryCode;
    private final String countryName;
    private final String tin;
    private final String tinObservation;
}
