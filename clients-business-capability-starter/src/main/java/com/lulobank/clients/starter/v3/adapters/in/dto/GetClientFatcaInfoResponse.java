package com.lulobank.clients.starter.v3.adapters.in.dto;

import com.lulobank.clients.starter.adapter.in.dto.GenericResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class GetClientFatcaInfoResponse extends GenericResponse {
    private boolean fatcaResponsibility;
    private String countryCode;
    private String countryName;
    private String tin;
    private String tinObservation;
    private LocalDateTime declaredDate;
    private String status;
    private String birthPlace;
    private String birthDate;
    private String address;
    private String addressComplement;
    private String city;
}
