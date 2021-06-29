package com.lulobank.clients.services.features.clientverificationresult.mapper;

import com.lulobank.clients.services.events.ClientVerificationResult;
import com.lulobank.clients.services.events.TransactionState;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.IdentityBiometricV3;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.TransactionStateV3;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import static com.lulobank.clients.services.utils.IdentityBiometricStatus.IN_PROGRESS;

@Mapper
public interface IdentityBiometricMapper {

    IdentityBiometricMapper INSTANCE= Mappers.getMapper(IdentityBiometricMapper.class);

    @Mappings(value ={
            @Mapping(target = "idTransaction",source = "clientVerificationResult.idTransactionBiometric"),
            @Mapping(target = "status",expression = "java(IdentityBiometricMapper.getStatus())"),
            @Mapping(target = "transactionState", ignore = true)
    })
    IdentityBiometricV3 identityBiometricFrom(ClientVerificationResult clientVerificationResult);

    TransactionStateV3 transactionStateV3From(TransactionState transactionState);

    static String getStatus(){
        return IN_PROGRESS.name();
    }
}
