package com.lulobank.clients.starter.v3.adapters.out.approvedriskengine;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.RiskOfferV3;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ApprovedRiskEngineMapper {
	
	ApprovedRiskEngineMapper INSTANCE = Mappers.getMapper(ApprovedRiskEngineMapper.class);
	
	@Mapping(target = "idProductOffer", source = "riskOfferV3.idProductOffer")
	@Mapping(target = "idClient", source = "clientsV3Entity.idClient")
	@Mapping(target = "clientInformation.documentId.id", source = "clientsV3Entity.idCard")
	@Mapping(target = "clientInformation.documentId.type", source = "clientsV3Entity.typeDocument")
	@Mapping(target = "clientInformation.documentId.issueDate", source = "clientsV3Entity.expirationDate")
	@Mapping(target = "clientInformation.name", source = "clientsV3Entity.additionalPersonalInformation.firstName")
	@Mapping(target = "clientInformation.lastName", source = "clientsV3Entity.additionalPersonalInformation.firstSurname")
	@Mapping(target = "clientInformation.middleName", source = "clientsV3Entity.additionalPersonalInformation.secondName")
	@Mapping(target = "clientInformation.secondSurname", source = "clientsV3Entity.additionalPersonalInformation.secondSurname")
	@Mapping(target = "clientInformation.gender", source = "clientsV3Entity.gender")
	@Mapping(target = "clientInformation.email", source = "clientsV3Entity.emailAddress")
	@Mapping(target = "clientInformation.phone.number", source = "clientsV3Entity.phoneNumber")
	@Mapping(target = "clientInformation.phone.prefix", source = "clientsV3Entity.phonePrefix")
	RiskEngineResponseMessage riskOfferV3toRiskEngineResponseMessage(ClientsV3Entity clientsV3Entity, RiskOfferV3 riskOfferV3);
}
