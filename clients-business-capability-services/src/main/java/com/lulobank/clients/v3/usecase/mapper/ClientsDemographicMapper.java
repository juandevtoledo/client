package com.lulobank.clients.v3.usecase.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.clients.v3.usecase.command.ClientDemographicInfo;

@Mapper
public interface ClientsDemographicMapper {
	
	ClientsDemographicMapper INSTANCE= Mappers.getMapper(ClientsDemographicMapper.class);
	
	ClientDemographicInfo clientEntityToDemographicInfo(ClientsV3Entity clientsV3Entity);
}
