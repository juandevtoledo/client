package com.lulobank.clients.starter.outboundadapter.digitalevidence.mapper;

import com.lulobank.clients.services.application.port.out.reporting.model.StoreDigitalEvidenceRequest;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DigitalEvidenceServiceMapper {

    DigitalEvidenceServiceMapper INSTANCE = Mappers.getMapper(DigitalEvidenceServiceMapper.class);

    @Mapping(target = "acceptanceTimestamp",source = "acceptances.documentAcceptancesTimestamp")
    StoreDigitalEvidenceRequest toStoreDigitalEvidenceRequest(ClientsV3Entity clientEntity);


}
