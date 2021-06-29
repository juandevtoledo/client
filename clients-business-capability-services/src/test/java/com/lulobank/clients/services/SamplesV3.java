package com.lulobank.clients.services;

import com.lulobank.clients.sdk.operations.dto.economicinformation.OccupationType;
import com.lulobank.clients.sdk.operations.util.CheckPoints;
import com.lulobank.clients.services.domain.notification.NotificationDisabledRequest;
import com.lulobank.clients.services.utils.BiometricResultCodes;
import com.lulobank.clients.services.utils.ProductTypeEnum;
import com.lulobank.clients.v3.adapters.port.out.digitalevidence.dto.DigitalEvidenceDocuments;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.AdditionalPersonalInfoV3;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientAcceptanceV3;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.CompanyV3;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.EconomicInformationV3;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.FatcaInformationV3;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.IdentityBiometricV3;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.OnBoardingStatusV3;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.TransactionStateV3;
import com.lulobank.clients.v3.adapters.port.out.messaging.dto.NotificationDisabledDetails;
import com.lulobank.clients.v3.usecase.command.ClientAddressData;
import com.lulobank.clients.v3.usecase.command.ClientFatcaInformation;
import com.lulobank.clients.v3.usecase.command.UpdateCheckpointInfo;
import com.lulobank.clients.v3.usecase.command.UpdateEmailAddress;
import com.lulobank.clients.v3.usecase.notification.NotificationDisabledType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.lulobank.clients.sdk.operations.dto.economicinformation.OccupationType.RETIRED;
import static com.lulobank.clients.sdk.operations.util.CheckPoints.ON_BOARDING;
import static com.lulobank.clients.services.Constants.*;
import static com.lulobank.clients.services.domain.DocumentType.CC;

public final class SamplesV3 {

    public static EconomicInformationV3 economicInformationV3Builder(
            OccupationType occupation) {
        EconomicInformationV3 economicInformation = new EconomicInformationV3();
        economicInformation.setAdditionalIncome(BigDecimal.valueOf(10000000));
        economicInformation.setAssets(BigDecimal.valueOf(20000000));
        economicInformation.setLiabilities(BigDecimal.valueOf(30000000));
        economicInformation.setMonthlyIncome(BigDecimal.valueOf(40000000));
        economicInformation.setMonthlyOutcome(BigDecimal.valueOf(50000000));
        economicInformation.setSavingPurpose("Purpose Test");
        economicInformation.setEconomicActivity("2029");
        economicInformation.setTypeSaving("Type Test");
        economicInformation.setCompany(new CompanyV3());
        economicInformation.getCompany().setCity("Bogota");
        economicInformation.getCompany().setName("Lulobank");
        economicInformation.getCompany().setState("Bogota");
        economicInformation.setOccupationType(occupation.name());
        return economicInformation;
    }

    public static final IdentityBiometricV3 identityBiometricV3Builder(String idTransaction, String status) {
        IdentityBiometricV3 identityBiometric = new IdentityBiometricV3();
        identityBiometric.setIdTransaction(idTransaction);
        identityBiometric.setStatus(status);
        return identityBiometric;
    }

    public static final OnBoardingStatusV3 onBoardingStatusV3Builder(ProductTypeEnum type) {
        OnBoardingStatusV3 onBoardingStatus = new OnBoardingStatusV3();
        onBoardingStatus.setCheckpoint(ON_BOARDING.name());
        onBoardingStatus.setProductSelected(type.name());
        return onBoardingStatus;
    }

    public static final ClientsV3Entity clientEntityV3Builder() {
        AdditionalPersonalInfoV3 additionalPersonalInfoV3 =new AdditionalPersonalInfoV3();
        additionalPersonalInfoV3.setFirstName(NAME);
        additionalPersonalInfoV3.setFirstSurname(LAST_NAME);
        additionalPersonalInfoV3.setSecondName("");
        additionalPersonalInfoV3.setSecondName("");
        additionalPersonalInfoV3.setPlaceBirth("");

        ClientsV3Entity clientEntity = new ClientsV3Entity();
        clientEntity.setIdClient(ID_CLIENT);
        clientEntity.setIdCard(ID_CARD);
        clientEntity.setEconomicInformation(economicInformationV3Builder(RETIRED));
        clientEntity.setName(NAME);
        clientEntity.setLastName(LAST_NAME);
        clientEntity.setPhoneNumber(PHONE);
        clientEntity.setPhonePrefix(PREFIX);
        clientEntity.setDateOfIssue(LocalDate.parse(DATE_ISSUE_ENTITY));
        clientEntity.setBirthDate(LocalDate.parse(BIRTH_DATE_ENTITY));
        clientEntity.setGender(GENDER);
        clientEntity.setTypeDocument(CC.name());
        clientEntity.setEmailAddress(EMAIL);
        clientEntity.setOnBoardingStatus(onBoardingStatusV3Builder(ProductTypeEnum.CREDIT_ACCOUNT));
        clientEntity.setFatcaInformation(buildFatcaInformation());
        clientEntity.setAdditionalPersonalInformation(additionalPersonalInfoV3);
        return clientEntity;
    }

    private static FatcaInformationV3 buildFatcaInformation() {
        FatcaInformationV3 fatcaInformationV3 = new FatcaInformationV3();
        fatcaInformationV3.setFatcaResponsibility(true);
        fatcaInformationV3.setStatus("STATUS_TEST");
        fatcaInformationV3.setCountryCode("COUNTRY_CODE_TEST");
        fatcaInformationV3.setDeclaredDate(LocalDateTime.now());
        fatcaInformationV3.setTin("TIN_NUMBER_TEST");
        fatcaInformationV3.setTinObservation("TIN_OBSERVATION_TEST");
        return fatcaInformationV3;
    }

    public static TransactionStateV3 transactionStateV3Builder(BiometricResultCodes code) {
        TransactionStateV3 transactionState = new TransactionStateV3();
        transactionState.setStateName(code.name());
        transactionState.setId(code.getCode());
        return transactionState;
    }

    public static final ClientsV3Entity clientEntityV3Builder(OnBoardingStatusV3 onBoardingStatus, IdentityBiometricV3 identityBiometric) {
        ClientsV3Entity clientEntity = new ClientsV3Entity();
        clientEntity.setIdClient(ID_CLIENT);
        clientEntity.setIdCard(ID_CARD);
        clientEntity.setEconomicInformation(economicInformationV3Builder(RETIRED));
        clientEntity.setOnBoardingStatus(onBoardingStatus);
        clientEntity.setIdentityBiometric(identityBiometric);
        clientEntity.setPhoneNumber(Constants.PHONE);
        clientEntity.setPhonePrefix(Constants.PREFIX);
        clientEntity.setName(NAME);
        clientEntity.setLastName(LAST_NAME);
        clientEntity.setGender(GENDER);
        clientEntity.setTypeDocument(TYPE_DOCUMENT);
        clientEntity.setBirthDate(LocalDate.parse(BIRTH_DATE_ENTITY));
        clientEntity.setDateOfIssue(LocalDate.parse(DATE_ISSUE_ENTITY));
        clientEntity.setEmailAddress(EMAIL);
        ClientAcceptanceV3 clientAcceptanceV3 = new ClientAcceptanceV3(LocalDateTime.now());
        clientEntity.setAcceptances(clientAcceptanceV3);
        return clientEntity;
    }

    public static NotificationDisabledDetails getNotificationDisabledDetails() {
        NotificationDisabledDetails notificationDisabledDetails = new NotificationDisabledDetails();
        notificationDisabledDetails.setDescription("Notification Description");
        notificationDisabledDetails.setTitle("Notification Title");
        return notificationDisabledDetails;
    }

    public static NotificationDisabledRequest getNotificationDisabledRequest() {
        NotificationDisabledRequest notificationDisabledRequest = new NotificationDisabledRequest();
        notificationDisabledRequest.setNotificationType(NotificationDisabledType.PUSH_NOTIFICATIONS_DISABLED);
        notificationDisabledRequest.setIdClient(ID_CLIENT);
        return notificationDisabledRequest;
    }

    public static ClientFatcaInformation buildClientFatcaInformation(boolean fatcaResponsibility) {
        return ClientFatcaInformation.builder()
                .countryCode("57")
                .fatcaResponsibility(fatcaResponsibility)
                .idClient(ID_CLIENT)
                .tin("TIN_NUMBER_TEST")
                .tinObservation("TIN_OBSERVATION_TEST")
                .build();
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
                .build();
    }

    public static UpdateCheckpointInfo buildUpdateCheckpointInfo(String clientId, CheckPoints checkpoint){
        return UpdateCheckpointInfo.builder()
                .clientId(clientId)
                .checkpoint(checkpoint)
                .build();
    }

    public static DigitalEvidenceDocuments buildDigitalEvidenceDocuments(){
        return DigitalEvidenceDocuments.builder()
                .response(true)
                .build();
    }

    public static UpdateEmailAddress buildUpdateEmailAddress() {
        return UpdateEmailAddress.builder()
                .idClient(ID_CLIENT)
                .newEmail(EMAIL)
                .build();
    }
}
