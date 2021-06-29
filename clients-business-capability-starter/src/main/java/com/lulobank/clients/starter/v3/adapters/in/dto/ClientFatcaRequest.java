package com.lulobank.clients.starter.v3.adapters.in.dto;

import com.lulobank.clients.starter.v3.adapters.in.validator.FatcaInformation;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@FatcaInformation
public class ClientFatcaRequest {
    private String idClient;
    @NotNull(message = "fatcaResponsibility is null or empty")
    private Boolean fatcaResponsibility;
    private String countryCode;
    private String countryName;
    private String tin;
    private String tinObservation;
}
