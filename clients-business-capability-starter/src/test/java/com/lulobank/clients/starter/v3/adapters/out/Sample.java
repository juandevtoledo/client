package com.lulobank.clients.starter.v3.adapters.out;

import com.lulobank.clients.services.domain.RiskLevelBlackList;
import com.lulobank.clients.services.domain.StateBlackList;
import com.lulobank.clients.services.utils.ProductTypeEnum;
import com.lulobank.clients.starter.v3.adapters.in.dto.UpdateEmailAddressRequest;
import com.lulobank.clients.v3.adapters.port.out.digitalevidence.dto.DigitalEvidenceRequest;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientAcceptanceV3;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.OnBoardingStatusV3;
import com.lulobank.clients.v3.usecase.pep.PepStatus;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.OnBoardingStatusV3;
import com.lulobank.clients.v3.util.DigitalEvidenceTypes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.lulobank.clients.sdk.operations.util.CheckPoints.ON_BOARDING;
import static com.lulobank.clients.starter.v3.adapters.out.Constant.EMAIL;
import static com.lulobank.clients.starter.v3.adapters.out.Constant.ID_CARD;

public class Sample {

    public static final String CLIENT_ID = UUID.randomUUID().toString();
    public static final String MAIL = "mail@mail.com";
    public static final String DATE = "2020-11-18T11:37:04";
    public static final Integer PHONE_PREFIX = Integer.valueOf("57");
    public static final String PHONE_NUMBER = "3002365874";
    public static final String SAVING_ACCOUNT_ID = "0698745223";
    public static final String ID_CBS = "1020365897";
    public static final String CLIENT_NAME = "Maria Conchita Alonso";
    public static final String TOKEN  = "Bearer eyJlbmMiOiJBMjU2R0NNIiwiY.HzL7pNp2Y6lY1umVFQYlCA";
    public static final String AUTHORIZATION = "Authorization";

    public static ClientsV3Entity getClientsV3Entity(){
        ClientsV3Entity entity = new ClientsV3Entity();
        entity.setIdClient(CLIENT_ID);
        entity.setEmailAddress(MAIL);
        entity.setBlackListDate(LocalDateTime.parse(DATE));
        entity.setBlackListRiskLevel(RiskLevelBlackList.HIGH_RISK.getLevel());
        entity.setBlackListState(StateBlackList.BLACKLISTED.name());
        entity.setOnBoardingStatus(onBoardingStatusV3Builder(ProductTypeEnum.CREDIT_ACCOUNT));
        entity.setPep(PepStatus.PEP_WHITELISTED.value());
        entity.setDateResponsePep(LocalDateTime.parse(DATE));
        entity.setIdSavingAccount(SAVING_ACCOUNT_ID);
        entity.setPhonePrefix(PHONE_PREFIX);
        entity.setPhoneNumber(PHONE_NUMBER);
        entity.setIdCbs(ID_CBS);
        entity.setName(CLIENT_NAME);
        entity.setAcceptances(buildAcceptances());
        return entity;
    }

    public static OnBoardingStatusV3 onBoardingStatusV3Builder(ProductTypeEnum type) {
        OnBoardingStatusV3 onBoardingStatus = new OnBoardingStatusV3();
        onBoardingStatus.setCheckpoint(ON_BOARDING.name());
        onBoardingStatus.setProductSelected(type.name());
        return onBoardingStatus;
    }

    public static Map<String,String> getHeaders(){
        Map<String,String> headers  = new HashMap<>();
        headers.put(AUTHORIZATION,TOKEN);
        return headers;
    }

    public static ClientAcceptanceV3 buildAcceptances(){
        return new ClientAcceptanceV3(LocalDateTime.parse(DATE));
    }
    public static DigitalEvidenceRequest getDigitalEvidenceRequest(){
        return DigitalEvidenceRequest.builder()
                .idCard(ID_CARD)
                .name("FirstName")
                .lastName("LastName")
                .emailAddress(EMAIL)
                .acceptanceTimestamp(LocalDateTime.now())
                .evidenceType(DigitalEvidenceTypes.APP).build();
    }

    public static UpdateEmailAddressRequest buildUpdateEmailAddressRequest() {
        UpdateEmailAddressRequest request = new UpdateEmailAddressRequest();
        request.setNewEmail(EMAIL);
        return request;
    }

}
