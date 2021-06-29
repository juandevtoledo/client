package com.lulobank.clients.starter.v3.mapper;

import com.lulobank.clients.starter.v3.adapters.in.dto.TransactionBiometricResponse;
import com.lulobank.clients.v3.usecase.command.BiometricResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AccountSettlementMapper {

    AccountSettlementMapper INSTANCE = Mappers.getMapper(AccountSettlementMapper.class);

    TransactionBiometricResponse accountSettlementResponseTo(BiometricResponse response);

}
