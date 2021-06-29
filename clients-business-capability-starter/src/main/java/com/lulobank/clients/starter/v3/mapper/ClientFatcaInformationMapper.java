package com.lulobank.clients.starter.v3.mapper;

import com.lulobank.clients.starter.adapter.in.mapper.GenericResponseMapper;
import com.lulobank.clients.starter.v3.adapters.in.dto.ClientFatcaRequest;
import com.lulobank.clients.starter.v3.adapters.in.dto.GetClientFatcaInfoResponse;
import com.lulobank.clients.v3.usecase.command.ClientFatcaInformation;
import com.lulobank.clients.v3.usecase.command.GetClientFatcaResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClientFatcaInformationMapper extends GenericResponseMapper {

    ClientFatcaInformationMapper INSTANCE = Mappers.getMapper(ClientFatcaInformationMapper.class);

    ClientFatcaInformation fromClientFatcaRequest(ClientFatcaRequest productOffer);

    GetClientFatcaInfoResponse fromGetClientFatcaResponse(GetClientFatcaResponse response);
}
