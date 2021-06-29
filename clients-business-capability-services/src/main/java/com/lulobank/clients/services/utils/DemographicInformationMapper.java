package com.lulobank.clients.services.utils;

import com.lulobank.clients.sdk.operations.dto.DemographicInfoByIdClient;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;

/**
 * @deprecated (see ClientsDemographicAdapterV3
 * new url /api/v3/client/{idClient}/demographic) 
 */
@Deprecated
public class DemographicInformationMapper {

  public static DemographicInfoByIdClient from(ClientEntity clientEntity) {
    DemographicInfoByIdClient demographicInformationByIdClient = new DemographicInfoByIdClient();
    demographicInformationByIdClient.setIdClient(clientEntity.getIdClient());
    demographicInformationByIdClient.setAddress(clientEntity.getAddress());
    demographicInformationByIdClient.setAddressPrefix(clientEntity.getAddressPrefix());
    demographicInformationByIdClient.setCode(clientEntity.getCode());
    demographicInformationByIdClient.setAddressComplement(clientEntity.getAddressComplement());
    demographicInformationByIdClient.setCity(clientEntity.getCity());
    demographicInformationByIdClient.setCityId(clientEntity.getCityId());
    demographicInformationByIdClient.setDepartment(clientEntity.getDepartment());
    demographicInformationByIdClient.setDepartmentId(clientEntity.getDepartmentId());
    demographicInformationByIdClient.setName(clientEntity.getName());
    demographicInformationByIdClient.setLastName(clientEntity.getLastName());

    return demographicInformationByIdClient;
  }
}
