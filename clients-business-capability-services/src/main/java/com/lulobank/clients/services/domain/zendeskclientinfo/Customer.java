package com.lulobank.clients.services.domain.zendeskclientinfo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Customer {
    private String documentNumber;
    private String name;
    private String lastName;
    private String mobilePhone;
    private String email;
    private String documentType;
    private String address;

}
