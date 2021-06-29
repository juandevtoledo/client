package com.lulobank.clients.services.features.onboardingclients.model;

import com.lulobank.clients.sdk.operations.dto.ClientInformationByTypeResponse;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.v3.usecase.pep.PepStatus;
import io.vavr.API;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import static io.vavr.API.$;
import static io.vavr.API.Case;

@Mapper
public interface ClientInfoEntityMapper {

  ClientInfoEntityMapper INSTANCE = Mappers.getMapper(ClientInfoEntityMapper.class);

  @Mapping(source = "idClient", target = "idClient")
  @Mapping(source = "idCard", target = "idCard")
  @Mapping(source = "name", target = "name")
  @Mapping(source = "lastName", target = "lastName")
  @Mapping(source = "phonePrefix", target = "phonePrefix")
  @Mapping(source = "phoneNumber", target = "phoneNumber")
  @Mapping(source = "emailAddress", target = "emailAddress")
  @Mapping(source = "emailVerified", target = "emailVerified")
  @Mapping(source = "idCbs", target = "idCbs")
  @Mapping(source = "idCbsHash", target = "idCbsHash")
  @Mapping(source = "identityBiometric.status", target = "biometricStatus")
  @Mapping(source = "onBoardingStatus.checkpoint", target = "checkpoint")
  @Mapping(source = "onBoardingStatus.productSelected", target = "productSelected")
  @Mapping(source = "idKeycloak", target = "idKeycloak")
  @Mapping(source = "digitalStorageStatus", target = "digitalStorageStatus")
  @Mapping(source = "acceptances.documentAcceptancesTimestamp", target = "documentAcceptancesTimestamp")
  @Mapping(source = "acceptances.persistedInDigitalEvidence", target = "persistedInDigitalEvidence")
  @Mapping(source = "blackListRiskLevel", target = "riskLevel")
  @Mapping(source = "blackListState", target = "blacklistState")
  @Mapping(source = "whitelistExpirationDate", target = "whitelistExpirationDate")
  @Mapping(source = "pep", target = "pep", qualifiedByName = "getPep")
  ClientInformationByTypeResponse createClientEntityToClientInfo(ClientEntity clientEntity);

  @Named("getPep")
  static boolean getPep(String pep){
    return API.Match(pep).of(
            Case($(PepStatus.PEP_WAIT_LIST.value()), true),
            Case($(PepStatus.PEP_BLACKLISTED.value()), true),
            Case($(), false)
    );
  }

}
