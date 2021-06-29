package com.lulobank.clients.services.features.identitybiometric.mapper;

import com.lulobank.clients.services.features.identitybiometric.model.UpdateIdTransactionBiometric;
import com.lulobank.clients.services.outboundadapters.model.IdentityBiometric;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import static com.lulobank.clients.services.utils.IdentityBiometricStatus.IN_PROGRESS;

@Mapper
public interface IdentityBiometricMapper {

    IdentityBiometricMapper INSTANCE= Mappers.getMapper(IdentityBiometricMapper.class);

    @Mappings(value ={
            @Mapping(target = "idTransaction",source = "idTransactionBiometric.idTransactionBiometric"),
            @Mapping(target = "status",expression = "java(IdentityBiometricMapper.getStatus())"),
            @Mapping(target = "transactionState", ignore = true)
    })
    IdentityBiometric identityBiometricFrom(UpdateIdTransactionBiometric idTransactionBiometric);

    static String getStatus(){
        return IN_PROGRESS.name();
    }
}
