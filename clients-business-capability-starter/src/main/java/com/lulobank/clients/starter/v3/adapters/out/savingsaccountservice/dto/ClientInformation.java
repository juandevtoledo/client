package com.lulobank.clients.starter.v3.adapters.out.savingsaccountservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientInformation {

    private DocumentId documentId;
    private String name;
    private String middleName;
    private String lastName;
    private String secondSurname;
    private String gender;
    private String email;
    private Phone phone;
}