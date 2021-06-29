package com.lulobank.clients.v3.adapters.port.out.dynamo.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import io.vavr.control.Option;

@Getter
@Setter
public class ClientsV3Entity {

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
    private List<AttachmentV3> attachments;
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
    private List<ForeignTransactionV3> foreignTransactions;
    private EconomicInformationV3 economicInformation;
    private OnBoardingStatusV3 onBoardingStatus;
    private CreditRiskAnalysisV3 creditRiskAnalysis;
    private ApprovedRiskAnalysisV3 approvedRiskAnalysis;
    private String gender;
    private String documentIssuedBy;
    private String typeDocument;
    private String expirationDate;
    private LoanRequestedV3 loanRequested;
    private IdentityBiometricV3 identityBiometric;
    private AdditionalPersonalInfoV3 additionalPersonalInformation;
    private Boolean resetBiometric;
    private boolean digitalStorageStatus;
    private boolean catsDocumentStatus;
    private ClientAcceptanceV3 acceptances;
    private boolean customerCreatedStatus;
    private boolean identityProcessed;
    private boolean economicProcessed;
    private String pep;
    private LocalDateTime dateResponsePep;
    private FatcaInformationV3 fatcaInformation;
    private String identityBiometricId;
    private Integer value;
    private String idSavingAccount;
    
	public ApprovedRiskAnalysisV3 getApprovedRiskAnalysis() {
		Option.of(approvedRiskAnalysis)
			.onEmpty(() -> approvedRiskAnalysis = new ApprovedRiskAnalysisV3());
		return approvedRiskAnalysis;
	}
}
