package com.lulobank.clients.v3.usecase.fatca.event;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FatcaDataReport {
    public final String fistName;
    public final String middleName;
    public final String surname;
    public final String secondSurname;
    public final String documentType;
    public final String idCard;
    public final String birthDate;
    public final String birthPlace;
    public final String address;
    public final String addressComplement;
    public final String city;
    public final String phoneNumber;
    public final String taxLiability;
    public final String country;
    public final String taxNumber;
    public final String reportDate;


}
