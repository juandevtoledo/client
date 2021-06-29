package com.lulobank.clients.starter.outboundadapter.profile.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class UpdatePhoneRequest {

    @NotNull
    private String newPhoneNumber;
    @NotNull
    private String email;
    @NotNull
    private Integer countryCode;

}
