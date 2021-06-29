package com.lulobank.clients.starter.v3.mapper;

import com.lulobank.clients.starter.v3.adapters.out.dynamo.dto.ClientEntity;
import com.lulobank.clients.starter.v3.adapters.out.dynamo.dto.IdentityBiometric;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.IdentityBiometricV3;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClientsEntityV3Mapper {

    ClientsEntityV3Mapper INSTANCE = Mappers.getMapper(ClientsEntityV3Mapper.class);

    ClientsV3Entity toV3Entity(ClientEntity clientEntity);

    ClientEntity toEntity(ClientsV3Entity clientsV3Entity);

    IdentityBiometric toIdentityBiometric(IdentityBiometricV3 identityBiometricV3);
}
