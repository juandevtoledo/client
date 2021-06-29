package com.lulobank.clients.services.features.clientverificationresult.mapper;

import com.lulobank.clients.services.events.IdentityInformation;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface IdentityInformationMapper {
    IdentityInformationMapper INSTANCE= Mappers.getMapper(IdentityInformationMapper.class);
    @Mappings(value = {
            @Mapping(target = "documentNumber", source = "idCard"),
            @Mapping(target = "expeditionDate", source = "dateOfIssue", dateFormat = "uuuu-MM-dd"),
            @Mapping(target = "documentType", source = "typeDocument"),
            @Mapping(target = "birthDate", source = "birthDate",dateFormat = "uuuu-MM-dd"),
            @Mapping(target = "phone.number", source = "phoneNumber"),
            @Mapping(target = "phone.prefix", source = "phonePrefix"),
            @Mapping(target = "email", source = "emailAddress")
    })
    IdentityInformation identityInformationFromClient(ClientsV3Entity clientEntity);
}
