package com.lulobank.clients.starter.adapter.in.mapper;

import com.lulobank.clients.services.domain.error.UseCaseResponseError;
import com.lulobank.clients.starter.adapter.in.dto.ErrorResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GenericResponseMapper {
	
	GenericResponseMapper INSTANCE = Mappers.getMapper(GenericResponseMapper.class);
	
    @Mapping(source = "businessCode", target = "code")
    @Mapping(source = "providerCode", target = "failure")
    ErrorResponse toErrorResponse(UseCaseResponseError savingsAccountResponseError);
}
