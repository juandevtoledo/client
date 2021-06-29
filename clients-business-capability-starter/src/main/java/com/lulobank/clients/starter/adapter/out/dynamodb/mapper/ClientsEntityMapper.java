package com.lulobank.clients.starter.adapter.out.dynamodb.mapper;

import com.lulobank.clients.starter.v3.adapters.out.dynamo.dto.ClientEntity;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClientsEntityMapper {
    ClientsEntityMapper INSTANCE = Mappers.getMapper(ClientsEntityMapper.class);

    ClientsV3Entity toClientsV3Entity(ClientEntity clientEntity);

}
