package com.lulobank.clients.services.features.profile.mapper;

import com.lulobank.clients.sdk.operations.dto.UpdateClientAddressRequest;
import com.lulobank.clients.services.events.UpdateClientAddressEvent;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UpdateClientAddressMapper {

    UpdateClientAddressMapper INSTANCE = Mappers.getMapper(UpdateClientAddressMapper.class);

    UpdateClientAddressRequest toUpdateClientProfileRequest(UpdateClientAddressEvent updateClientAddressEvent);
}
