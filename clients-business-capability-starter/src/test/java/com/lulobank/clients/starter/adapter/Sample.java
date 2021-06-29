package com.lulobank.clients.starter.adapter;

import com.lulobank.clients.services.domain.zendeskclientinfo.Customer;
import com.lulobank.clients.services.domain.zendeskclientinfo.GetClientInfoByEmailResponse;
import com.lulobank.clients.services.domain.zendeskclientinfo.Product;
import com.lulobank.clients.starter.adapter.out.debitcards.dto.CardRequestStatus;
import com.lulobank.clients.starter.adapter.out.debitcards.dto.CurrentCardStatus;
import com.lulobank.clients.starter.adapter.out.debitcards.dto.DebitCardInformation;
import com.lulobank.clients.starter.adapter.out.debitcards.dto.DebitCardStatus;
import com.lulobank.clients.starter.adapter.out.debitcards.dto.ResponseDebitCardInformation;
import com.lulobank.clients.starter.adapter.out.debitcards.dto.ResponseDebitCardStatus;
import com.lulobank.clients.starter.adapter.out.savingsaccounts.dto.ResponseSavingAccountType;
import com.lulobank.clients.starter.adapter.out.savingsaccounts.dto.SavingAccountBalance;
import com.lulobank.clients.starter.adapter.out.savingsaccounts.dto.SavingAccountType;
import com.lulobank.clients.starter.adapter.out.transactions.dto.PendingTransferDto;
import com.lulobank.clients.starter.adapter.out.transactions.dto.PendingTransfersDto;
import com.lulobank.clients.starter.v3.adapters.out.dynamo.dto.ClientEntity;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.clients.v3.adapters.port.out.messaging.dto.NotificationCategory;
import com.lulobank.clients.v3.adapters.port.out.messaging.dto.NotificationDisabledTypeMessage;
import joptsimple.internal.Strings;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.lulobank.clients.services.domain.DocumentType.CC;
import static com.lulobank.clients.starter.adapter.Constant.AUTHORIZATION_HEADER;
import static com.lulobank.clients.starter.adapter.Constant.CREATION_DATE_CARD;
import static com.lulobank.clients.starter.adapter.Constant.CREATION_DATE_SAVING;
import static com.lulobank.clients.starter.adapter.Constant.DOCUMENT_TYPE;
import static com.lulobank.clients.starter.adapter.Constant.ID_CARD;
import static com.lulobank.clients.starter.adapter.Constant.ID_CLIENT;
import static com.lulobank.clients.starter.adapter.Constant.ID_SAVINGS_ACCOUNT;
import static com.lulobank.clients.starter.adapter.Constant.MAIL;
import static com.lulobank.clients.starter.adapter.Constant.PHONE_NUMBER;
import static com.lulobank.clients.starter.adapter.Constant.NAME;
import static com.lulobank.clients.starter.adapter.Constant.LAST_NAME;
import static com.lulobank.clients.starter.adapter.Constant.PHONE;
import static com.lulobank.clients.starter.adapter.Constant.PREFIX;
import static com.lulobank.clients.starter.adapter.Constant.DATE_ISSUE_ENTITY;
import static com.lulobank.clients.starter.adapter.Constant.BIRTH_DATE_ENTITY;
import static com.lulobank.clients.starter.adapter.Constant.GENDER;
import static com.lulobank.clients.starter.adapter.Constant.EMAIL;


public class Sample {
    public static ClientEntity getClientEntity() {
        ClientEntity entity = new ClientEntity();
        entity.setIdClient(ID_CLIENT);
        return entity;
    }

    public static ResponseSavingAccountType getResponseSavingAccountType() {
        ResponseSavingAccountType responseSavingAccountType = new ResponseSavingAccountType();
        responseSavingAccountType.setContent(getSavingAccountType());
        return responseSavingAccountType;
    }

    public static PendingTransfersDto getResponsePendingTransactions() {
        PendingTransferDto pendingTransferDto = new PendingTransferDto();
        ArrayList<PendingTransferDto> list = new ArrayList<>();
        list.add(pendingTransferDto);
        PendingTransfersDto pendingTransfersDto = new PendingTransfersDto();
        pendingTransfersDto.setContent(list);
        return pendingTransfersDto;
    }

    public static PendingTransfersDto getResponseNotPendingTransactions() {
        PendingTransfersDto pendingTransfersDto = new PendingTransfersDto();
        pendingTransfersDto.setContent(new ArrayList<>());
        return pendingTransfersDto;
    }

    private static SavingAccountType getSavingAccountType() {
        SavingAccountType savingAccountType = new SavingAccountType();
        savingAccountType.setIdSavingAccount(ID_SAVINGS_ACCOUNT);
        savingAccountType.setState("ACTIVE");
        savingAccountType.setGmf(false);
        savingAccountType.setSimpleDeposit(false);
        savingAccountType.setCreationDate(CREATION_DATE_SAVING);
        savingAccountType.setType("CURRENT_ACCOUNT");
        savingAccountType.setBalance(getSavingAccountBalance());
        return savingAccountType;
    }

    private static SavingAccountBalance getSavingAccountBalance() {
        SavingAccountBalance savingAccountBalance = new SavingAccountBalance();
        savingAccountBalance.setAmount(BigDecimal.valueOf(100000));
        savingAccountBalance.setCurrency("COP");
        savingAccountBalance.setAvailableAmount(BigDecimal.valueOf(100000));
        return savingAccountBalance;
    }

    public static Map<String, String> getAuthorizationHeader() {
        Map<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION_HEADER, "token");
        return headers;
    }

    public static ResponseDebitCardInformation getResponseDebitCardInformation() {
        ResponseDebitCardInformation responseDebitCardInformation = new ResponseDebitCardInformation();
        responseDebitCardInformation.setContent(getDebitCardInformation());
        return responseDebitCardInformation;
    }

    private static DebitCardInformation getDebitCardInformation() {
        DebitCardInformation debitCardInformation = new DebitCardInformation();
        debitCardInformation.setCardNumberMask(ID_CARD);
        debitCardInformation.setExpirationDate("08/25");
        debitCardInformation.setHolderName("name");
        debitCardInformation.setFullName("name lastName");
        debitCardInformation.setColor("LULO");
        return debitCardInformation;
    }

    public static ResponseDebitCardStatus getResponseDebitCardStatus() {
        ResponseDebitCardStatus responseDebitCardStatus = new ResponseDebitCardStatus();
        responseDebitCardStatus.setContent(getDebitCardStatus());
        return responseDebitCardStatus;
    }

    private static DebitCardStatus getDebitCardStatus() {
        DebitCardStatus debitCardStatus = new DebitCardStatus();
        debitCardStatus.setCurrentCard(getCurrentCardStatus());
        debitCardStatus.setCardRequest(getCardRequestStatus());
        return debitCardStatus;
    }

    private static CardRequestStatus getCardRequestStatus() {
        CardRequestStatus cardRequestStatus = new CardRequestStatus();
        cardRequestStatus.setStatus("CREATED");
        cardRequestStatus.setStatusDate(CREATION_DATE_CARD);
        cardRequestStatus.setDetail(Strings.EMPTY);
        return cardRequestStatus;
    }

    private static CurrentCardStatus getCurrentCardStatus() {
        CurrentCardStatus currentCardStatus = new CurrentCardStatus();
        currentCardStatus.setDetail(Strings.EMPTY);
        currentCardStatus.setStatus("ACTIVE");
        return currentCardStatus;

    }

    public static GetClientInfoByEmailResponse getClientInfoByEmailResponse() {
        GetClientInfoByEmailResponse getClientInfoByEmailResponse = new GetClientInfoByEmailResponse();
        getClientInfoByEmailResponse.setCustomer(getCustomer());
        getClientInfoByEmailResponse.setProducts(getProducts());
        return getClientInfoByEmailResponse;
    }

    private static List<Product> getProducts() {

        List<Product> productList = new ArrayList<>();
        Product p1 = new Product();
        p1.setStatus("ACTIVE");
        p1.setProductNumber(ID_SAVINGS_ACCOUNT.substring(ID_SAVINGS_ACCOUNT.length() - 4));
        p1.setCreated(CREATION_DATE_SAVING);
        p1.setProductType("ACCOUNT");
        productList.add(p1);
        Product p2 = new Product();
        p2.setStatus("ACTIVE");
        p2.setProductNumber(ID_CARD.substring(ID_CARD.length() - 4));
        p2.setCreated(CREATION_DATE_CARD);
        p2.setProductType("CARD");
        productList.add(p2);
        return productList;
    }

    private static Customer getCustomer() {
        Customer customer = new Customer();
        customer.setEmail(MAIL);
        customer.setMobilePhone(PHONE_NUMBER);
        customer.setDocumentNumber(ID_CARD);
        customer.setDocumentType(DOCUMENT_TYPE);
        customer.setAddress("address");
        customer.setName("name");
        customer.setLastName("lastName");
        return customer;
    }

    public static NotificationDisabledTypeMessage getNotificationDisabledTypeMessage(){
        return NotificationDisabledTypeMessage.builder()
                .category(NotificationCategory.SECURITY)
                .description("Descripcion Notification Disabled")
                .idClient(ID_CLIENT)
                .dateNotification(LocalDateTime.now().toString())
                .operation("Operation Notification Disabled")
                .title("Title Notification").build();
    }

    public static final ClientsV3Entity clientEntityV3Builder() {
        ClientsV3Entity clientEntity = new ClientsV3Entity();
        clientEntity.setIdClient(ID_CLIENT);
        clientEntity.setIdCard(ID_CARD);
        clientEntity.setName(NAME);
        clientEntity.setLastName(LAST_NAME);
        clientEntity.setPhoneNumber(PHONE);
        clientEntity.setPhonePrefix(PREFIX);
        clientEntity.setDateOfIssue(LocalDate.parse(DATE_ISSUE_ENTITY));
        clientEntity.setBirthDate(LocalDate.parse(BIRTH_DATE_ENTITY));
        clientEntity.setGender(GENDER);
        clientEntity.setTypeDocument(CC.name());
        clientEntity.setEmailAddress(EMAIL);
        return clientEntity;
    }
}
