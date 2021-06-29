package com.lulobank.clients.starter.v3.adapters.out.approvedriskengine;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.lulobank.clients.services.events.EventV2;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.AdditionalPersonalInfoV3;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ApprovedRiskAnalysisV3;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.RiskOfferV3;

public class ApprovedRiskEngineEventTest {
	
	private ApprovedRiskEngineEvent subject;
	
	@Before
	public void setup() {
		subject  = new ApprovedRiskEngineEvent("endpoint");
	}
	
	@Test
	public void shouldMapSuccess() {
		String idProduct = "idProductOffer";
		ClientsV3Entity clientsV3Entity = buildClientsV3Entity(buildApprovedRiskAnalysis(idProduct));
		EventV2<RiskEngineResponseMessage> result = subject.map(clientsV3Entity);
		assertEquals(result.getPayload().getIdProductOffer(), idProduct);
		assertEquals(result.getPayload().getClientInformation().getDocumentId().getId(), clientsV3Entity.getIdCard());
		assertEquals(result.getPayload().getClientInformation().getName(), clientsV3Entity.getAdditionalPersonalInformation().getFirstName());
	}

	private ClientsV3Entity buildClientsV3Entity(ApprovedRiskAnalysisV3 approvedRiskAnalysisV3) {
		ClientsV3Entity clientsV3Entity = new ClientsV3Entity();
		clientsV3Entity.setIdCard("idCard");
		clientsV3Entity.setAdditionalPersonalInformation(buildAdditionalPersonalInformation());
		clientsV3Entity.setApprovedRiskAnalysis(approvedRiskAnalysisV3);
		return clientsV3Entity;
	}

	private ApprovedRiskAnalysisV3 buildApprovedRiskAnalysis(String idProduct) {
		RiskOfferV3 riskOfferV3 = new RiskOfferV3();
		riskOfferV3.setIdProductOffer(idProduct);
		List<RiskOfferV3> riskOffer = new ArrayList<>();
		riskOffer.add(riskOfferV3);
		ApprovedRiskAnalysisV3 approvedRiskAnalysisV3 = new ApprovedRiskAnalysisV3();
		approvedRiskAnalysisV3.setResults(riskOffer);
		return approvedRiskAnalysisV3;
	}

	private AdditionalPersonalInfoV3 buildAdditionalPersonalInformation() {
		AdditionalPersonalInfoV3 additionalPersonalInfoV3 = new AdditionalPersonalInfoV3();
		additionalPersonalInfoV3.setFirstName("firstName");
		additionalPersonalInfoV3.setSecondName("secondName");
		additionalPersonalInfoV3.setFirstSurname("firstSurname");
		additionalPersonalInfoV3.setSecondSurname("secondSurname");
		return additionalPersonalInfoV3;
	}

}
