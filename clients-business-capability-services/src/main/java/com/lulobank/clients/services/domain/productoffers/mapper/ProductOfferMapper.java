package com.lulobank.clients.services.domain.productoffers.mapper;

import com.lulobank.clients.services.domain.productoffers.ProductOffer;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.RiskOfferV3;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductOfferMapper {

    ProductOfferMapper INSTANCE = Mappers.getMapper(ProductOfferMapper.class);

    ProductOffer fromRiskOfferV3(RiskOfferV3 riskOfferV3);
}
