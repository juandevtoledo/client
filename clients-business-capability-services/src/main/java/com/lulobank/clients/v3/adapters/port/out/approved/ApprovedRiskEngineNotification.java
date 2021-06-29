package com.lulobank.clients.v3.adapters.port.out.approved;

import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;

public interface ApprovedRiskEngineNotification {
	void notifyRiskEngineResponse(ClientsV3Entity clientsV3Entity);
}
