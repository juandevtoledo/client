package com.lulobank.clients.services.features.clientverificationresult.mapper;

import com.lulobank.clients.sdk.operations.dto.economicinformation.ClientEconomicInformation;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {ForeignTransactionMapper.class})
public interface EconomicInformationMapper {

    EconomicInformationMapper INSTANCE = Mappers.getMapper(EconomicInformationMapper.class);

    @Mapping(target = "idClient", source = "idClient")
    @Mapping(target = "occupationType", source = "economicInformation.occupationType")
    @Mapping(target = "employeeCompany.name", source = "economicInformation.company.name")
    @Mapping(target = "employeeCompany.state", source = "economicInformation.company.state")
    @Mapping(target = "employeeCompany.city", source = "economicInformation.company.city")
    @Mapping(target = "economicActivity", source = "economicInformation.economicActivity")
    @Mapping(target = "monthlyIncome", source = "economicInformation.monthlyIncome")
    @Mapping(target = "monthlyOutcome", source = "economicInformation.monthlyOutcome")
    @Mapping(target = "additionalIncome", source = "economicInformation.additionalIncome")
    @Mapping(target = "assets", source = "economicInformation.assets")
    @Mapping(target = "liabilities", source = "economicInformation.liabilities")
    @Mapping(target = "savingPurpose", source = "economicInformation.savingPurpose")
    @Mapping(target = "typeSaving", source = "economicInformation.typeSaving")
    ClientEconomicInformation economicInformationFromEntity(ClientsV3Entity clientEntity);
}
