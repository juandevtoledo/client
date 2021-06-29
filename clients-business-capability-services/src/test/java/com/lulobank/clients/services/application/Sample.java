package com.lulobank.clients.services.application;

import com.google.common.collect.ImmutableList;
import com.lulobank.clients.sdk.operations.util.CheckPoints;
import com.lulobank.clients.services.application.port.out.debitcards.model.CardStatus;
import com.lulobank.clients.services.application.port.out.debitcards.model.DebitCard;
import com.lulobank.clients.services.application.port.out.savingsaccounts.model.Balance;
import com.lulobank.clients.services.application.port.out.savingsaccounts.model.SavingAccount;
import com.lulobank.clients.services.domain.DocumentType;
import com.lulobank.clients.services.domain.RiskLevelBlackList;
import com.lulobank.clients.services.domain.StateBlackList;
import com.lulobank.clients.services.domain.activateblacklistedclient.ActivateBlacklistedClientRequest;
import com.lulobank.clients.services.domain.activateblacklistedclient.Blacklist;
import com.lulobank.clients.services.domain.activateblacklistedclient.ClientPersonalInformation;
import com.lulobank.clients.services.domain.activateblacklistedclient.Document;
import com.lulobank.clients.services.domain.activateselfcertifiedpepclient.ActivatePepClientRequest;
import com.lulobank.clients.services.domain.findclientbyidbsc.NotifyAutomaticPaymentRequest;
import com.lulobank.clients.services.domain.productoffers.OfferStatus;
import com.lulobank.clients.services.domain.productoffers.UpdateProductOffersRequest;
import com.lulobank.clients.services.domain.zendeskclientinfo.GetClientInfoByEmailRequest;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ApprovedRiskAnalysisV3;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.OnBoardingStatusV3;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.RiskOfferV3;
import com.lulobank.clients.v3.vo.AdapterCredentials;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.lulobank.clients.services.application.Constant.AUTHORIZATION_HEADER;
import static com.lulobank.clients.services.application.Constant.CREATION_DATE_CARD;
import static com.lulobank.clients.services.application.Constant.CREATION_DATE_SAVING;
import static com.lulobank.clients.services.application.Constant.EXPEDITION_DATE;
import static com.lulobank.clients.services.application.Constant.ID_CARD;
import static com.lulobank.clients.services.application.Constant.ID_CBS;
import static com.lulobank.clients.services.application.Constant.ID_CLIENT;
import static com.lulobank.clients.services.application.Constant.ID_PRODUCT_OFFER;
import static com.lulobank.clients.services.application.Constant.ID_SAVINGS_ACCOUNT;
import static com.lulobank.clients.services.application.Constant.ID_TRANSACTION_BIOMETRIC;
import static com.lulobank.clients.services.application.Constant.MAIL;
import static com.lulobank.clients.services.application.Constant.PAYMENT_STATUS;
import static com.lulobank.clients.services.application.Constant.PHONE_NUMBER;
import static com.lulobank.clients.services.application.Constant.REPORT_DATE_BLACKLIST;
import static com.lulobank.clients.services.application.Constant.VALUE_PAID;
import static com.lulobank.clients.services.application.Constant.WHITELIST_EXPIRATION;
import static com.lulobank.clients.v3.adapters.port.out.dynamo.dto.OfferState.ACTIVE;

public class Sample {

    public static GetClientInfoByEmailRequest getClientInfoByEmailRequest() {
        GetClientInfoByEmailRequest getClientInfoByEmailRequest = new GetClientInfoByEmailRequest(MAIL);
        getClientInfoByEmailRequest.setHttpHeaders(getHeaders());
        return getClientInfoByEmailRequest;
    }

    public static ClientsV3Entity getClientsV3EntityZendesk() {
        ClientsV3Entity entity = new ClientsV3Entity();
        entity.setIdClient(ID_CLIENT);
        entity.setIdCard(ID_CARD);
        entity.setName("name");
        entity.setLastName("lastName");
        entity.setPhoneNumber(PHONE_NUMBER);
        entity.setEmailAddress(MAIL);
        entity.setTypeDocument(DocumentType.CC.name());
        entity.setAddressPrefix("Cll");
        entity.setAddress("12 34");
        return entity;
    }

    public static SavingAccount getSavingAccount() {
        SavingAccount savingAccount = new SavingAccount();
        savingAccount.setState("ACTIVE");
        savingAccount.setType("CURRENT_ACCOUNT");
        savingAccount.setSimpleDeposit(false);
        savingAccount.setGmf(false);
        savingAccount.setIdSavingAccount(ID_SAVINGS_ACCOUNT);
        savingAccount.setCreationDate(CREATION_DATE_SAVING);
        Balance balance = new Balance();
        balance.setAmount(BigDecimal.valueOf(100000));
        balance.setCurrency("COP");
        balance.setAvailableAmount(BigDecimal.valueOf(100000));
        savingAccount.setBalance(balance);
        return savingAccount;
    }

    public static Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION_HEADER, "token");
        return headers;
    }

    public static DebitCard getDebitCard() {
        DebitCard debitCard = new DebitCard();
        debitCard.setCardNumberMask(ID_CARD);
        debitCard.setExpirationDate("08/25");
        debitCard.setHolderName("name");
        debitCard.setColor("LULO");
        debitCard.setFullName("name lastName");
        return debitCard;
    }

    public static CardStatus getDebitCardStatus() {
        CardStatus cardStatus = new CardStatus();
        cardStatus.setStatus("ACTIVE");
        cardStatus.setStatusDate(CREATION_DATE_CARD);
        return cardStatus;

    }

    public static ClientsV3Entity getClientsV3Entity() {
        ClientsV3Entity entity = new ClientsV3Entity();
        entity.setIdClient(ID_CLIENT);
        entity.setIdCard(ID_CARD);
        entity.setName("name");
        entity.setLastName("lastName");
        entity.setPhoneNumber(PHONE_NUMBER);
        entity.setEmailAddress(MAIL);
        entity.setTypeDocument(DocumentType.CC.name());
        entity.setAddressPrefix("Cll");
        entity.setAddress("12 34");
        entity.setValue(3000000);
        OnBoardingStatusV3 onBoardingStatusV3 = new OnBoardingStatusV3();
        onBoardingStatusV3.setCheckpoint(CheckPoints.BLACKLIST_STARTED.name());
        onBoardingStatusV3.setProductSelected("SAVING_ACCOUNT");
        entity.setOnBoardingStatus(onBoardingStatusV3);
        entity.setApprovedRiskAnalysis(new ApprovedRiskAnalysisV3());
        entity.getApprovedRiskAnalysis().setResults(ImmutableList.of(buildRiskOffer()));
        return entity;
    }

    @NotNull
    public static RiskOfferV3 buildRiskOffer() {
        RiskOfferV3 riskOfferV3 = new RiskOfferV3();
        riskOfferV3.setState(ACTIVE);
        riskOfferV3.setType("REGISTRY_PREAPPROVED");
        riskOfferV3.setIdProductOffer(ID_PRODUCT_OFFER);
        riskOfferV3.setOfferDate(LocalDateTime.now().minusHours(12));
        riskOfferV3.setValue(7000000);
        return riskOfferV3;
    }

    public static ClientsV3Entity getClientsV3EntityConfirmPreApproved() {
        ClientsV3Entity entity = new ClientsV3Entity();
        entity.setIdClient(ID_CLIENT);
        entity.setIdCard(ID_CARD);
        entity.setName("name");
        entity.setLastName("lastName");
        entity.setPhoneNumber(PHONE_NUMBER);
        entity.setEmailAddress(MAIL);
        entity.setTypeDocument(DocumentType.CC.name());
        entity.setAddressPrefix("Cll");
        entity.setAddress("12 34");
        entity.setValue(3000000);
        entity.setApprovedRiskAnalysis(new ApprovedRiskAnalysisV3());
        entity.getApprovedRiskAnalysis().setResults(ImmutableList.of(buildConfirmRiskOffer()));
        return entity;
    }

    @NotNull
    public static RiskOfferV3 buildConfirmRiskOffer() {
        RiskOfferV3 riskOfferV3 = new RiskOfferV3();
        riskOfferV3.setState(ACTIVE);
        riskOfferV3.setType("CONFIRM_PREAPPROVED");
        riskOfferV3.setIdProductOffer(ID_PRODUCT_OFFER);
        riskOfferV3.setOfferDate(LocalDateTime.now().minusHours(12));
        riskOfferV3.setValue(7000000);
        return riskOfferV3;
    }

    public static UpdateProductOffersRequest buildUpdateOfferRequest() {
        AdapterCredentials credentials = new AdapterCredentials(new HashMap<>());
        return new UpdateProductOffersRequest(ID_PRODUCT_OFFER, OfferStatus.ACTIVE_HOME, ID_CLIENT, credentials);
    }

    public static NotifyAutomaticPaymentRequest.NotifyAutomaticPaymentRequestBuilder notifyAutomaticPaymentRequestBuilder() {

        return NotifyAutomaticPaymentRequest.builder()
                .paymentStatus(PAYMENT_STATUS)
                .cbsId(ID_CBS)
                .valuePaid(VALUE_PAID);
    }

    public static ActivateBlacklistedClientRequest getActivateBlacklistedClientRequest(){
        return ActivateBlacklistedClientRequest.builder()
                .idTransactionBiometric(ID_TRANSACTION_BIOMETRIC)
                .blacklist(Blacklist.builder()
                        .status(StateBlackList.WHITELISTED)
                        .riskLevel(RiskLevelBlackList.MID_RISK.getLevel())
                        .reportDate(REPORT_DATE_BLACKLIST)
                        .build())
                .clientPersonalInformation(ClientPersonalInformation.builder()
                        .document(Document.builder()
                                .documentType(DocumentType.CC.name())
                                .idCard(ID_CARD)
                                .expeditionDate(EXPEDITION_DATE)
                                .build())
                        .build())
                .whitelistExpirationDate(WHITELIST_EXPIRATION)
                .build();
    }

    public static ActivatePepClientRequest getActivateSelfCertifiedPEPClientRequest(){
        return ActivatePepClientRequest.builder()
                .whitelisted(true)
                .clientPersonalInformation(com.lulobank.clients.services.domain.activateselfcertifiedpepclient.ClientPersonalInformation.builder()
                        .document(com.lulobank.clients.services.domain.activateselfcertifiedpepclient.Document.builder()
                                .cardId(ID_CARD)
                                .documentType(DocumentType.CC.name())
                                .build())
                        .build())
                .whitelistExpirationDate(WHITELIST_EXPIRATION)
                .build();
    }

    public static ActivatePepClientRequest getActivateSelfCertifiedPEPClientRequestPepBlacklisted(){
        return ActivatePepClientRequest.builder()
                .whitelisted(false)
                .clientPersonalInformation(com.lulobank.clients.services.domain.activateselfcertifiedpepclient.ClientPersonalInformation.builder()
                        .document(com.lulobank.clients.services.domain.activateselfcertifiedpepclient.Document.builder()
                                .cardId(ID_CARD)
                                .documentType(DocumentType.CC.name())
                                .build())
                        .build())
                .whitelistExpirationDate(WHITELIST_EXPIRATION)
                .build();
    }
}
