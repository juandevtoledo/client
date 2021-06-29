package com.lulobank.clients.services.mapper;

import com.lulobank.clients.services.events.IdentityInformation;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IdentityInformationMapper {

    IdentityInformationMapper INSTANCE = Mappers.getMapper(IdentityInformationMapper.class);

    @Mapping(target = "name", source = "name")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "gender", source = "gender")
    @Mapping(target = "documentNumber", source = "idCard")
    @Mapping(target = "documentType", source = "typeDocument")
    @Mapping(target = "expeditionDate", source = "dateOfIssue", dateFormat = "uuuu-MM-dd")
    @Mapping(target = "birthDate", source = "birthDate", dateFormat = "uuuu-MM-dd")
    @Mapping(target = "phone.number", source = "phoneNumber")
    @Mapping(target = "phone.prefix", source = "phonePrefix")
    @Mapping(target = "email", source = "emailAddress")
    IdentityInformation identityInformationFromClientEntity(ClientEntity clientEntity);
}
