package com.lulobank.clients.v3.usecase.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.lulobank.clients.services.utils.IdentityBiometricStatus;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.IdentityBiometricV3;
import com.lulobank.clients.v3.usecase.command.ClientBiometricIdTransactionRequest;

@Mapper(imports = IdentityBiometricStatus.class)
public interface IdentityBiometricMapper {

	IdentityBiometricMapper INSTANCE = Mappers.getMapper(IdentityBiometricMapper.class);

	@Mapping(target = "idTransaction", source = "idTransactionBiometric")
	@Mapping(target = "status", expression = "java(IdentityBiometricStatus.IN_PROGRESS.name())")
	@Mapping(target = "transactionState", ignore = true)
	IdentityBiometricV3 identityBiometricFrom(ClientBiometricIdTransactionRequest clientBiometricIdTransaction);
}
