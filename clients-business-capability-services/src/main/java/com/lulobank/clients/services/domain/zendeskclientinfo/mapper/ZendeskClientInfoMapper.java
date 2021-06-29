package com.lulobank.clients.services.domain.zendeskclientinfo.mapper;

import com.lulobank.clients.services.application.port.out.debitcards.model.CardStatus;
import com.lulobank.clients.services.application.port.out.debitcards.model.DebitCard;
import com.lulobank.clients.services.application.port.out.savingsaccounts.model.SavingAccount;
import com.lulobank.clients.services.application.util.ProductUtils;
import com.lulobank.clients.services.domain.zendeskclientinfo.GetClientInfoByEmailResponse;
import com.lulobank.clients.services.domain.zendeskclientinfo.Product;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.Collections;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {Collections.class, ProductUtils.class})
public interface ZendeskClientInfoMapper {

    ZendeskClientInfoMapper INSTANCE = Mappers.getMapper(ZendeskClientInfoMapper.class);

    @Mapping(source = "name", target = "customer.name")
    @Mapping(source = "lastName", target = "customer.lastName")
    @Mapping(source = "idCard", target = "customer.documentNumber")
    @Mapping(source = "phoneNumber", target = "customer.mobilePhone")
    @Mapping(source = "emailAddress", target = "customer.email")
    @Mapping(source = "typeDocument", target = "customer.documentType")
    @Mapping(source = "address", target = "customer.address")
    GetClientInfoByEmailResponse toGetClientInfoByEmailUseCaseResponse(ClientsV3Entity clientsV3Entity);


    @Mapping(expression = "java(ProductUtils.hideProductNumberSavingAccount(savingAccount))",
            target = "productNumber")
    @Mapping(source = "savingAccount.state", target = "status")
    @Mapping(source = "savingAccount.creationDate", target = "created")
    @Mapping(target = "productType" , constant = "ACCOUNT")
    Product toProduct(SavingAccount savingAccount);

    @Mapping(expression = "java(ProductUtils.hideProductNumberDebitCard(debitCard))",
            target = "productNumber")
    @Mapping(source = "cardStatus.status", target = "status")
    @Mapping(source = "cardStatus.statusDate", target = "created")
    @Mapping(target = "productType" , constant = "CARD")
    Product toProduct(DebitCard debitCard, CardStatus cardStatus);

}
