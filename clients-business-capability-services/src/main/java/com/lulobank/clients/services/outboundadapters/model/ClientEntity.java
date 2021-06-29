package com.lulobank.clients.services.outboundadapters.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;
import com.lulobank.clients.services.outboundadapters.dynamoconverter.LocalDateConverter;
import com.lulobank.clients.services.outboundadapters.dynamoconverter.LocalDateTimeConverter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@DynamoDBTable(tableName = "Clients")
public class ClientEntity {

  @DynamoDBHashKey private String idClient;
  private String idCard;
  private String name;
  private String lastName;
  private String address;
  private String addressPrefix;
  private String code;
  private String addressComplement;
  private String city;
  private String cityId;
  private String department;
  private String departmentId;
  private String emailAddress;
  private Boolean emailVerified;
  private String qualityCode;
  private Integer phonePrefix;
  private String phoneDeviceInfoIddevice;
  private String phoneDeviceInfoIpAddress;
  private String phoneDeviceInfoGeolocation;
  private String phoneDeviceInfoCountry;
  private String phoneDeviceInfoCity;
  private String phoneNumber;
  private Boolean phoneVerified;
  private String phoneDeviceInfoMobileDevice;
  private String phoneDeviceInfoSimCardId;
  private String phoneDeviceInfoModel;
  private String phoneDeviceInfoOperator;
  private List<com.lulobank.clients.services.outboundadapters.model.Attachment> attachments;
  private String idCognito;
  private String idKeycloak;
  private String idCbs;
  private String idCbsHash;
  private String blackListState;
  private LocalDateTime blackListDate;
  private String blackListRiskLevel;
  private LocalDateTime whitelistExpirationDate;
  private LocalDate dateOfIssue;
  private LocalDate birthDate;
  private List<ForeignTransaction> foreignTransactions;
  private EconomicInformation economicInformation;
  private OnBoardingStatus onBoardingStatus;
  private CreditRiskAnalysis creditRiskAnalysis;
  private String gender;
  private String documentIssuedBy;
  private String typeDocument;
  private String expirationDate;
  private LoanRequested loanRequested;
  private IdentityBiometric identityBiometric;
  private AdditionalPersonalInformation additionalPersonalInformation;
  private Boolean resetBiometric;
  private boolean digitalStorageStatus;
  private ClientAcceptance acceptances;
  private boolean customerCreatedStatus;
  private boolean identityProcessed;
  private boolean economicProcessed;
  private String pep;
  private String identityBiometricId;

  public String getIdClient() {
    return idClient;
  }

  public void setIdClient(String idClient) {
    this.idClient = idClient;
  }

  public String getIdCard() {
    return idCard;
  }

  public void setIdCard(String idCard) {
    this.idCard = idCard;
  }

  @DynamoDBTypeConverted(converter = LocalDateTimeConverter.class)
  public LocalDateTime getBlackListDate() {
    return blackListDate;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getEmailAddress() {
    return emailAddress;
  }

  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }

  public Boolean getEmailVerified() {
    return emailVerified;
  }

  public void setEmailVerified(Boolean emailVerified) {
    this.emailVerified = emailVerified;
  }

  public String getQualityCode() {
    return qualityCode;
  }

  public void setQualityCode(String qualityCode) {
    this.qualityCode = qualityCode;
  }

  public Integer getPhonePrefix() {
    return phonePrefix;
  }

  public void setPhonePrefix(Integer phonePrefix) {
    this.phonePrefix = phonePrefix;
  }

  public String getPhoneDeviceInfoIddevice() {
    return phoneDeviceInfoIddevice;
  }

  public void setPhoneDeviceInfoIddevice(String phoneDeviceInfoIddevice) {
    this.phoneDeviceInfoIddevice = phoneDeviceInfoIddevice;
  }

  public String getPhoneDeviceInfoIpAddress() {
    return phoneDeviceInfoIpAddress;
  }

  public void setPhoneDeviceInfoIpAddress(String phoneDeviceInfoIpAddress) {
    this.phoneDeviceInfoIpAddress = phoneDeviceInfoIpAddress;
  }

  public String getPhoneDeviceInfoGeolocation() {
    return phoneDeviceInfoGeolocation;
  }

  public void setPhoneDeviceInfoGeolocation(String phoneDeviceInfoGeolocation) {
    this.phoneDeviceInfoGeolocation = phoneDeviceInfoGeolocation;
  }

  public String getPhoneDeviceInfoCountry() {
    return phoneDeviceInfoCountry;
  }

  public void setPhoneDeviceInfoCountry(String phoneDeviceInfoCountry) {
    this.phoneDeviceInfoCountry = phoneDeviceInfoCountry;
  }

  public String getPhoneDeviceInfoCity() {
    return phoneDeviceInfoCity;
  }

  public void setPhoneDeviceInfoCity(String phoneDeviceInfoCity) {
    this.phoneDeviceInfoCity = phoneDeviceInfoCity;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public Boolean getPhoneVerified() {
    return phoneVerified;
  }

  public void setPhoneVerified(Boolean phoneVerified) {
    this.phoneVerified = phoneVerified;
  }

  public String getPhoneDeviceInfoMobileDevice() {
    return phoneDeviceInfoMobileDevice;
  }

  public void setPhoneDeviceInfoMobileDevice(String phoneDeviceInfoMobileDevice) {
    this.phoneDeviceInfoMobileDevice = phoneDeviceInfoMobileDevice;
  }

  public String getPhoneDeviceInfoSimCardId() {
    return phoneDeviceInfoSimCardId;
  }

  public void setPhoneDeviceInfoSimCardId(String phoneDeviceInfoSimCardId) {
    this.phoneDeviceInfoSimCardId = phoneDeviceInfoSimCardId;
  }

  public String getPhoneDeviceInfoModel() {
    return phoneDeviceInfoModel;
  }

  public void setPhoneDeviceInfoModel(String phoneDeviceInfoModel) {
    this.phoneDeviceInfoModel = phoneDeviceInfoModel;
  }

  public String getPhoneDeviceInfoOperator() {
    return phoneDeviceInfoOperator;
  }

  public void setPhoneDeviceInfoOperator(String phoneDeviceInfoOperator) {
    this.phoneDeviceInfoOperator = phoneDeviceInfoOperator;
  }

  public List<Attachment> getAttachments() {
    return attachments;
  }

  public void setAttachments(List<Attachment> attachments) {
    this.attachments = attachments;
  }

  public String getIdCognito() {
    return idCognito;
  }

  public void setIdCognito(String idCognito) {
    this.idCognito = idCognito;
  }

  public String getIdKeycloak() {
    return idKeycloak;
  }

  public void setIdKeycloak(String idKeycloak) {
    this.idKeycloak = idKeycloak;
  }

  public String getIdCbs() {
    return idCbs;
  }

  public void setIdCbs(String idCbs) {
    this.idCbs = idCbs;
  }

  public String getIdCbsHash() {
    return idCbsHash;
  }

  public void setIdCbsHash(String idCbsHash) {
    this.idCbsHash = idCbsHash;
  }

  public String getBlackListState() {
    return blackListState;
  }

  public void setBlackListState(String blackListState) {
    this.blackListState = blackListState;
  }

  public void setBlackListDate(LocalDateTime blackListDate) {
    this.blackListDate = blackListDate;
  }

  @DynamoDBTypeConverted(converter = LocalDateConverter.class)
  public LocalDate getDateOfIssue() {
    return dateOfIssue;
  }

  public void setDateOfIssue(LocalDate dateOfIssue) {
    this.dateOfIssue = dateOfIssue;
  }

  public List<ForeignTransaction> getForeignTransactions() {
    return foreignTransactions;
  }

  public void setForeignTransactions(List<ForeignTransaction> foreignTransactions) {
    this.foreignTransactions = foreignTransactions;
  }

  public OnBoardingStatus getOnBoardingStatus() {
    return onBoardingStatus;
  }

  public void setOnBoardingStatus(OnBoardingStatus onBoardingStatus) {
    this.onBoardingStatus = onBoardingStatus;
  }

  public EconomicInformation getEconomicInformation() {
    return economicInformation;
  }

  public ClientEntity setEconomicInformation(EconomicInformation economicInformation) {
    this.economicInformation = economicInformation;
    return this;
  }

  public CreditRiskAnalysis getCreditRiskAnalysis() {
    return creditRiskAnalysis;
  }

  public void setCreditRiskAnalysis(CreditRiskAnalysis creditRiskAnalysis) {
    this.creditRiskAnalysis = creditRiskAnalysis;
  }

  public String getGender() {
    return gender;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }

  public String getDocumentIssuedBy() {
    return documentIssuedBy;
  }

  public void setDocumentIssuedBy(String documentIssuedBy) {
    this.documentIssuedBy = documentIssuedBy;
  }

  public String getTypeDocument() {
    return typeDocument;
  }

  public void setTypeDocument(String typeDocument) {
    this.typeDocument = typeDocument;
  }

  public String getExpirationDate() {
    return expirationDate;
  }

  public void setExpirationDate(String expirationDate) {
    this.expirationDate = expirationDate;
  }

  public LoanRequested getLoanRequested() {
    return loanRequested;
  }

  public void setLoanRequested(LoanRequested loanRequested) {
    this.loanRequested = loanRequested;
  }

  public String getDepartment() {
    return department;
  }

  public void setDepartment(String department) {
    this.department = department;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public IdentityBiometric getIdentityBiometric() {
    return identityBiometric;
  }

  public void setIdentityBiometric(IdentityBiometric identityBiometric) {
    this.identityBiometric = identityBiometric;
  }

  @DynamoDBTypeConverted(converter = LocalDateConverter.class)
  public LocalDate getBirthDate() {
    return birthDate;
  }

  public void setBirthDate(LocalDate birthDate) {
    this.birthDate = birthDate;
  }

  public AdditionalPersonalInformation getAdditionalPersonalInformation() {
    return additionalPersonalInformation;
  }

  public void setAdditionalPersonalInformation(
      AdditionalPersonalInformation additionalPersonalInformation) {
    this.additionalPersonalInformation = additionalPersonalInformation;
  }

  public Boolean getResetBiometric() {
    return resetBiometric;
  }

  public void setResetBiometric(Boolean resetBiometric) {
    this.resetBiometric = resetBiometric;
  }

  public String getAddressPrefix() {
    return addressPrefix;
  }

  public void setAddressPrefix(String addressPrefix) {
    this.addressPrefix = addressPrefix;
  }

  public String getAddressComplement() {
    return addressComplement;
  }

  public void setAddressComplement(String addressComplement) {
    this.addressComplement = addressComplement;
  }

  public String getCityId() {
    return cityId;
  }

  public void setCityId(String cityId) {
    this.cityId = cityId;
  }

  public String getDepartmentId() {
    return departmentId;
  }

  public void setDepartmentId(String departmentId) {
    this.departmentId = departmentId;
  }

  public boolean getDigitalStorageStatus() {
    return digitalStorageStatus;
  }

  public void setDigitalStorageStatus(boolean digitalStorageStatus) {
    this.digitalStorageStatus = digitalStorageStatus;
  }

  public ClientAcceptance getAcceptances() {
    return acceptances;
  }

  public void setAcceptances(ClientAcceptance acceptances) {
    this.acceptances = acceptances;
  }

  public boolean isCustomerCreatedStatus() {
    return customerCreatedStatus;
  }

  public void setCustomerCreatedStatus(boolean customerCreatedStatus) {
    this.customerCreatedStatus = customerCreatedStatus;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public boolean isIdentityProcessed() {
    return identityProcessed;
  }

  public void setIdentityProcessed(boolean identityProcessed) {
    this.identityProcessed = identityProcessed;
  }

  public boolean isEconomicProcessed() {
    return economicProcessed;
  }

  public void setEconomicProcessed(boolean economicProcessed) {
    this.economicProcessed = economicProcessed;
  }

  public String getPep() {
    return pep;
  }

  public void setPep(String pep) {
    this.pep = pep;
  }

  public String getBlackListRiskLevel() {
    return blackListRiskLevel;
  }

  public void setBlackListRiskLevel(String blackListRiskLevel) {
    this.blackListRiskLevel = blackListRiskLevel;
  }

  @DynamoDBTypeConverted(converter = LocalDateTimeConverter.class)
  public LocalDateTime getWhitelistExpirationDate() {
    return whitelistExpirationDate;
  }

  public void setWhitelistExpirationDate(LocalDateTime whitelistExpirationDate) {
    this.whitelistExpirationDate = whitelistExpirationDate;
  }

  public String getIdentityBiometricId() {
    return identityBiometricId;
  }

  public void setIdentityBiometricId(String identityBiometricId) {
    this.identityBiometricId = identityBiometricId;
  }
}
