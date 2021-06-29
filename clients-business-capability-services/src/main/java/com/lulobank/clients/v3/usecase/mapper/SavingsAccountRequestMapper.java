package com.lulobank.clients.v3.usecase.mapper;

import com.lulobank.clients.v3.adapters.port.out.savingsaccount.dto.SavingsAccountRequest;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SavingsAccountRequestMapper {

    SavingsAccountRequestMapper INSTANCE= Mappers.getMapper(SavingsAccountRequestMapper.class);

    @Mapping(target = "clientInformation.name",source ="name" )
    @Mapping(target = "clientInformation.lastName",source ="lastName" )
    @Mapping(target = "clientInformation.middleName",source ="additionalPersonalInformation.secondName" )
    @Mapping(target = "clientInformation.secondSurname",source ="additionalPersonalInformation.secondSurname" )
    @Mapping(target = "clientInformation.gender",source ="gender" )
    @Mapping(target = "clientInformation.email",source ="emailAddress" )
    @Mapping(target = "clientInformation.phone.number",source ="phoneNumber" )
    @Mapping(target = "clientInformation.phone.prefix",source ="phonePrefix" )
    @Mapping(target = "clientInformation.documentId.id",source ="idCard" )
    @Mapping(target = "clientInformation.documentId.type",source ="typeDocument" )
    @Mapping(target = "clientInformation.documentId.expirationDate",source ="expirationDate" )
    SavingsAccountRequest toSavingsAccountRequest(ClientsV3Entity clientsV3Entity);


}
