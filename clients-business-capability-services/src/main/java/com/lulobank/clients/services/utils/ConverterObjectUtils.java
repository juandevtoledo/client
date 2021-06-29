package com.lulobank.clients.services.utils;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.defaultString;

import com.lulobank.clients.sdk.operations.dto.ClientInformationByIdCard;
import com.lulobank.clients.sdk.operations.dto.economicinformation.ClientEconomicInformation;
import com.lulobank.clients.sdk.operations.dto.economicinformation.EmployeeCompany;
import com.lulobank.clients.sdk.operations.dto.economicinformation.ForeignCurrencyTransaction;
import com.lulobank.clients.sdk.operations.dto.onboardingclients.AccountBasicInfo;
import com.lulobank.clients.sdk.operations.dto.onboardingclients.ClientInformationByIdClient;
import com.lulobank.clients.sdk.operations.dto.onboardingclients.OnBoardingStatus;
import com.lulobank.clients.services.domain.Attachment;
import com.lulobank.clients.services.domain.CheckingAccount;
import com.lulobank.clients.services.domain.Client;
import com.lulobank.clients.services.domain.ForeignTransaction;
import com.lulobank.clients.services.domain.StateBlackList;
import com.lulobank.clients.services.events.EconomicInformationEvent;
import com.lulobank.clients.services.events.IdentityInformation;
import com.lulobank.clients.services.events.Phone;
import com.lulobank.clients.services.features.onboardingclients.model.AttachmentCreateClientRequest;
import com.lulobank.clients.services.features.onboardingclients.model.CheckingAccountCreateClientRequest;
import com.lulobank.clients.services.features.onboardingclients.model.ClientInformationByPhone;
import com.lulobank.clients.services.features.onboardingclients.model.CreateClientRequest;
import com.lulobank.clients.services.features.onboardingclients.model.ForeignTransactionCreateClientRequest;
import com.lulobank.clients.services.features.riskengine.model.ClientWithIdCardInformation;
import com.lulobank.clients.services.outboundadapters.model.AdditionalPersonalInformation;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.outboundadapters.model.EconomicInformation;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.savingsaccounts.sdk.dto.createsavingaccount.SavingAccountToCreate;
import flexibility.client.models.request.GetPendingTransactionsRequest;
import flexibility.client.models.response.CreateClientResponse;
import flexibility.client.models.response.GetAccountResponse;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.vavr.control.Option;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;

public class ConverterObjectUtils {
  public static Client getClientFromClientWithIdCardInformation(
      ClientWithIdCardInformation clientCClientWithIdCardInformationreated) {
    ModelMapper modelMapper = new ModelMapper();
    PropertyMap<ClientWithIdCardInformation, Client> orderMap =
        new PropertyMap<ClientWithIdCardInformation, Client>() {
          @Override
          protected void configure() {
            map().setIdCard(source.getIdCard());
            map().getPhone().setNumber(source.getPhone().getNumber());
            map().getPhone().setPrefix(source.getPhone().getPrefix());
            map().getPhone().setVerified(source.getPhone().getVerified());
            map()
                .getPhone()
                .getDeviceInfo()
                .setSimCardId(source.getPhone().getDeviceInfo().getSimCardId());
            map()
                .getPhone()
                .getDeviceInfo()
                .setOperator(source.getPhone().getDeviceInfo().getOperator());
            map().getPhone().getDeviceInfo().setModel(source.getPhone().getDeviceInfo().getModel());
            map()
                .getPhone()
                .getDeviceInfo()
                .setMobileDevice(source.getPhone().getDeviceInfo().getMobileDevice());
            map()
                .getPhone()
                .getDeviceInfo()
                .setIpAddress(source.getPhone().getDeviceInfo().getIpAddress());
            map()
                .getPhone()
                .getDeviceInfo()
                .setIddevice(source.getPhone().getDeviceInfo().getIddevice());
            map()
                .getPhone()
                .getDeviceInfo()
                .setGeolocation(source.getPhone().getDeviceInfo().getGeolocation());
            map()
                .getPhone()
                .getDeviceInfo()
                .setCountry(source.getPhone().getDeviceInfo().getCountry());
            map().getPhone().getDeviceInfo().setCity(source.getPhone().getDeviceInfo().getCity());
          }
        };
    modelMapper.addMappings(orderMap);
    Client client = modelMapper.map(clientCClientWithIdCardInformationreated, Client.class);
    client.setDateOfIssue(
        DatesUtil.convertStringYYYYmmDDToLocalDateTime(
            clientCClientWithIdCardInformationreated.getDateOfIssue()));
    return client;
  }

  // Todo: Revisar como se van a crear los objetos de dominio
  public static Client getClientFromCreateClientRequest(CreateClientRequest createClientRequest) {
    ModelMapper modelMapper = new ModelMapper();
    PropertyMap<CreateClientRequest, Client> orderMap =
        new PropertyMap<CreateClientRequest, Client>() {
          @Override
          protected void configure() {
            map().setId(source.getIdClient());
            map().setQualityCode(source.getPassword());
            map().getEmail().setAddress(source.getEmail().getAddress());
            map().getPhone().setNumber(source.getPhone().getNumber());
            map().getPhone().setPrefix(source.getPhone().getPrefix());
            map().getPhone().setVerified(source.getPhone().getVerified());
            map()
                .getPhone()
                .getDeviceInfo()
                .setSimCardId(source.getPhone().getDeviceInfo().getSimCardId());
            map()
                .getPhone()
                .getDeviceInfo()
                .setOperator(source.getPhone().getDeviceInfo().getOperator());
            map().getPhone().getDeviceInfo().setModel(source.getPhone().getDeviceInfo().getModel());
            map()
                .getPhone()
                .getDeviceInfo()
                .setMobileDevice(source.getPhone().getDeviceInfo().getMobileDevice());
            map()
                .getPhone()
                .getDeviceInfo()
                .setIpAddress(source.getPhone().getDeviceInfo().getIpAddress());
            map()
                .getPhone()
                .getDeviceInfo()
                .setIddevice(source.getPhone().getDeviceInfo().getIddevice());
            map()
                .getPhone()
                .getDeviceInfo()
                .setGeolocation(source.getPhone().getDeviceInfo().getGeolocation());
            map()
                .getPhone()
                .getDeviceInfo()
                .setCountry(source.getPhone().getDeviceInfo().getCountry());
            map().getPhone().getDeviceInfo().setCity(source.getPhone().getDeviceInfo().getCity());
            // TODO: Put logic when an Id exist in black list
          }
        };
    modelMapper.addMappings(orderMap);
    Client client = modelMapper.map(createClientRequest, Client.class);
    client.setAttachments(
        getAttachmentsFromClientRequestAttachments(createClientRequest.getAttachments()));
    client.setForeignTransactions(
        getForeignCurrencyTransactionsFromClientRequest(
            createClientRequest.getForeignCurrencyTransactions()));
    return client;
  }

  // TODO: LOS MAPPER NO PUEDEN LOGICA DE NEGOCIO
  public static ClientEntity initClientEntityFromClient(Client client) {
    ClientEntity clientEntity = new ModelMapper().map(client, ClientEntity.class);
    // TODO desperate way to set de UUID as generator doesnt work, fix it
    clientEntity.setBlackListState(StateBlackList.WAITING_FOR_VERIFICATION.name());
    clientEntity.setBlackListDate(DatesUtil.getLocalDateGMT5());
    return clientEntity;
  }

  public static ClientInformationByPhone createClientTransactionResponseFromClientEntity(
      ClientEntity clientEntity) {
    ClientInformationByPhone clientTransactionResponse = new ClientInformationByPhone();
    clientTransactionResponse.setIdClient(clientEntity.getIdClient());
    clientTransactionResponse.setName(clientEntity.getName());
    clientTransactionResponse.setLastName(clientEntity.getLastName());
    clientTransactionResponse.setIdCbs(clientEntity.getIdCbs());
    clientTransactionResponse.setIdCbsHash(clientEntity.getIdCbsHash());
    clientTransactionResponse.setPhoneNumber(clientEntity.getPhoneNumber());
    clientTransactionResponse.setPhonePrefix(clientEntity.getPhonePrefix());
    clientTransactionResponse.setEmailAddress(clientEntity.getEmailAddress());
    return clientTransactionResponse;
  }

  public static ClientInformationByIdCard createClientByIdCardTransactionResponseFromClientEntity(
      ClientEntity clientEntity) {
    ClientInformationByIdCard clientTransactionResponse = new ClientInformationByIdCard();
    clientTransactionResponse.setIdClient(clientEntity.getIdClient());
    clientTransactionResponse.setName(clientEntity.getName());
    clientTransactionResponse.setLastName(clientEntity.getLastName());
    clientTransactionResponse.setIdCbs(clientEntity.getIdCbs());
    clientTransactionResponse.setPhoneNumber(clientEntity.getPhoneNumber());
    clientTransactionResponse.setPhonePrefix(clientEntity.getPhonePrefix());
    clientTransactionResponse.setEmailAddress(clientEntity.getEmailAddress());
    clientTransactionResponse.setIdCard(clientEntity.getIdCard());
    return clientTransactionResponse;
  }

  // RGA
  public static ClientInformationByIdClient createClientInformationByIdCardFromClientEntity(
      ClientEntity clientEntity, List<GetAccountResponse> clientAccounts) {
    ModelMapper modelMapper = new ModelMapper();
    ClientInformationByIdClient clientInformationByIdClient = new ClientInformationByIdClient();
    clientInformationByIdClient.setIdClient(clientEntity.getIdClient());
    clientInformationByIdClient.setIdCard(clientEntity.getIdCard());
    clientInformationByIdClient.setName(clientEntity.getName());
    clientInformationByIdClient.setLastName(clientEntity.getLastName());
    clientInformationByIdClient.setAddress(getShortAddress(clientEntity));
    clientInformationByIdClient.setIdCbs(clientEntity.getIdCbs());
    clientInformationByIdClient.setIdCbsHash(clientEntity.getIdCbsHash());
    clientInformationByIdClient.setPhoneNumber(clientEntity.getPhoneNumber());
    clientInformationByIdClient.setPhonePrefix(clientEntity.getPhonePrefix());
    clientInformationByIdClient.setEmailAddress(clientEntity.getEmailAddress());
    clientInformationByIdClient.setGender(clientEntity.getGender());
    clientInformationByIdClient.setDocumentIssuedBy(clientEntity.getDocumentIssuedBy());
    clientInformationByIdClient.setTypeDocument(clientEntity.getTypeDocument());
    clientInformationByIdClient.setExpirationDate(clientEntity.getExpirationDate());
    clientInformationByIdClient.setExpeditionDate(Option.of(clientEntity.getDateOfIssue()).fold(() -> null, date -> date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
    clientInformationByIdClient.setBirthDate(clientEntity.getBirthDate());
    clientInformationByIdClient.setCapitalizedName(getCapitalizedFirstName(clientEntity));
    clientInformationByIdClient.setInitialsName(getPrefix(clientEntity));
    Optional.ofNullable(clientEntity.getOnBoardingStatus())
        .ifPresent(
            onBoardingStatus -> {
              clientInformationByIdClient.setOnBoardingStatus(
                  modelMapper.map(clientEntity.getOnBoardingStatus(), OnBoardingStatus.class));
            });
    clientInformationByIdClient.setSavingsAccounts(
        clientAccounts.stream()
            .filter(Objects::nonNull)
            .map(ConverterObjectUtils::getAccountBasicInfo)
            .collect(Collectors.toList()));
    Option<AdditionalPersonalInformation> additionalPersonalInformation =
            Option.of(clientEntity.getAdditionalPersonalInformation());
    ClientInformationByIdClient.AdditionalPersonalInfo additionalPersonalInfo = ClientInformationByIdClient
            .AdditionalPersonalInfo.builder()
            .firstName(additionalPersonalInformation.map(AdditionalPersonalInformation::getFirstName)
                    .getOrElse(EMPTY))
            .secondName(additionalPersonalInformation.map(AdditionalPersonalInformation::getSecondName)
                    .getOrElse(EMPTY))
            .firstSurname(additionalPersonalInformation.map(AdditionalPersonalInformation::getFirstSurname)
                    .getOrElse(EMPTY))
            .secondSurname(additionalPersonalInformation.map(AdditionalPersonalInformation::getSecondSurname)
                    .getOrElse(EMPTY))
            .build();
    clientInformationByIdClient.setAdditionalPersonalInfo(additionalPersonalInfo);
    clientInformationByIdClient.setAddressComplement(getAddressComplement(clientEntity));
    return clientInformationByIdClient;
  }

  private static String getAddressComplement(ClientEntity clientEntity){
    return Option.of(clientEntity.getAddressComplement())
            .fold(() -> EMPTY, Function.identity());
  }

  public static AccountBasicInfo getAccountBasicInfo(GetAccountResponse getAccountResponse) {
    return new AccountBasicInfo(getAccountResponse.getNumber(), getAccountResponse.getGmf());
  }

  public static ClientEntity clientEntityFromClient(Client client) {
    return new ModelMapper().map(client, ClientEntity.class);
  }

  public static Client clientFromClientEntity(ClientEntity clientEntity) {
    ModelMapper modelMapper = new ModelMapper();
    PropertyMap<ClientEntity, Client> orderMap =
        new PropertyMap<ClientEntity, Client>() {
          @Override
          protected void configure() {
            map().setId(source.getIdClient());
            map().setIdCard(source.getIdCard());
            map().getEmail().setAddress(source.getEmailAddress());
            map().getEmail().setVerified(source.getEmailVerified());
            map().getPhone().setNumber(source.getPhoneNumber());
            map().getPhone().setPrefix(source.getPhonePrefix());
            map().getPhone().setVerified(source.getPhoneVerified());
            map().getPhone().getDeviceInfo().setSimCardId(source.getPhoneDeviceInfoSimCardId());
            map().getPhone().getDeviceInfo().setOperator(source.getPhoneDeviceInfoOperator());
            map().getPhone().getDeviceInfo().setModel(source.getPhoneDeviceInfoModel());
            map()
                .getPhone()
                .getDeviceInfo()
                .setMobileDevice(source.getPhoneDeviceInfoMobileDevice());
            map().getPhone().getDeviceInfo().setIpAddress(source.getPhoneDeviceInfoIpAddress());
            map().getPhone().getDeviceInfo().setIddevice(source.getPhoneDeviceInfoIddevice());
            map().getPhone().getDeviceInfo().setGeolocation(source.getPhoneDeviceInfoGeolocation());
            map().getPhone().getDeviceInfo().setCountry(source.getPhoneDeviceInfoCountry());
            map().getPhone().getDeviceInfo().setCity(source.getPhoneDeviceInfoCity());
          }
        };
    modelMapper.addMappings(orderMap);
    return modelMapper.map(clientEntity, Client.class);
  }

  private static List<Attachment> getAttachmentsFromClientRequestAttachments(
      List<AttachmentCreateClientRequest> attachments) {
    return attachments.stream()
        .map(x -> new Attachment(x.getKey(), x.getLink()))
        .collect(Collectors.toList());
  }

  private static List<ForeignTransaction> getForeignCurrencyTransactionsFromClientRequest(
      List<ForeignTransactionCreateClientRequest> foreignCurrencyTransactions) {
    if (Objects.isNull(foreignCurrencyTransactions) || foreignCurrencyTransactions.isEmpty()) {
      return new ArrayList<>();
    }
    return foreignCurrencyTransactions.stream()
        .map(
            x ->
                new ForeignTransaction(
                    x.getName(), getCheckingAccountFromCreateClientRequest(x.getCheckingAccount())))
        .collect(Collectors.toList());
  }

  private static CheckingAccount getCheckingAccountFromCreateClientRequest(
      CheckingAccountCreateClientRequest checkingAccount) {
    ModelMapper modelMapper = new ModelMapper();
    PropertyMap<CheckingAccountCreateClientRequest, CheckingAccount> orderMap =
        new PropertyMap<CheckingAccountCreateClientRequest, CheckingAccount>() {
          @Override
          protected void configure() {
            map().setAmount(source.getAmount());
            map().setBank(source.getBank());
            map().setCity(source.getCity());
            map().setCountry(source.getCountry());
            map().setCurrency(source.getCurrency());
          }
        };
    modelMapper.addMappings(orderMap);
    return modelMapper.map(checkingAccount, CheckingAccount.class);
  }

  public static SavingAccountToCreate getSavingAccountToCreateRequest(
      CreateClientResponse createClientResponse, String idClient) {
    SavingAccountToCreate request = new SavingAccountToCreate();
    request.setIdClientCBS(createClientResponse.getClient().getId());
    request.setIdClientCBSHash(createClientResponse.getAccount().getId());
    request.setIdSavingAccount(createClientResponse.getAccount().getNumber());
    request.setIdSavingAccountCBSHash(createClientResponse.getAccount().getProductKey());
    request.setIdClient(idClient);
    return request;
  }

  public static EconomicInformation getEconomicInfoToDynamo(ClientEconomicInformation request) {
    EconomicInformation economicInformation =
        new ModelMapper().map(request, EconomicInformation.class);
    return economicInformation;
  }

  public static com.lulobank.clients.services.outboundadapters.model.ForeignTransaction
      getForeignTransactionInfoToDynamo(ForeignCurrencyTransaction request) {
    return new ModelMapper()
        .map(
            request, com.lulobank.clients.services.outboundadapters.model.ForeignTransaction.class);
  }

  public static IdentityInformation getIdentityInfoEvent(IdentityInformation request) {
    request.setGender(request.getGender().toUpperCase(LocaleUtils.toLocale("es_CO")));
    return request;
  }

  public static EconomicInformationEvent getEconomicInfoEvent(ClientEconomicInformation request) {
    EconomicInformationEvent event = new EconomicInformationEvent();
    event.setAssets(request.getAssets());
    event.setLiabilities(request.getLiabilities());
    event.setEconomicActivity(request.getEconomicActivity());
    event.setMonthlyIncome(request.getMonthlyIncome());
    event.setMonthlyOutcome(request.getMonthlyOutcome());
    event.setEmployeeCompany(getEmployeeCompany(request));
    event.setAdditionalIncome(request.getAdditionalIncome());
    event.setOccupationType(request.getOccupationType().name());
    event.setSavingPurpose(defaultString(request.getSavingPurpose(), EMPTY));
    event.setTypeSaving(defaultString(request.getTypeSaving(), EMPTY));
    return event;
  }

  public static IdentityInformation getIdentityInformation(ClientsV3Entity clientsV3Entity) {
    Phone phone =new Phone();
    phone.setNumber(clientsV3Entity.getPhoneNumber());
    phone.setPrefix(clientsV3Entity.getPhonePrefix().toString());

    IdentityInformation identityInformation = new IdentityInformation();
    identityInformation.setName(clientsV3Entity.getName());
    identityInformation.setLastName(clientsV3Entity.getLastName());
    identityInformation.setGender(clientsV3Entity.getGender());
    identityInformation.setDocumentNumber(clientsV3Entity.getIdCard());
    identityInformation.setDocumentType(clientsV3Entity.getTypeDocument());
    identityInformation.setExpeditionDate(clientsV3Entity.getDateOfIssue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    identityInformation.setBirthDate(clientsV3Entity.getBirthDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    identityInformation.setPhone(phone);
    identityInformation.setEmail(clientsV3Entity.getEmailAddress());

    return identityInformation;
  }

  private static EmployeeCompany getEmployeeCompany(ClientEconomicInformation request) {
    EmployeeCompany company = new EmployeeCompany();
    company.setCity(EMPTY);
    company.setName(EMPTY);
    company.setState(EMPTY);
    Optional.ofNullable(request.getEmployeeCompany())
        .ifPresent(
            employeeCompany -> {
              company.setCity(defaultString(employeeCompany.getCity()));
              company.setState(defaultString(employeeCompany.getState()));
              company.setName(defaultString(employeeCompany.getName()));
            });
    return company;
  }

  public static String getShortAddress(ClientEntity clientEntity) {
    return StringUtils.defaultString(clientEntity.getAddressPrefix(), EMPTY)
        .concat(" ")
        .concat(StringUtils.defaultString(clientEntity.getAddress(), EMPTY));
  }

  public static String getPrefix(ClientEntity client) {
    return getPrefixName(client) + getPrefixLastName(client);
  }

  public static String getPrefixName(ClientEntity client) {
    return Option.of(client)
            .map(ClientEntity::getName)
            .map(name -> {
              if(StringUtils.isBlank(name) || StringUtils.isEmpty(name)) {
                return EMPTY;
              }
              return name.substring(0, 1).toUpperCase();
            })
            .getOrElse(()-> EMPTY);
  }

  public static String getPrefixLastName(ClientEntity client) {
    return Option.of(client)
            .map(ClientEntity::getLastName)
            .map(lastName -> {
              if(StringUtils.isBlank(lastName) || StringUtils.isEmpty(lastName)) {
                return EMPTY;
              }

              return lastName.substring(0, 1).toUpperCase();
            })
            .getOrElse(()-> EMPTY);
  }

  public static String getCapitalizedFirstName(ClientEntity client) {
    return Option.of(client)
            .map(ClientEntity::getName)
            .map(name -> {
              if (StringUtils.isBlank(name) || StringUtils.isEmpty(name)) {
                return EMPTY;
              }

              String[] names = name.split(" ");
              name = names[0];
              return name.substring(0,1).toUpperCase() + name.substring(1).toLowerCase();
            })
            .getOrElse(()-> EMPTY);
  }

  public static GetPendingTransactionsRequest createGetPendingTransactionsRequest(String idCbs) {
    GetPendingTransactionsRequest getPendingTransactionsRequest =
        new GetPendingTransactionsRequest();
    getPendingTransactionsRequest.setClientId(idCbs);
    getPendingTransactionsRequest.setOnlyToApprove(true);
    return getPendingTransactionsRequest;
  }
}