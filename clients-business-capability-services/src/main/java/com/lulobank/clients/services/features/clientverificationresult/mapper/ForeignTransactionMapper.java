package com.lulobank.clients.services.features.clientverificationresult.mapper;

import com.lulobank.clients.sdk.operations.dto.economicinformation.ForeignCurrencyTransaction;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ForeignTransactionV3;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ForeignTransactionMapper {

    ForeignTransactionMapper INSTANCE = Mappers.getMapper(ForeignTransactionMapper.class);

    ForeignCurrencyTransaction foreignCurrencyTransactionFromV3(ForeignTransactionV3 clientEntity);
}
