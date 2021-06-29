package com.lulobank.clients.services.events;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateClientAddressEvent {
    private String idClient;
    private String checkpoint;
    private String address;
    private String code;
    private String addressPrefix;
    private String addressComplement;
    private String city;
    private String cityId;
    private String department;
    private String departmentId;
}
