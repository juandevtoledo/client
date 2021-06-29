package com.lulobank.clients.v3.mapper;

import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClientEntityMapper {

    ClientEntityMapper INSTANCE = Mappers.getMapper(ClientEntityMapper.class);

    ClientEntity fromEntityV3(ClientsV3Entity clientV3Entity);
}
