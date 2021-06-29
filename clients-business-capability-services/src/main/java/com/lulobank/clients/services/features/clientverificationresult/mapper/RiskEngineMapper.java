package com.lulobank.clients.services.features.clientverificationresult.mapper;

import com.lulobank.clients.services.ports.out.dto.RiskOfferResponse;
import com.lulobank.clients.services.utils.InterestUtil;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.RiskOfferV3;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = InterestUtil.class)
public interface RiskEngineMapper {

    RiskEngineMapper INSTANCE = Mappers.getMapper(RiskEngineMapper.class);

    @Mapping(target = "idProductOffer", expression = "java(java.util.UUID.randomUUID().toString())")
    @Mapping(target = "state", constant = "ACTIVE")
    @Mapping(target = "type", constant = "REGISTRY_PREAPPROVED")
    @Mapping(target = "offerDate", expression = "java(java.time.LocalDateTime.now())")
    RiskOfferV3 toRiskOfferV3(RiskOfferResponse riskEngineResponse);
}
