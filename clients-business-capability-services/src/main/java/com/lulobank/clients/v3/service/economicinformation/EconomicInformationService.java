package com.lulobank.clients.v3.service.economicinformation;

import com.lulobank.clients.sdk.operations.dto.economicinformation.ClientEconomicInformation;
import com.lulobank.clients.sdk.operations.dto.economicinformation.ForeignCurrencyTransaction;
import com.lulobank.clients.sdk.operations.dto.economicinformation.OccupationType;
import com.lulobank.clients.services.ports.out.ParameterService;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.*;
import com.lulobank.parameters.sdk.dto.parameters.ParameterResponse;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.CustomLog;
import org.apache.commons.lang3.StringUtils;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Collections.emptyMap;

@CustomLog
public class EconomicInformationService {

    private static final String RISK_LEVEL_KEY = "riskLevel";
    private static final String RISK_LEVEL_MONITOR_KEY = "monitorRiskLevel";

    private final ClientsV3Repository clientsRepository;
    private final ParameterService parameterService;

    public EconomicInformationService(ClientsV3Repository clientsRepository,
                                      ParameterService parameterService) {

        this.clientsRepository = clientsRepository;
        this.parameterService = parameterService;
    }


    public Try< ClientsV3Entity> build(ClientEconomicInformation command) {
        return clientsRepository.findByIdClient(command.getIdClient()).toTry()
                .map(client -> setEconomicInformation(client, command))
                .map(client -> setForeignTransactions(client, command))
                .map(client -> setEconomicActivity(client, command))
                .map(client -> setEconomicActivityRiskLevel(client, command));
    }

    private ClientsV3Entity setEconomicActivity(ClientsV3Entity clientEntity, ClientEconomicInformation command) {
        if (OccupationType.EMPLOYEE.equals(command.getOccupationType())) {
            clientEntity.getEconomicInformation().setEconomicActivity(OccupationType.EMPLOYEE.getCode());
        } else if (OccupationType.RETIRED.equals(command.getOccupationType())) {
            clientEntity.getEconomicInformation().setEconomicActivity(OccupationType.RETIRED.getCode());
        }
        return clientEntity;
    }

    private ClientsV3Entity setEconomicActivityRiskLevel(ClientsV3Entity clientEntity, ClientEconomicInformation command) {
        getEconomicActivityRiskLevels(command).peek(levels -> {
            clientEntity.getEconomicInformation().setEconomicActivityRiskLevel(levels.get(RISK_LEVEL_KEY));
            clientEntity.getEconomicInformation().setEconomicActivityRiskLevelMonitor(levels.get(RISK_LEVEL_MONITOR_KEY));
        });
        return clientEntity;
    }

    private ClientsV3Entity setForeignTransactions(ClientsV3Entity clientEntity, ClientEconomicInformation command) {

        Option.of(command.getForeignCurrencyTransactions())
                .peek(transactions -> clientEntity.setForeignTransactions(buildForeignTransactions(transactions)));

        return clientEntity;
    }

    private List<ForeignTransactionV3> buildForeignTransactions(List<ForeignCurrencyTransaction> transactions) {
        return transactions.parallelStream()
                .map(this::toForeignTransactionV3)
                .collect(Collectors.toList());
    }

    private ForeignTransactionV3 toForeignTransactionV3(ForeignCurrencyTransaction foreignCurrencyTransaction) {
        CheckingAccountV3 checkingAccountV3=new CheckingAccountV3();
        checkingAccountV3.setAmount(foreignCurrencyTransaction.getCheckingAccount().getAmount());
        checkingAccountV3.setBank(foreignCurrencyTransaction.getCheckingAccount().getBank());
        checkingAccountV3.setCity(foreignCurrencyTransaction.getCheckingAccount().getCity());
        checkingAccountV3.setCountry(foreignCurrencyTransaction.getCheckingAccount().getCountry());
        checkingAccountV3.setCurrency(foreignCurrencyTransaction.getCheckingAccount().getCurrency());
        checkingAccountV3.setNumber(foreignCurrencyTransaction.getCheckingAccount().getNumber());

        ForeignTransactionV3 foreignTransactionV3=new ForeignTransactionV3();
        foreignTransactionV3.setName(foreignCurrencyTransaction.getName());
        foreignTransactionV3.setCheckingAccount(checkingAccountV3);
        return foreignTransactionV3;
    }

    private ClientsV3Entity setEconomicInformation(ClientsV3Entity clientEntity, ClientEconomicInformation command) {
        CompanyV3 companyV3=new CompanyV3();
        companyV3.setName(command.getEmployeeCompany().getName());
        companyV3.setCity(command.getEmployeeCompany().getCity());
        companyV3.setState(command.getEmployeeCompany().getState());

        EconomicInformationV3 economicInformationV3 =new EconomicInformationV3();
        economicInformationV3.setOccupationType(command.getOccupationType().name());
        economicInformationV3.setAdditionalIncome(command.getAdditionalIncome());
        economicInformationV3.setAssets(command.getAssets());
        economicInformationV3.setCompany(companyV3);
        economicInformationV3.setEconomicActivity(command.getEconomicActivity());
        economicInformationV3.setLiabilities(command.getLiabilities());
        economicInformationV3.setMonthlyIncome(command.getMonthlyIncome());
        economicInformationV3.setMonthlyOutcome(command.getMonthlyOutcome());
        economicInformationV3.setSavingPurpose(command.getSavingPurpose());
        economicInformationV3.setTypeSaving(command.getTypeSaving());

        clientEntity.setEconomicInformation(economicInformationV3);
        clientEntity.setEconomicProcessed(true);
        return clientEntity;
    }

    private Option<Map<String, String>> getEconomicActivityRiskLevels(final ClientEconomicInformation request) {
        return Try.of(request::getEconomicActivity)
                .filter(StringUtils::isNotEmpty)
                .flatMap(economicActivity -> parameterService.getParameterByKey(request.getAuthorizationHeader(),
                        request.getIdClient(), economicActivity))
                .filter(Objects::nonNull)
                .map(ParameterResponse::getContent)
                .map(paramList -> paramList.stream().findFirst().orElse(emptyMap()))
                .peek(param -> log.info("Parameter response: {}", param))
                .onFailure(e -> log.error("Error getting risk for economic activity: {}", e.toString()))
                .toOption();
    }
}
