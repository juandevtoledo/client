package com.lulobank.clients.services.features.clientverificationresult;

import com.lulobank.clients.sdk.operations.util.CheckPoints;
import com.lulobank.clients.services.events.ClientVerificationResult;
import com.lulobank.clients.services.features.clientverificationresult.mapper.ClientEntityMapper;
import com.lulobank.clients.services.outboundadapters.ClientsOutboundAdapter;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.clients.v3.adapters.port.out.riskengine.RiskEngineService;
import com.lulobank.clients.v3.adapters.port.out.riskengine.dto.ValidateClientWLRequest;

import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.CustomLog;

import java.util.function.Function;

import static com.lulobank.clients.services.features.clientverificationresult.NotificationHelper.notifyPepValidation;

@CustomLog
public class NonBlacklistedProcessService {

    private final ClientsOutboundAdapter clientsOutboundAdapter;
    private final ClientsV3Repository clientsV3Repository;
    private final RiskEngineService riskEngineService;

    public NonBlacklistedProcessService(ClientsOutboundAdapter clientsOutboundAdapter,
                                        ClientsV3Repository clientsV3Repository,
                                        RiskEngineService riskEngineService) {
        this.clientsOutboundAdapter = clientsOutboundAdapter;
        this.clientsV3Repository = clientsV3Repository;
        this.riskEngineService = riskEngineService;
    }
    
    public void emitEvent(ClientsV3Entity clientEntity, ClientVerificationResult payload){
        Option.of(clientEntity)
                .peek(client -> log.info("Client with idCard: {} is NON_BLACKLISTED. IdTransactionBiometric: {}, checkpoint: {}",
                        clientEntity.getIdCard(), payload.getIdTransactionBiometric(),
                        CheckPoints.BLACKLIST_FINISHED.name()))
                .map(ok -> mapNonBlacklistResult(clientEntity, payload))
                .flatMap(this::setIdentityProcessed)
                .flatMap(this::sendValidateClientWL);
    }
    
    private Option<ClientsV3Entity> sendValidateClientWL(ClientsV3Entity clientsV3Entity) {
    	return riskEngineService.sendValidateClientWLMessage(buildValidateClientWLRequest(clientsV3Entity))
    			.onFailure(error -> log.error(String.format("[NonBlacklistedProcessService] Error sending message to Risk Engine: %s", error.getMessage())))
    			.map(v -> clientsV3Entity)
    			.toOption();
    }
    
    private Option<ClientsV3Entity> setIdentityProcessed(ClientsV3Entity clientsV3Entity) {
    	clientsV3Entity.setIdentityProcessed(true);
    	return clientsV3Repository.save(clientsV3Entity)
    			.onFailure(error -> log.error(String.format("[NonBlacklistedProcessService] Error saving clientsEntity: %s", error.getMessage())))
    			.toOption();
    }

    private ValidateClientWLRequest buildValidateClientWLRequest(ClientsV3Entity entity) {
		return ValidateClientWLRequest.builder()
				.documentNumber(entity.getIdCard())
				.documentType(entity.getTypeDocument())
				.build();
	}

	private ClientsV3Entity setCheckpoint(ClientsV3Entity clientEntity) {
        clientEntity.getOnBoardingStatus().setCheckpoint(CheckPoints.BLACKLIST_FINISHED.name());
        return clientEntity;
    }

    private ClientsV3Entity mapNonBlacklistResult(ClientsV3Entity clientEntity, ClientVerificationResult payload) {
        return Try.of(() -> ClientEntityMapper.INSTANCE.clientEntityFrom(payload, clientEntity))
                .onSuccess(this::processClient)
                .map(Function.identity())
                .onFailure(ex -> log.error("Error in mapper process, msg: {}, IdTransactionBiometric: {}, idClient: {}, exceptionMessage: {}",
                        ex.getMessage(), clientEntity.getIdentityBiometric().getIdTransaction(),
                        clientEntity.getIdClient(), ex.getMessage()))
                .getOrElse(clientEntity);
    }

    private void processClient(ClientsV3Entity clientsV3Entity) {
        Option.of(clientsV3Entity)
                .peek(this::setCheckpoint)
                .peek(notifyPepValidation(clientsOutboundAdapter))
                .peek(client -> clientsV3Repository.updateOnBoarding(client));
    }
}