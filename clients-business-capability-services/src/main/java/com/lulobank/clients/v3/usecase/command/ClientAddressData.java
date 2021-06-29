package com.lulobank.clients.v3.usecase.command;

import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ClientAddressData {
    private final String idClient;
    private final String address;
    private final String addressPrefix;
    private final String addressComplement;
    private final String city;
    private final String cityId;
    private final String department;
    private final String departmentId;
    private final String code;

    public static ClientAddressData fromClientsV3Entity(ClientsV3Entity clientsV3Entity){
        return ClientAddressData.builder()
                .idClient(clientsV3Entity.getIdClient())
                .address(clientsV3Entity.getAddress())
                .addressPrefix(clientsV3Entity.getAddressPrefix())
                .addressComplement(clientsV3Entity.getAddressComplement())
                .city(clientsV3Entity.getCity())
                .cityId(clientsV3Entity.getCityId())
                .department(clientsV3Entity.getDepartment())
                .departmentId(clientsV3Entity.getDepartmentId())
                .code(clientsV3Entity.getCode())
                .build();
    }
}
