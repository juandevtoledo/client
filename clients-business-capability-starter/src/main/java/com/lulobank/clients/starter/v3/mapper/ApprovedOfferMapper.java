package com.lulobank.clients.starter.v3.mapper;

import com.lulobank.clients.services.domain.productoffers.ProductOffer;
import com.lulobank.clients.starter.v3.adapters.in.dto.ApprovedOffer;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ApprovedOfferMapper {

    ApprovedOfferMapper INSTANCE = Mappers.getMapper(ApprovedOfferMapper.class);

    ApprovedOffer toApprovedOffer(ProductOffer productOffer);
}
