package com.lulobank.clients.starter.v3.adapters.in.dto;

import com.lulobank.clients.starter.adapter.in.dto.GenericResponse;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateAddressResponse extends GenericResponse {
    private String address;
    private String addressPrefix;
    private String addressComplement;
    private String city;
    private String cityId;
    private String department;
    private String departmentId;
    private String code;
}
