package com.lulobank.clients.starter.outboundadapter.customerservice.mapper;

import com.lulobank.clients.starter.outboundadapter.customerservice.dto.CreateCustomerRequest;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CustomerServiceAdapterMapper {

    CustomerServiceAdapterMapper INSTANCE = Mappers.getMapper(CustomerServiceAdapterMapper.class);

    @Mapping(source = "idCard", target = "documentNumber")
    @Mapping(target = "fullName", expression = "java(clientsV3Entity.getName() + \" \" + clientsV3Entity.getLastName())")
    @Mapping(source = "typeDocument", target = "documentType")
    CreateCustomerRequest toCreateCustomerRequest(ClientsV3Entity clientsV3Entity);


}
