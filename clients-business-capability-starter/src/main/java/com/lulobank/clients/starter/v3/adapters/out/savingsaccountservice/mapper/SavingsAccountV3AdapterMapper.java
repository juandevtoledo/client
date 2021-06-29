package com.lulobank.clients.starter.v3.adapters.out.savingsaccountservice.mapper;

import com.lulobank.clients.starter.v3.adapters.out.savingsaccountservice.dto.CreateSavingsAccountRequest;
import com.lulobank.clients.starter.v3.adapters.in.dto.ErrorResult;
import com.lulobank.clients.starter.v3.adapters.out.savingsaccountservice.dto.SavingAccountCreated;
import com.lulobank.clients.v3.adapters.port.out.savingsaccount.error.SavingsAccountError;
import com.lulobank.clients.v3.adapters.port.out.savingsaccount.dto.SavingsAccountRequest;
import com.lulobank.clients.v3.adapters.port.out.savingsaccount.dto.SavingsAccountResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SavingsAccountV3AdapterMapper {

    SavingsAccountV3AdapterMapper INSTANCE= Mappers.getMapper(SavingsAccountV3AdapterMapper.class);

    CreateSavingsAccountRequest toCreateSavingsAccountRequest(SavingsAccountRequest savingsAccountRequest);

    SavingsAccountResponse toSavingsAccountResponse(SavingAccountCreated savingAccountCreated);

    @Mapping(target = "businessCode",constant = "Error in Saving Account Service")
    @Mapping(target = "detail",source = "failure")
    @Mapping(target = "providerCode",source = "value")
    SavingsAccountError toSavingsAccountError(ErrorResult errorResult);
}
