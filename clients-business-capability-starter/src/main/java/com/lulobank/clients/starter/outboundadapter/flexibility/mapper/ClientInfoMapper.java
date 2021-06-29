package com.lulobank.clients.starter.outboundadapter.flexibility.mapper;

import com.lulobank.clients.sdk.operations.dto.UpdateClientAddressRequest;
import flexibility.client.models.request.GetClientRequest;
import flexibility.client.models.request.UpdateClientRequest;
import flexibility.client.models.response.GetClientResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClientInfoMapper {

    ClientInfoMapper INSTANCE = Mappers.getMapper(ClientInfoMapper.class);

    @Mapping(source = "idCbs", target = "clientId")
    GetClientRequest toGetClientRequest(String idCbs);

    @Mapping(source = "id", target = "clientId")
    UpdateClientRequest toUpdateClientRequest(GetClientResponse getClientResponse);

    @Mapping(source = "addressComplement", target = "description")
    @Mapping(source = "code", target = "dian")
    @Mapping(source = "cityId", target = "municipality")
    @Mapping(source = "departmentId", target = "department")
    @Mapping(source = "city", target = "city.description")
    @Mapping(source = "cityId", target = "city.code")
    @Mapping(source = "department", target = "state.description")
    @Mapping(source = "departmentId", target = "state.code")
    UpdateClientRequest.Address toUpdateClientRequestAddress(UpdateClientAddressRequest updateClientAddressRequest);

}
