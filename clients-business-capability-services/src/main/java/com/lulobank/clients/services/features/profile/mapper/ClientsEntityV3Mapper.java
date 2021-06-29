package com.lulobank.clients.services.features.profile.mapper;

import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClientsEntityV3Mapper {

    ClientsEntityV3Mapper INSTANCE = Mappers.getMapper(ClientsEntityV3Mapper.class);

    ClientEntity toClientEntity(ClientsV3Entity clientsV3Entity);

    ClientsV3Entity toClientsV3Entity(ClientEntity clientEntity);



}
