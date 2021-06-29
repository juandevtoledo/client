package com.lulobank.clients.v3.usecase.productoffers;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.lulobank.clients.v3.error.ClientsDataError;
import com.lulobank.clients.services.domain.error.UseCaseResponseError;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ApprovedRiskAnalysisV3;
import org.springframework.context.i18n.LocaleContextHolder;

import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.OfferState;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.RiskOfferV3;
import com.lulobank.clients.v3.service.productoffers.ProductOfferValidatorService;
import com.lulobank.clients.v3.usecase.productoffers.command.ClientProductOffer;
import com.lulobank.clients.v3.usecase.productoffers.command.GetClientProductOfferRequest;
import com.lulobank.clients.v3.util.UseCase;

import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.CustomLog;

@CustomLog
public class GetClientProductOfferUseCase
		implements UseCase<GetClientProductOfferRequest, Either<UseCaseResponseError, ClientProductOffer>> {

	private final ClientsV3Repository clientsV3Repository;
	private final Map<String, ProductOfferValidatorService> productOfferValidators;
	private final Map<String, List<String>> productOfferValidatorsEnabled;
	private final Map<String, Integer> expiredDays;
	private final Map<String, String> descriptions;
	private final Map<String, String> additionalInfo;

	public GetClientProductOfferUseCase(ClientsV3Repository clientsV3Repository,
			Map<String, ProductOfferValidatorService> productOfferValidators,
			Map<String, List<String>> productOfferTypeValidators,
			Map<String, Integer> expiredDays,
			Map<String, String> descriptions,
			Map<String, String> additionalInfo) {
		this.clientsV3Repository = clientsV3Repository;
		this.productOfferValidators = productOfferValidators;
		this.productOfferValidatorsEnabled = productOfferTypeValidators;
		this.expiredDays = expiredDays;
		this.descriptions = descriptions;
		this.additionalInfo = additionalInfo;
	}

	@Override
	public Either<UseCaseResponseError, ClientProductOffer> execute(GetClientProductOfferRequest command) {
		log.info("[GetClientProductOfferUseCase] execute()");
		return clientsV3Repository.findByIdClient(command.getIdClient())
				.peek(entity -> log.info(String.format("[GetClientProductOfferUseCase] Client found: %s", entity.getIdClient())))
				.flatMap(entity -> getRiskOffer(entity, command))
				.onEmpty(() -> log.info(String.format("[GetClientProductOfferUseCase] There are not productOffer for client: %s", command.getIdClient())))
				.map(this::mapRiskOffer)
                .toEither(ClientsDataError.clientNotFound());
	}

	private ClientProductOffer mapRiskOffer(RiskOfferV3 riskOfferV3) {
        String type = riskOfferV3.getType();
        return ClientProductOffer.builder()
				.idProductOffer(riskOfferV3.getIdProductOffer())
				.state(riskOfferV3.getState().name())
				.type(type)
				.expiredDays(expiredDays.getOrDefault(toKeyFormat(type), 30))
				.description(descriptions.getOrDefault(toKeyFormat(type), null))
				.additionalInfo(additionalInfo.getOrDefault(toKeyFormat(type), null))
				.value(riskOfferV3.getValue())
				.build();
	}

	private Option<RiskOfferV3> getRiskOffer(ClientsV3Entity clientsV3Entity, GetClientProductOfferRequest getClientProductOfferRequest) {
		return Option.of(clientsV3Entity.getApprovedRiskAnalysis())
				.map(ApprovedRiskAnalysisV3::getResults)
				.flatMap(results -> findRiskOffer(results, clientsV3Entity, getClientProductOfferRequest));
	}

	private Option<RiskOfferV3> findRiskOffer(List<RiskOfferV3> results, ClientsV3Entity clientsV3Entity, GetClientProductOfferRequest getClientProductOfferRequest) {
		return Option.ofOptional(results.stream()
				.filter(result -> OfferState.ACTIVE_STATES.contains(result.getState()))
				.sorted(offerDate)
				.filter(this::validateByExpirationDate)
				.filter(result -> isValidOfferByType(result, clientsV3Entity, getClientProductOfferRequest))
				.findFirst());
	}
	
	private boolean validateByExpirationDate(RiskOfferV3 riskOfferV3) {
        String type = riskOfferV3.getType();
        log.info(String.format("[GetClientProductOfferUseCase] ProductOffer type: %s", type));
		return riskOfferV3.getOfferDate()
				.plusDays(expiredDays.getOrDefault(toKeyFormat(type), 30))
				.isAfter(LocalDateTime.now());
	}

    private boolean isValidOfferByType(RiskOfferV3 riskOfferV3, ClientsV3Entity clientsV3Entity,
                                   GetClientProductOfferRequest getClientProductOfferRequest) {
    	String type = riskOfferV3.getType();
        log.info(String.format("[GetClientProductOfferUseCase] ProductOffer type: %s", type));
        io.vavr.collection.List<String> list = io.vavr.collection.List.ofAll(productOfferValidatorsEnabled.getOrDefault(toKeyFormat(type),
                Collections.emptyList()));
        return list
                .peek(validator -> log.info(String.format("[GetClientProductOfferUseCase] Validator: %s", validator)))
                .map(productOfferValidators::get)
                .filter(Objects::nonNull)
                .filter(validator -> !validator.validateProductOffer(clientsV3Entity, getClientProductOfferRequest.getAuth()))
                .peek(e -> log.info(String.format("[GetClientProductOfferUseCase] ValidatorError: %s", e)))
                .map(e -> false)
                .headOption()
                .getOrElse(true);
	}
    
    private String toKeyFormat(String type) {
        return type.toLowerCase(LocaleContextHolder.getLocale());
    }

	private static final Comparator<RiskOfferV3> offerDate = Comparator.comparing(RiskOfferV3::getOfferDate);
}
