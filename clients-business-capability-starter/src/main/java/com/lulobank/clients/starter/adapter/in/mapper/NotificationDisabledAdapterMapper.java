package com.lulobank.clients.starter.adapter.in.mapper;

import com.lulobank.clients.services.domain.notification.NotificationDisabledRequest;
import com.lulobank.clients.starter.v3.adapters.in.notification.dto.NotificationDisabledAdapterRequest;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NotificationDisabledAdapterMapper extends GenericResponseMapper{

    NotificationDisabledAdapterMapper INSTANCE = Mappers.getMapper(NotificationDisabledAdapterMapper.class);

    NotificationDisabledRequest toNotificationDisabledRequest(NotificationDisabledAdapterRequest request);
}
