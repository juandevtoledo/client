package com.lulobank.clients.starter.v3.adapters.out.dynamo.dto;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
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

    private String idClient;
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
    private List<Attachment> attachments;
    private String idCognito;
    private String idKeycloak;
    private String idCbs;
    private String idCbsHash;
    private String blackListState;
    private LocalDateTime blackListDate;
    private String blackListRiskLevel;
    private LocalDate dateOfIssue;
    private LocalDate birthDate;
    private List<ForeignTransaction> foreignTransactions;
    private EconomicInformation economicInformation;
    private OnBoardingStatus onBoardingStatus;
    private CreditRiskAnalysis creditRiskAnalysis;
    private ApprovedRiskAnalysis approvedRiskAnalysis;
    private String gender;
    private String documentIssuedBy;
    private String typeDocument;
    private String expirationDate;
    private LoanRequested loanRequested;
    private IdentityBiometric identityBiometric;
    private AdditionalPersonalInformation additionalPersonalInformation;
    private Boolean resetBiometric;
    private Boolean digitalStorageStatus;
    private Boolean catsDocumentStatus;
    private ClientAcceptance acceptances;
    private boolean customerCreatedStatus;
    private boolean identityProcessed;
    private boolean economicProcessed;
    private String pep;
    private LocalDateTime dateResponsePep;
    private FatcaInformation fatcaInformation;
    private String identityBiometricId;
    private Integer value;

    @DynamoDBHashKey(attributeName = "idClient")
    public String getIdClient() {
        return idClient;
    }

    public void setIdClient(String idClient) {
        this.idClient = idClient;
    }

    @DynamoDBAttribute(attributeName = "idCard")
    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    @DynamoDBTypeConverted(converter = LocalDateTimeConverter.class)
    @DynamoDBAttribute(attributeName = "blackListDate")
    public LocalDateTime getBlackListDate() {
        return blackListDate;
    }

    public void setBlackListDate(LocalDateTime blackListDate) {
        this.blackListDate = blackListDate;
    }

    @DynamoDBAttribute(attributeName = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @DynamoDBAttribute(attributeName = "lastName")
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @DynamoDBAttribute(attributeName = "address")
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @DynamoDBAttribute(attributeName = "emailAddress")
    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    @DynamoDBAttribute(attributeName = "emailVerified")
    public Boolean getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    @DynamoDBAttribute(attributeName = "qualityCode")
    public String getQualityCode() {
        return qualityCode;
    }

    public void setQualityCode(String qualityCode) {
        this.qualityCode = qualityCode;
    }

    @DynamoDBAttribute(attributeName = "phonePrefix")
    public Integer getPhonePrefix() {
        return phonePrefix;
    }

    public void setPhonePrefix(Integer phonePrefix) {
        this.phonePrefix = phonePrefix;
    }

    @DynamoDBAttribute(attributeName = "phoneDeviceInfoIddevice")
    public String getPhoneDeviceInfoIddevice() {
        return phoneDeviceInfoIddevice;
    }

    public void setPhoneDeviceInfoIddevice(String phoneDeviceInfoIddevice) {
        this.phoneDeviceInfoIddevice = phoneDeviceInfoIddevice;
    }

    @DynamoDBAttribute(attributeName = "phoneDeviceInfoIpAddress")
    public String getPhoneDeviceInfoIpAddress() {
        return phoneDeviceInfoIpAddress;
    }

    public void setPhoneDeviceInfoIpAddress(String phoneDeviceInfoIpAddress) {
        this.phoneDeviceInfoIpAddress = phoneDeviceInfoIpAddress;
    }

    @DynamoDBAttribute(attributeName = "phoneDeviceInfoGeolocation")
    public String getPhoneDeviceInfoGeolocation() {
        return phoneDeviceInfoGeolocation;
    }

    public void setPhoneDeviceInfoGeolocation(String phoneDeviceInfoGeolocation) {
        this.phoneDeviceInfoGeolocation = phoneDeviceInfoGeolocation;
    }

    @DynamoDBAttribute(attributeName = "phoneDeviceInfoCountry")
    public String getPhoneDeviceInfoCountry() {
        return phoneDeviceInfoCountry;
    }

    public void setPhoneDeviceInfoCountry(String phoneDeviceInfoCountry) {
        this.phoneDeviceInfoCountry = phoneDeviceInfoCountry;
    }

    @DynamoDBAttribute(attributeName = "phoneDeviceInfoCity")
    public String getPhoneDeviceInfoCity() {
        return phoneDeviceInfoCity;
    }

    public void setPhoneDeviceInfoCity(String phoneDeviceInfoCity) {
        this.phoneDeviceInfoCity = phoneDeviceInfoCity;
    }

    @DynamoDBAttribute(attributeName = "phoneNumber")
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @DynamoDBAttribute(attributeName = "phoneVerified")
    public Boolean getPhoneVerified() {
        return phoneVerified;
    }

    public void setPhoneVerified(Boolean phoneVerified) {
        this.phoneVerified = phoneVerified;
    }

    @DynamoDBAttribute(attributeName = "phoneDeviceInfoMobileDevice")
    public String getPhoneDeviceInfoMobileDevice() {
        return phoneDeviceInfoMobileDevice;
    }

    public void setPhoneDeviceInfoMobileDevice(String phoneDeviceInfoMobileDevice) {
        this.phoneDeviceInfoMobileDevice = phoneDeviceInfoMobileDevice;
    }

    @DynamoDBAttribute(attributeName = "phoneDeviceInfoSimCardId")
    public String getPhoneDeviceInfoSimCardId() {
        return phoneDeviceInfoSimCardId;
    }

    public void setPhoneDeviceInfoSimCardId(String phoneDeviceInfoSimCardId) {
        this.phoneDeviceInfoSimCardId = phoneDeviceInfoSimCardId;
    }

    @DynamoDBAttribute(attributeName = "phoneDeviceInfoModel")
    public String getPhoneDeviceInfoModel() {
        return phoneDeviceInfoModel;
    }

    public void setPhoneDeviceInfoModel(String phoneDeviceInfoModel) {
        this.phoneDeviceInfoModel = phoneDeviceInfoModel;
    }

    @DynamoDBAttribute(attributeName = "phoneDeviceInfoOperator")
    public String getPhoneDeviceInfoOperator() {
        return phoneDeviceInfoOperator;
    }

    public void setPhoneDeviceInfoOperator(String phoneDeviceInfoOperator) {
        this.phoneDeviceInfoOperator = phoneDeviceInfoOperator;
    }

    @DynamoDBAttribute(attributeName = "attachments")
    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    @DynamoDBAttribute(attributeName = "idCognito")
    public String getIdCognito() {
        return idCognito;
    }

    public void setIdCognito(String idCognito) {
        this.idCognito = idCognito;
    }

    @DynamoDBAttribute(attributeName = "idKeycloak")
    public String getIdKeycloak() {
        return idKeycloak;
    }

    public void setIdKeycloak(String idKeycloak) {
        this.idKeycloak = idKeycloak;
    }

    @DynamoDBAttribute(attributeName = "idCbs")
    public String getIdCbs() {
        return idCbs;
    }

    public void setIdCbs(String idCbs) {
        this.idCbs = idCbs;
    }

    @DynamoDBAttribute(attributeName = "idCbsHash")
    public String getIdCbsHash() {
        return idCbsHash;
    }

    public void setIdCbsHash(String idCbsHash) {
        this.idCbsHash = idCbsHash;
    }

    @DynamoDBAttribute(attributeName = "blackListState")
    public String getBlackListState() {
        return blackListState;
    }

    public void setBlackListState(String blackListState) {
        this.blackListState = blackListState;
    }

    @DynamoDBAttribute(attributeName = "dateOfIssue")
    @DynamoDBTypeConverted(converter = LocalDateConverter.class)
    public LocalDate getDateOfIssue() {
        return dateOfIssue;
    }

    public void setDateOfIssue(LocalDate dateOfIssue) {
        this.dateOfIssue = dateOfIssue;
    }

    @DynamoDBAttribute(attributeName = "foreignTransactions")
    public List<ForeignTransaction> getForeignTransactions() {
        return foreignTransactions;
    }

    public void setForeignTransactions(List<ForeignTransaction> foreignTransactions) {
        this.foreignTransactions = foreignTransactions;
    }

    @DynamoDBAttribute(attributeName = "onBoardingStatus")
    public OnBoardingStatus getOnBoardingStatus() {
        return onBoardingStatus;
    }

    public void setOnBoardingStatus(OnBoardingStatus onBoardingStatus) {
        this.onBoardingStatus = onBoardingStatus;
    }

    @DynamoDBAttribute(attributeName = "economicInformation")
    public EconomicInformation getEconomicInformation() {
        return economicInformation;
    }

    public void setEconomicInformation(EconomicInformation economicInformation) {
        this.economicInformation = economicInformation;
    }

    @DynamoDBAttribute(attributeName = "creditRiskAnalysis")
    public CreditRiskAnalysis getCreditRiskAnalysis() {
        return creditRiskAnalysis;
    }

    public void setCreditRiskAnalysis(CreditRiskAnalysis creditRiskAnalysis) {
        this.creditRiskAnalysis = creditRiskAnalysis;
    }

    @DynamoDBAttribute(attributeName = "approvedRiskAnalysis")
    public ApprovedRiskAnalysis getApprovedRiskAnalysis() {
        return approvedRiskAnalysis;
    }

    public void setApprovedRiskAnalysis(ApprovedRiskAnalysis approvedRiskAnalysis) {
        this.approvedRiskAnalysis = approvedRiskAnalysis;
    }

    @DynamoDBAttribute(attributeName = "gender")
    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @DynamoDBAttribute(attributeName = "documentIssuedBy")
    public String getDocumentIssuedBy() {
        return documentIssuedBy;
    }

    public void setDocumentIssuedBy(String documentIssuedBy) {
        this.documentIssuedBy = documentIssuedBy;
    }

    @DynamoDBAttribute(attributeName = "typeDocument")
    public String getTypeDocument() {
        return typeDocument;
    }

    public void setTypeDocument(String typeDocument) {
        this.typeDocument = typeDocument;
    }

    @DynamoDBAttribute(attributeName = "expirationDate")
    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    @DynamoDBAttribute(attributeName = "loanRequested")
    public LoanRequested getLoanRequested() {
        return loanRequested;
    }

    public void setLoanRequested(LoanRequested loanRequested) {
        this.loanRequested = loanRequested;
    }

    @DynamoDBAttribute(attributeName = "department")
    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @DynamoDBAttribute(attributeName = "city")
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @DynamoDBAttribute(attributeName = "identityBiometric")
    public IdentityBiometric getIdentityBiometric() {
        return identityBiometric;
    }

    public void setIdentityBiometric(IdentityBiometric identityBiometric) {
        this.identityBiometric = identityBiometric;
    }

    @DynamoDBAttribute(attributeName = "birthDate")
    @DynamoDBTypeConverted(converter = LocalDateConverter.class)
    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    @DynamoDBAttribute(attributeName = "additionalPersonalInformation")
    public AdditionalPersonalInformation getAdditionalPersonalInformation() {
        return additionalPersonalInformation;
    }

    public void setAdditionalPersonalInformation(
            AdditionalPersonalInformation additionalPersonalInformation) {
        this.additionalPersonalInformation = additionalPersonalInformation;
    }

    @DynamoDBAttribute(attributeName = "resetBiometric")
    public Boolean getResetBiometric() {
        return resetBiometric;
    }

    public void setResetBiometric(Boolean resetBiometric) {
        this.resetBiometric = resetBiometric;
    }

    @DynamoDBAttribute(attributeName = "addressPrefix")
    public String getAddressPrefix() {
        return addressPrefix;
    }

    public void setAddressPrefix(String addressPrefix) {
        this.addressPrefix = addressPrefix;
    }

    @DynamoDBAttribute(attributeName = "addressComplement")
    public String getAddressComplement() {
        return addressComplement;
    }

    public void setAddressComplement(String addressComplement) {
        this.addressComplement = addressComplement;
    }

    @DynamoDBAttribute(attributeName = "cityId")
    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    @DynamoDBAttribute(attributeName = "departmentId")
    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    @DynamoDBAttribute(attributeName = "digitalStorageStatus")
    public Boolean getDigitalStorageStatus() {
        return digitalStorageStatus;
    }

    public void setDigitalStorageStatus(Boolean digitalStorageStatus) {
        this.digitalStorageStatus = digitalStorageStatus;
    }

    @DynamoDBAttribute(attributeName = "catsDocumentStatus")
    public Boolean getCatsDocumentStatus() {return catsDocumentStatus;}

    public void setCatsDocumentStatus(Boolean catsDocumentStatus){
        this.catsDocumentStatus = catsDocumentStatus;
    }

    @DynamoDBAttribute(attributeName = "acceptances")
    public ClientAcceptance getAcceptances() {
        return acceptances;
    }

    public void setAcceptances(ClientAcceptance acceptances) {
        this.acceptances = acceptances;
    }

    @DynamoDBAttribute(attributeName = "customerCreatedStatus")
    public Boolean getCustomerCreatedStatus() {
        return customerCreatedStatus;
    }

    public void setCustomerCreatedStatus(Boolean customerCreatedStatus) {
        this.customerCreatedStatus = customerCreatedStatus;
    }

    @DynamoDBAttribute(attributeName = "code")
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @DynamoDBAttribute(attributeName = "identityProcessed")
    public boolean isIdentityProcessed() {
        return identityProcessed;
    }

    public void setIdentityProcessed(boolean identityProcessed) {
        this.identityProcessed = identityProcessed;
    }

    @DynamoDBAttribute(attributeName = "economicProcessed")
    public boolean isEconomicProcessed() {
        return economicProcessed;
    }

    public void setEconomicProcessed(boolean economicProcessed) {
        this.economicProcessed = economicProcessed;
    }

    @DynamoDBAttribute(attributeName = "pep")
    public String getPep() {
        return pep;
    }

    public void setPep(String pep) {
        this.pep = pep;
    }

    @DynamoDBTypeConverted(converter = LocalDateTimeConverter.class)
    @DynamoDBAttribute(attributeName = "dateResponsePep")
    public LocalDateTime getDateResponsePep() {
        return dateResponsePep;
    }

    public void setDateResponsePep(LocalDateTime dateResponsePep) {
        this.dateResponsePep = dateResponsePep;
    }

    @DynamoDBAttribute(attributeName = "fatcaInformation")
    public FatcaInformation getFatcaInformation() {
        return fatcaInformation;
    }

    public void setFatcaInformation(FatcaInformation fatcaInformation) {
        this.fatcaInformation = fatcaInformation;
    }

    @DynamoDBAttribute(attributeName = "blackListRiskLevel")
    public String getBlackListRiskLevel() {
        return blackListRiskLevel;
    }

    public void setBlackListRiskLevel(String blackListRiskLevel) {
        this.blackListRiskLevel = blackListRiskLevel;
    }
    
    @DynamoDBAttribute(attributeName = "identityBiometricId")
    public String getIdentityBiometricId() {
    	return identityBiometricId;
    }
    
    public void setIdentityBiometricId(String identityBiometricId) {
        this.identityBiometricId = identityBiometricId;
    }

    @DynamoDBAttribute(attributeName = "value")
    public Integer getValue() { return value; }

    public void setValue(Integer value) { this.value = value; }
}
