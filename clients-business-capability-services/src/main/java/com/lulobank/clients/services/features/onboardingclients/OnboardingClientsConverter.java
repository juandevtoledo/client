package com.lulobank.clients.services.features.onboardingclients;

import com.lulobank.clients.services.events.IdentityInformation;
import com.lulobank.clients.services.features.initialclient.model.CreateInitialClient;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.utils.ClientVerificationFirebase;
import com.lulobank.clients.services.utils.LoanRequestedStatusFirebase;
import com.lulobank.clients.services.utils.StatusClientVerificationFirebaseEnum;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientAcceptanceV3;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.OnBoardingStatusV3;
import com.lulobank.savingsaccounts.sdk.dto.createsavingsaccount.ClientInformation;
import com.lulobank.savingsaccounts.sdk.dto.createsavingsaccount.CreateSavingsAccountRequest;
import com.lulobank.savingsaccounts.sdk.dto.createsavingsaccount.DocumentId;
import com.lulobank.savingsaccounts.sdk.dto.createsavingsaccount.Phone;
import com.lulobank.utils.exception.ServiceException;
import org.apache.commons.lang3.LocaleUtils;
import org.springframework.http.HttpStatus;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

import static com.lulobank.clients.services.utils.BiometricResultCodes.isCodeFailedBiometricFraud;
import static com.lulobank.clients.services.utils.DatesUtil.getLocalDateTimeGMT5FromTimestamp;
import static com.lulobank.clients.services.utils.StatusClientVerificationFirebaseEnum.KO_IDENTITY;
import static com.lulobank.clients.services.utils.StatusClientVerificationFirebaseEnum.KO_IDENTITY_FRAUD;
import static com.lulobank.core.utils.OnboardingStatus.ON_BOARDING;

public class OnboardingClientsConverter {

  private OnboardingClientsConverter() {}

  public static CreateSavingsAccountRequest getCreateSavingsAccountRequestFromEntity(
      ClientEntity clientEntity) {
    CreateSavingsAccountRequest createSavingsAccountRequest = new CreateSavingsAccountRequest();
    createSavingsAccountRequest.setClientInformation(
        createClientInformationByClientEntity(clientEntity));
    createSavingsAccountRequest.setIdClient(clientEntity.getIdClient());
    return createSavingsAccountRequest;
  }

  private static ClientInformation createClientInformationByClientEntity(
      ClientEntity clientEntity) {
    ClientInformation clientInformation = new ClientInformation();
    clientInformation.setDocumentId(createDocumentIdFromEntity(clientEntity));
    clientInformation.setEmail(clientEntity.getEmailAddress());
    clientInformation.setGender(clientEntity.getGender());
    clientInformation.setLastName(clientEntity.getLastName());
    clientInformation.setName(clientEntity.getName());
    clientInformation.setPhone(createPhoneFromEntity(clientEntity));
    return clientInformation;
  }

  private static Phone createPhoneFromEntity(ClientEntity clientEntity) {
    Phone phone = new Phone();
    phone.setNumber(clientEntity.getPhoneNumber());
    phone.setPrefix(String.valueOf(clientEntity.getPhonePrefix()));
    return phone;
  }

  private static DocumentId createDocumentIdFromEntity(ClientEntity clientEntity) {
    DocumentId documentId = new DocumentId();
    documentId.setExpirationDate(clientEntity.getExpirationDate());
    documentId.setId(clientEntity.getIdCard());
    documentId.setIssueDate(
        clientEntity.getDateOfIssue().format(DateTimeFormatter.ofPattern("uuuu-MM-dd")));
    documentId.setType(clientEntity.getTypeDocument());
    return documentId;
  }

  public static IdentityInformation createIdentityInformationByClientEntity(
      ClientEntity clientEntity) {
    try {
      return IdentityInformation.builder()
          .birthDate(getFormatDate(clientEntity.getBirthDate()))
          .name(clientEntity.getName())
          .lastName(clientEntity.getLastName())
          .gender(clientEntity.getGender())
          .documentNumber(clientEntity.getIdCard())
          .documentType(clientEntity.getTypeDocument())
          .expeditionDate(getFormatDate(clientEntity.getDateOfIssue()))
          .phone(
              new com.lulobank.clients.services.events.Phone(
                  clientEntity.getPhoneNumber(), String.valueOf(clientEntity.getPhonePrefix())))
          .email(clientEntity.getEmailAddress())
          .build();
    } catch (DateTimeException ex) {
      throw new ServiceException(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), ex);
    } catch (Exception ex) {
      throw new ServiceException(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), ex);
    }
  }

  private static String getFormatDate(LocalDate dateOfIssue2) {
    return Optional.ofNullable(dateOfIssue2)
        .map(dateOfIssue -> dateOfIssue.format(DateTimeFormatter.ofPattern("uuuu-MM-dd")))
        .orElse(null);
  }

  public static ClientsV3Entity createClientEntityFromInitialClient(
      CreateInitialClient createInitialClient) {
    ClientsV3Entity clientEntity = new ClientsV3Entity();
    clientEntity.setIdClient(UUID.randomUUID().toString());
    clientEntity.setPhonePrefix(createInitialClient.getPhoneCreateInitialClient().getPrefix());
    clientEntity.setPhoneNumber(createInitialClient.getPhoneCreateInitialClient().getNumber());
    clientEntity.setPhoneVerified(createInitialClient.getPhoneCreateInitialClient().getVerified());
    clientEntity.setEmailAddress(createInitialClient.getEmailCreateClientRequest().getAddress().toLowerCase(LocaleUtils.toLocale("es_CO")));
    clientEntity.setEmailVerified(createInitialClient.getEmailCreateClientRequest().getVerified());
    clientEntity.setQualityCode(createInitialClient.getPassword());
    clientEntity.setAcceptances(new ClientAcceptanceV3(getLocalDateTimeGMT5FromTimestamp(Long.parseLong(createInitialClient.getDocumentAcceptancesTimestamp()))));
    clientEntity.setIdentityBiometricId(UUID.randomUUID().toString());
    clientEntity.setIdCbs(UUID.randomUUID().toString());
    OnBoardingStatusV3 onBoardingStatus =
        new OnBoardingStatusV3(ON_BOARDING.name(), createInitialClient.getSelectedProduct().name());
    clientEntity.setOnBoardingStatus(onBoardingStatus);
    return clientEntity;
  }

  public static ClientVerificationFirebase createClientVerificationFirebaseKOIdentityFromEntity(
      ClientEntity clientEntity) {
    ClientVerificationFirebase clientVerificationFirebase =
        new ClientVerificationFirebase(
            clientEntity.getOnBoardingStatus().getProductSelected(), KO_IDENTITY.name());
    Optional.ofNullable(clientEntity.getIdentityBiometric().getTransactionState())
        .ifPresent(
            transactionState -> {
              if (isCodeFailedBiometricFraud.test(transactionState.getId())) {
                clientVerificationFirebase.setVerificationResult(KO_IDENTITY_FRAUD.name());
              }
            });
    return clientVerificationFirebase;
  }

  public static ClientVerificationFirebase createClientVerificationFirebaseOKFromEntity(
      ClientEntity clientEntity) {
    return new ClientVerificationFirebase(
        clientEntity.getOnBoardingStatus().getProductSelected(),
        StatusClientVerificationFirebaseEnum.OK.name());
  }

  public static ClientVerificationFirebase createClientVerificationFirebaseFailFromEntity(
      ClientEntity clientEntity, String detail) {
    return new ClientVerificationFirebase(
        clientEntity.getOnBoardingStatus().getProductSelected(),
        StatusClientVerificationFirebaseEnum.FAILED.name(),
        detail);
  }

  public static LoanRequestedStatusFirebase createLoanRequestedStatusFirebaseFail(String detail) {
    return new LoanRequestedStatusFirebase(
        StatusClientVerificationFirebaseEnum.FAILED.name(), detail);
  }
}
