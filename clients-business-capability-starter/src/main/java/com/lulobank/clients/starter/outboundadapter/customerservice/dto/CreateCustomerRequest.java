package com.lulobank.clients.starter.outboundadapter.customerservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCustomerRequest {
    private String fullName;
    private String documentNumber;
    private String emailAddress;
    private String phoneNumber;
    private String documentType;

}
