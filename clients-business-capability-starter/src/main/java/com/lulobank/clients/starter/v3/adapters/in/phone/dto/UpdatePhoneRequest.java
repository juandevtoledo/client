package com.lulobank.clients.starter.v3.adapters.in.phone.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePhoneRequest {

    private String newPhoneNumber;
    private Integer countryCode;
}
