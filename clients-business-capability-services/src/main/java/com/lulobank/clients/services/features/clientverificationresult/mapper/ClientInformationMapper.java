package com.lulobank.clients.services.features.clientverificationresult.mapper;

import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.savingsaccounts.sdk.dto.createsavingsaccount.ClientInformation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ClientInformationMapper {

    ClientInformationMapper INSTANCE = Mappers.getMapper(ClientInformationMapper.class);

    @Mappings(value = {
            @Mapping(target = "email", source = "emailAddress"),
            @Mapping(target = "phone.number", source = "phoneNumber"),
            @Mapping(target = "phone.prefix", source = "phonePrefix"),
            @Mapping(target = "documentId.id", source = "idCard"),
            @Mapping(target = "documentId.issueDate", source = "dateOfIssue", dateFormat = "uuuu-MM-dd"),
            @Mapping(target = "documentId.type", source = "typeDocument")
    })
    ClientInformation clientInformationFrom(ClientsV3Entity clientEntity);
}
