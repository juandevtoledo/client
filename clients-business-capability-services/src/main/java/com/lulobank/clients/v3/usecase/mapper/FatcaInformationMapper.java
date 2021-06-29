package com.lulobank.clients.v3.usecase.mapper;

import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.FatcaInformationV3;
import com.lulobank.clients.v3.usecase.command.ClientFatcaInformation;
import com.lulobank.clients.v3.usecase.command.GetClientFatcaResponse;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FatcaInformationMapper {

    FatcaInformationMapper INSTANCE = Mappers.getMapper(FatcaInformationMapper.class);

    @Mapping(target = "declaredDate", expression = "java(java.time.LocalDateTime.now())")
    FatcaInformationV3 fromRequest(ClientFatcaInformation clientFatcaInformation);

    @AfterMapping
    default void mapStatus(@MappingTarget FatcaInformationV3 fatcaInformationV3) {
        fatcaInformationV3.setStatus(fatcaInformationV3.isFatcaResponsibility() ? "VALIDATION" : "REPORTED");
    }

    @Mapping(target = "fatcaResponsibility", source = "fatcaInformation.fatcaResponsibility")
    @Mapping(target = "countryCode", source = "fatcaInformation.countryCode")
    @Mapping(target = "countryName", source = "fatcaInformation.countryName")
    @Mapping(target = "tin", source = "fatcaInformation.tin")
    @Mapping(target = "tinObservation", source = "fatcaInformation.tinObservation")
    @Mapping(target = "declaredDate", source = "fatcaInformation.declaredDate")
    @Mapping(target = "status", source = "fatcaInformation.status")
    @Mapping(target = "birthPlace", source = "additionalPersonalInformation.placeBirth")
    @Mapping(target = "address", source = "address")
    @Mapping(target = "addressComplement", source = "addressComplement")
    @Mapping(target = "city", source = "city")
    @Mapping(target = "birthDate", source = "birthDate")
    GetClientFatcaResponse toGetClientFatcaResponse(ClientsV3Entity entity);
}
