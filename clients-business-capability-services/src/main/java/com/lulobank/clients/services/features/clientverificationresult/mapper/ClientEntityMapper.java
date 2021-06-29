package com.lulobank.clients.services.features.clientverificationresult.mapper;

import com.lulobank.clients.services.events.ClientVerificationResult;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.utils.StringUtils;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import static com.lulobank.clients.services.utils.IdentityBiometricStatus.FINISHED;

@Mapper(imports = StringUtils.class)
public interface ClientEntityMapper {

    ClientEntityMapper INSTANCE = Mappers.getMapper(ClientEntityMapper.class);

    @Mapping(target = "name",expression = "java(StringUtils.concatNames(clientVerificationResult))")
    @Mapping(target = "lastName",expression = "java(StringUtils.concatLastNames(clientVerificationResult))")
    @Mapping(target = "gender", source = "clientVerificationResult.clientPersonalInformation.gender")
    @Mapping(target = "birthDate", source = "clientVerificationResult.clientPersonalInformation.birthDate",
            dateFormat = "yyyy-MM-dd'T'HH:mm:ss")
    @Mapping(target = "additionalPersonalInformation", source = "clientVerificationResult.clientPersonalInformation.additionalPersonalInformation")
    @Mapping(target = "typeDocument", source = "clientVerificationResult.clientPersonalInformation.idDocument.documentType")
    @Mapping(target = "idCard", source = "clientVerificationResult.clientPersonalInformation.idDocument.idCard")
    @Mapping(target = "dateOfIssue", source = "clientVerificationResult.clientPersonalInformation.idDocument" +
            ".expeditionDate", dateFormat = "yyyy-MM-dd'T'HH:mm:ss")
    @Mapping(target = "identityBiometric.status", expression = "java(ClientEntityMapper.getFinishedStatus())")
    @Mapping(target = "identityBiometric.idTransaction", source = "clientVerificationResult.idTransactionBiometric")
    @Mapping(target = "identityBiometric.transactionState", source = "clientVerificationResult.transactionState")
    @Mapping(target = "blackListDate", source = "clientVerificationResult.blacklist.reportDate")
    @Mapping(target = "blackListState", source = "clientVerificationResult.blacklist.status")
    @Mapping(target = "blackListRiskLevel", source = "clientVerificationResult.blacklist.resultRiskLevel")
    ClientsV3Entity clientEntityFrom(ClientVerificationResult clientVerificationResult, ClientsV3Entity clientEntity);

    @Mapping(target = "identityBiometric.idTransaction", source = "clientVerificationResult.idTransactionBiometric")
    @Mapping(target = "identityBiometric.transactionState", source = "clientVerificationResult.transactionState")
    @Mapping(target = "blackListDate", source = "clientVerificationResult.blacklist.reportDate")
    @Mapping(target = "blackListState", source = "clientVerificationResult.blacklist.status")
    ClientEntity biometricResult(ClientVerificationResult clientVerificationResult, ClientEntity clientEntity);

    static String getFinishedStatus() {
        return FINISHED.name();
    }
}
