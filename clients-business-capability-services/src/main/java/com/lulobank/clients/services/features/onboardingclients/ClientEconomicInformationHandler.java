package com.lulobank.clients.services.features.onboardingclients;

import com.lulobank.clients.sdk.operations.dto.economicinformation.ClientEconomicInformation;
import com.lulobank.clients.services.exception.ClientNotFoundException;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.outboundadapters.repository.ClientsRepository;
import com.lulobank.clients.services.ports.out.ParameterService;
import com.lulobank.clients.services.utils.ConverterObjectUtils;
import com.lulobank.core.Handler;
import com.lulobank.core.Response;
import com.lulobank.parameters.sdk.dto.parameters.ParameterResponse;
import io.vavr.control.Option;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.lulobank.clients.services.utils.ClientErrorResultsEnum.CLIENT_NOT_FOUND_IN_DB;
import static com.lulobank.clients.services.utils.ConverterObjectUtils.getEconomicInfoToDynamo;
import static java.util.Collections.emptyMap;

@Slf4j
public class ClientEconomicInformationHandler implements Handler<Response, ClientEconomicInformation> {
    private static final String RISK_LEVEL_KEY = "riskLevel";
    private static final String RISK_LEVEL_MONITOR_KEY = "monitorRiskLevel";

    private ClientsRepository clientsRepository;
    private ParameterService parameterService;

    public ClientEconomicInformationHandler(ClientsRepository clientsRepository, ParameterService parameterService) {
        this.clientsRepository = clientsRepository;
        this.parameterService = parameterService;
    }

    @Override
    public Response handle(ClientEconomicInformation request) {
        ClientEntity clientEntity = clientsRepository.findByIdClient(request.getIdClient())
                .orElseThrow(() -> new ClientNotFoundException(CLIENT_NOT_FOUND_IN_DB.name(), request.getIdClient()));
        clientEntity.setEconomicInformation(getEconomicInfoToDynamo(request));
        clientEntity.setEconomicProcessed(true);

        if (Objects.nonNull(request.getForeignCurrencyTransactions())) {
            clientEntity.setForeignTransactions(request.getForeignCurrencyTransactions().stream()
                    .map(ConverterObjectUtils::getForeignTransactionInfoToDynamo)
                    .collect(Collectors.toList()));
        }

        getEconomicActivityRiskLevels(request).peek(levels -> {
            clientEntity.getEconomicInformation().setEconomicActivityRiskLevel(levels.get(RISK_LEVEL_KEY));
            clientEntity.getEconomicInformation().setEconomicActivityRiskLevelMonitor(levels.get(RISK_LEVEL_MONITOR_KEY));
        });
        ClientEntity response = clientsRepository.save(clientEntity);
        return new Response<>(response);
    }

    private Option<Map<String, String>> getEconomicActivityRiskLevels(final ClientEconomicInformation request) {
        return Option.of(request.getEconomicActivity()).filter(StringUtils::isNotEmpty).toTry()
                .flatMap(economicActivity -> parameterService.getParameterByKey(request.getAuthorizationHeader(),
                        request.getIdClient(), economicActivity))
                .onFailure(e -> log.error("Error getting risk for economic activity: {}", e.toString()))
                .toOption()
                .filter(Objects::nonNull)
                .map(ParameterResponse::getContent)
                .map(paramList -> paramList.stream().findFirst().orElse(emptyMap()))
                .peek(param -> log.info("Parameter response: {}", param));
    }
}
