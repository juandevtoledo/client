package com.lulobank.clients.v3.adapters.port.out.dynamo.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class FatcaInformationV3 {
    private boolean fatcaResponsibility;
    private String countryCode;
    private String countryName;
    private String tin;
    private String tinObservation;
    private LocalDateTime declaredDate;
    private String status;
}
