package com.lulobank.clients.starter.adapter.in.zendesk.mapper;

import com.lulobank.clients.services.domain.zendeskclientinfo.GetClientInfoByEmailResponse;
import com.lulobank.clients.starter.adapter.in.mapper.GenericResponseMapper;
import com.lulobank.clients.starter.adapter.in.zendesk.dto.GetClientInfoByEmailAdapterResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ZendeskClientInfoAdapterMapper extends GenericResponseMapper {
    ZendeskClientInfoAdapterMapper INSTANCE = Mappers.getMapper(ZendeskClientInfoAdapterMapper.class);

    GetClientInfoByEmailAdapterResponse toGetClientInfoByEmailAdapterResponse(GetClientInfoByEmailResponse getClientInfoByEmailResponse);

}
