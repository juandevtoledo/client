package com.lulobank.clients.services.features.profilev2.model;

import com.lulobank.clients.services.features.profile.model.UpdateEmailClientRequest;
import com.lulobank.clients.services.features.profile.model.UpdateEmailClientResponse;
import flexibility.client.models.request.UpdateClientRequest;
import flexibility.client.models.response.GetClientResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UpdateEmailMapper {

  UpdateEmailMapper UPDATE_EMAIL_MAPPER = Mappers.getMapper( UpdateEmailMapper.class );

  UpdateEmailClientRequest toSqsRequest(UpdateClientEmailRequest clientInformation);

  UpdateEmailClientResponse toSqsRequest(UpdateEmailNotification clientInformation);

  @Mapping(target = "clientId", source = "id")
  @Mapping(target = "address.address", source = "address.address" , defaultValue = " ")
  @Mapping(target = "address.city.code", source = "address.city.code", defaultValue = " ")
  @Mapping(target = "address.city.description", source = "address.city.description", defaultValue = " ")
  @Mapping(target = "address.state.code", source = "address.state.code" , defaultValue = " ")
  @Mapping(target = "address.state.description", source = "address.state.description", defaultValue = " ")
  UpdateClientRequest toFlexibilityRequest(GetClientResponse getClientResponse);

}
