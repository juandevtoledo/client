package com.lulobank.clients.starter.v3.adapters.in.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
public class CreateAddressRequest {
    @NotBlank(message = "address is null or empty")
    private String address;
    @NotBlank(message = "addressPrefix is null or empty")
    private String addressPrefix;
    private String addressComplement;
    @NotBlank(message = "city is null or empty")
    private String city;
    @NotBlank(message = "cityId is null or empty")
    private String cityId;
    @NotBlank(message = "department is null or empty")
    private String department;
    @NotBlank(message = "departmentId is null or empty")
    private String departmentId;
    @NotBlank(message = "code is empty or null")
    private String code;
}
