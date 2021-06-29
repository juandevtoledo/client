package com.lulobank.clients.starter.inboundadapter.mapper;

import com.lulobank.clients.services.features.initialclient.model.CreateInitialClient;
import com.lulobank.clients.starter.inboundadapter.dto.CreateInitialClientRequest;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InboundsMapper {

    InboundsMapper INSTANCE = Mappers.getMapper(InboundsMapper.class);

    CreateInitialClient toCommand(CreateInitialClientRequest clientEntity);

}
