package com.lulobank.clients.starter.adapter.out.debitcards.mapper;

import com.lulobank.clients.services.application.port.out.debitcards.model.CardStatus;
import com.lulobank.clients.services.application.port.out.debitcards.model.DebitCard;
import com.lulobank.clients.starter.adapter.out.debitcards.dto.DebitCardInformation;
import com.lulobank.clients.starter.adapter.out.debitcards.dto.DebitCardStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DebitCardsMapper {
    DebitCardsMapper INSTANCE = Mappers.getMapper(DebitCardsMapper.class);

    DebitCard toDomain(DebitCardInformation debitCardStatus);

    @Mapping(source = "currentCard.status",target = "status")
    @Mapping(source = "cardRequest.statusDate",target = "statusDate")
    CardStatus toDomain(DebitCardStatus debitCardStatus);


}