package com.lulobank.clients.starter.outboundadapter.authentication.mapper;

import com.lulobank.authentication.sdk.dto.InitialClientTokenRequest;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuthenticationServiceMapper {

    AuthenticationServiceMapper INSTANCE = Mappers.getMapper(AuthenticationServiceMapper.class);

    InitialClientTokenRequest toInitialClientTokenRequest(ClientsV3Entity clientEntity, String password);


}
