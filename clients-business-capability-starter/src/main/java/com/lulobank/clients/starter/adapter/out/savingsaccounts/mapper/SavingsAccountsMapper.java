package com.lulobank.clients.starter.adapter.out.savingsaccounts.mapper;

import com.lulobank.clients.services.application.port.out.savingsaccounts.model.SavingAccount;
import com.lulobank.clients.starter.adapter.out.savingsaccounts.dto.SavingAccountType;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SavingsAccountsMapper {
    SavingsAccountsMapper INSTANCE = Mappers.getMapper(SavingsAccountsMapper.class);

    SavingAccount toDomain(SavingAccountType accountsByClient);


}