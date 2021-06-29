package com.lulobank.clients.services.features.loanrequested;

import com.lulobank.clients.sdk.operations.dto.ClientLoanRequested;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.outboundadapters.model.LoanClientRequested;
import com.lulobank.clients.services.outboundadapters.model.Result;
import com.lulobank.credits.sdk.dto.initialofferv2.GetOfferToClient;
import com.lulobank.credits.sdk.dto.initialofferv2.RiskEngineAnalysis;
import io.vavr.control.Option;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;



@Mapper
public interface InitialOffersMapper {

    InitialOffersMapper INSTANCE = Mappers.getMapper(InitialOffersMapper.class);

    @Mapping(target = "idClient", source = "clientEntity.idClient")
    @Mapping(target = "clientLoanRequestedAmount", source = "clientLoanRequested.amount")
    @Mapping(target = "loanPurpose", source = "clientLoanRequested.loanPurpose")
    @Mapping(target = "clientInformation.documentId.id", source = "clientEntity.idCard")
    @Mapping(target = "clientInformation.documentId.type", source = "clientEntity.typeDocument")
    @Mapping(target = "clientInformation.documentId.issueDate", source = "clientEntity.dateOfIssue")
    @Mapping(target = "clientInformation.email", source = "clientEntity.emailAddress")
    @Mapping(target = "clientInformation.gender", source = "clientEntity.gender")
    @Mapping(target = "clientInformation.phone.number", source = "clientEntity.phoneNumber")
    @Mapping(target = "clientInformation.phone.prefix", source = "clientEntity.phonePrefix")
    @Mapping(target = "clientInformation.name", source = "clientEntity.additionalPersonalInformation.firstName")
    @Mapping(target = "clientInformation.lastName", source = "clientEntity.additionalPersonalInformation.firstSurname")
    @Mapping(target = "clientInformation.middleName", source = "clientEntity.additionalPersonalInformation.secondName")
    @Mapping(target = "clientInformation.secondSurname", source = "clientEntity.additionalPersonalInformation.secondSurname")
    GetOfferToClient getOfferClientFrom(ClientEntity clientEntity, ClientLoanRequested clientLoanRequested);

    ClientLoanRequested clientLoanRequested(LoanClientRequested loanClientRequested);

    RiskEngineAnalysis riskAnalysisFrom(Result result);

    @AfterMapping
    default void datePayments(ClientEntity clientEntity, @MappingTarget GetOfferToClient getOfferToClient) {
        Option.ofOptional(clientEntity.getCreditRiskAnalysis().getResults().stream().findFirst())
                .peek(result -> getOfferToClient.setRiskEngineAnalysis(INSTANCE.riskAnalysisFrom(result)));
    }
}
