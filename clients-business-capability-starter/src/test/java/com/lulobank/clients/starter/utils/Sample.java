package com.lulobank.clients.starter.utils;

import com.google.common.collect.ImmutableList;
import com.lulobank.clients.sdk.operations.util.CheckPoints;
import com.lulobank.clients.services.application.util.HttpDomainStatus;
import com.lulobank.clients.services.domain.DocumentType;
import com.lulobank.clients.services.domain.error.UseCaseResponseError;
import com.lulobank.clients.services.domain.productoffers.ClientProductOffer;
import com.lulobank.clients.services.domain.productoffers.ProductOffer;
import com.lulobank.clients.starter.v3.adapters.in.dto.ClientFatcaRequest;
import com.lulobank.clients.starter.v3.adapters.in.dto.CreateAddressRequest;
import com.lulobank.clients.starter.v3.adapters.in.dto.CreateAddressResponse;
import com.lulobank.clients.starter.v3.adapters.in.dto.GetClientFatcaInfoResponse;
import com.lulobank.clients.starter.v3.adapters.in.dto.UpdateCheckpointRequest;
import com.lulobank.clients.starter.v3.adapters.in.dto.UpdateCheckpointResponse;
import com.lulobank.clients.starter.v3.adapters.in.notification.dto.NotificationDisabledAdapterRequest;
import com.lulobank.clients.starter.v3.adapters.out.dynamo.dto.ApprovedRiskAnalysis;
import com.lulobank.clients.starter.v3.adapters.out.dynamo.dto.ClientEntity;
import com.lulobank.clients.starter.v3.adapters.out.dynamo.dto.RiskOffer;
import com.lulobank.clients.v3.usecase.command.ClientAddressData;
import com.lulobank.clients.v3.usecase.command.GetClientFatcaResponse;
import com.lulobank.clients.v3.usecase.command.UpdateCheckpointInfo;
import com.lulobank.clients.v3.usecase.notification.NotificationDisabledType;

import java.time.LocalDateTime;

import static com.lulobank.clients.starter.adapter.Constant.*;
import static com.lulobank.clients.v3.error.UseCaseErrorStatus.CLI_180;

public class Sample {

    public static ClientEntity getClientsEntity() {
        ClientEntity entity = new ClientEntity();
        entity.setIdClient(ID_CLIENT);
        entity.setIdCard(ID_CARD);
        entity.setName("name");
        entity.setLastName("lastName");
        entity.setPhoneNumber(PHONE_NUMBER);
        entity.setEmailAddress(MAIL);
        entity.setTypeDocument(DocumentType.CC.name());
        entity.setAddressPrefix("Cll");
        entity.setAddress("12 34");
        entity.setApprovedRiskAnalysis(new ApprovedRiskAnalysis());
        entity.getApprovedRiskAnalysis().setResults(ImmutableList.of(buildRiskOffer()));
        return entity;
    }

    public static RiskOffer buildRiskOffer() {
        RiskOffer riskOfferV3 = new RiskOffer();
        riskOfferV3.setState("ACTIVE");
        riskOfferV3.setType("REGISTRY_PREAPPROVED");
        riskOfferV3.setInterestRate(16.5f);
        riskOfferV3.setMonthlyNominalRate(1.5f);
        riskOfferV3.setInstallments(48);
        riskOfferV3.setIdProductOffer("50bcb86b-cfd4-443d-9c38-7c50123997b7");
        riskOfferV3.setAmount(30000000d);
        riskOfferV3.setOfferDate(LocalDateTime.now().minusHours(12));
        return riskOfferV3;
    }

    public static ClientProductOffer getClientProductOffer() {
        ProductOffer productOffer = ProductOffer.builder()
                .idProductOffer("50bcb86b-cfd4-443d-9c38-7c50123997b7")
                .state("ACTIVE")
                .type("REGISTRY_PREAPPROVED")
                .build();
        return new ClientProductOffer(ImmutableList.of(productOffer));
    }

    public static NotificationDisabledAdapterRequest getNotificationDisabledRequest() {
        NotificationDisabledAdapterRequest notificationDisabledRequest = new NotificationDisabledAdapterRequest();
        notificationDisabledRequest.setNotificationType(NotificationDisabledType.PUSH_NOTIFICATIONS_DISABLED);
        return notificationDisabledRequest;
    }

    public static UseCaseResponseError getInternalServerError() {
        return new UseCaseResponseError(CLI_180.name(),
                String.valueOf(HttpDomainStatus.INTERNAL_SERVER_ERROR.value()));
    }

    public static ClientFatcaRequest buildClientFatcaRequest() {
        ClientFatcaRequest clientFatcaRequest = new ClientFatcaRequest();
        clientFatcaRequest.setCountryCode("57");
        clientFatcaRequest.setFatcaResponsibility(true);
        clientFatcaRequest.setTin("TIN_NUMBER_TEST");
        clientFatcaRequest.setTinObservation("TIN_OBSERVATION_TEST");
        return clientFatcaRequest;
    }

    public static GetClientFatcaInfoResponse buildGetClientFatcaInfoResponse() {
        GetClientFatcaInfoResponse response = new GetClientFatcaInfoResponse();
        response.setCountryCode("COUNTRY_CODE_TEST");
        response.setDeclaredDate(LocalDateTime.now());
        response.setFatcaResponsibility(true);
        response.setStatus("STATUS_TEST");
        response.setTin("TIN_NUMBER_TEST");
        response.setTinObservation("TIN_OBSERVATION_TEST");
        return response;
    }

    public static GetClientFatcaResponse buildGetFatcaInfoResponse() {
        return GetClientFatcaResponse.builder()
                .tinObservation("TIN_OBSERVATION_TEST")
                .tin("TIN_NUMBER_TEST")
                .status("STATUS_TEST")
                .fatcaResponsibility(true)
                .declaredDate(LocalDateTime.now())
                .countryCode("COUNTRY_CODE_TEST")
                .build();
    }

    public static CreateAddressRequest buildCreateAddressRequest(){
        CreateAddressRequest createAddressRequest = new CreateAddressRequest();
        createAddressRequest.setAddress(ADDRESS);
        createAddressRequest.setAddressPrefix(ADDRESS_PREFIX);
        createAddressRequest.setAddressComplement(ADDRESS_COMPLEMENT);
        createAddressRequest.setCity(CITY);
        createAddressRequest.setCityId(CITY_ID);
        createAddressRequest.setDepartment(DEPARTMENT);
        createAddressRequest.setDepartmentId(DEPARTMENT_ID);
        createAddressRequest.setCode(CODE);
        return createAddressRequest;
    }

    public static CreateAddressResponse buildCreateAddressResponse(){
        CreateAddressResponse createAddressResponse = new CreateAddressResponse();
        createAddressResponse.setAddress(ADDRESS);
        createAddressResponse.setAddressPrefix(ADDRESS_PREFIX);
        createAddressResponse.setAddressComplement(ADDRESS_COMPLEMENT);
        createAddressResponse.setCity(CITY);
        createAddressResponse.setCityId(CITY_ID);
        createAddressResponse.setDepartment(DEPARTMENT);
        createAddressResponse.setDepartmentId(DEPARTMENT_ID);
        createAddressResponse.setCode(CODE);
        return createAddressResponse;
    }

    public static ClientAddressData buildClientAddressData(){
        return ClientAddressData.builder()
                .idClient(ID_CLIENT)
                .address(ADDRESS)
                .addressPrefix(ADDRESS_PREFIX)
                .addressComplement(ADDRESS_COMPLEMENT)
                .city(CITY)
                .cityId(CITY_ID)
                .department(DEPARTMENT)
                .departmentId(DEPARTMENT_ID)
                .code(CODE)
                .build();
    }
    
    public static UpdateCheckpointRequest buildUpdateCheckpointRequest(){
        return new UpdateCheckpointRequest();
    }

    public static UpdateCheckpointResponse buildUpdateCheckpointResponse(){
        return new UpdateCheckpointResponse();
    }

    public static UpdateCheckpointInfo buildUpdateCheckpointInfo(CheckPoints checkpoint){
        return UpdateCheckpointInfo.builder()
                .checkpoint(checkpoint)
                .build();
    }
}
