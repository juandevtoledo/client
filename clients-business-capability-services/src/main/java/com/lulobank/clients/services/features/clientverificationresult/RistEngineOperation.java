package com.lulobank.clients.services.features.clientverificationresult;

import com.lulobank.clients.services.features.RetriesOption;
import com.lulobank.clients.v3.adapters.port.out.approved.ApprovedRiskEngineNotification;

import lombok.Getter;

@Getter
public class RistEngineOperation {
	
	private final RetriesOption riskEngineRetriesOption;
	private final ApprovedRiskEngineNotification approvedRiskEngineNotification;

	public RistEngineOperation(RetriesOption riskEngineRetriesOption, ApprovedRiskEngineNotification approvedRiskEngineNotification) {
		this.riskEngineRetriesOption = riskEngineRetriesOption;
		this.approvedRiskEngineNotification = approvedRiskEngineNotification;
	}
}
