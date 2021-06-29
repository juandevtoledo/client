package com.lulobank.clients.v3.usecase.productoffers;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.lulobank.clients.services.domain.error.UseCaseResponseError;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ApprovedRiskAnalysisV3;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.OfferState;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.RiskOfferV3;
import com.lulobank.clients.v3.service.productoffers.ProductOfferValidatorService;
import com.lulobank.clients.v3.usecase.productoffers.command.ClientProductOffer;
import com.lulobank.clients.v3.usecase.productoffers.command.GetClientProductOfferRequest;

import io.vavr.control.Either;
import io.vavr.control.Option;

public class GetClientProductOfferUseCaseTest {

	private GetClientProductOfferUseCase subject;

	@Mock
	private ClientsV3Repository clientsV3Repository;
	@Mock
	private Map<String, ProductOfferValidatorService> productOfferValidators;
	@Mock
	private Map<String, List<String>> productOfferValidatorsEnabled;
	@Mock
	private Map<String, Integer> expiredDays;
	@Mock
	private Map<String, String> descriptions;
	@Mock
	private Map<String, String> additionalInfo;
	@Mock
	private ProductOfferValidatorService validatorService;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		subject = new GetClientProductOfferUseCase(clientsV3Repository, productOfferValidators,
				productOfferValidatorsEnabled, expiredDays, descriptions, additionalInfo);
	}

	@Test
	public void getClientProductOfferShouldReturnData() {
		GetClientProductOfferRequest command = buildGetClientProductOfferRequest();
		ClientsV3Entity clientsV3Entity = buildClientsV3Entity(buildApprovedRiskAnalysisV3(buildRiskOfferV3List(buildActiveRiskOfferV3())));
		String description = "description";
		String aditionalInfo = "aditionalInfo";
		int offerExpiredDays = 21;
		
		when(clientsV3Repository.findByIdClient(command.getIdClient())).thenReturn(Option.of(clientsV3Entity));
		when(expiredDays.getOrDefault(isA(String.class), any())).thenReturn(offerExpiredDays);
		when(productOfferValidatorsEnabled.getOrDefault(isA(String.class), any())).thenReturn(Arrays.asList("type"));
		when(productOfferValidators.getOrDefault(isA(String.class), any())).thenReturn(validatorService);
		when(descriptions.getOrDefault(isA(String.class), any())).thenReturn(description);
		when(additionalInfo.getOrDefault(isA(String.class), any())).thenReturn(aditionalInfo);
		when(validatorService.validateProductOffer(clientsV3Entity, command.getAuth())).thenReturn(true);
		
		Either<UseCaseResponseError, ClientProductOffer> response = subject.execute(command);
		assertThat(response.isRight(), is(true));
		assertThat(response.get(), notNullValue());
		assertThat(response.get().getDescription(), is(description));
		assertThat(response.get().getAdditionalInfo(), is(aditionalInfo));
		assertThat(response.get().getExpiredDays(), is(offerExpiredDays));
		
	}
	
	@Test
	public void getClientProductOfferShouldFilterByValidator() {
		GetClientProductOfferRequest command = buildGetClientProductOfferRequest();
		ClientsV3Entity clientsV3Entity = buildClientsV3Entity(buildApprovedRiskAnalysisV3(buildRiskOfferV3List(buildActiveRiskOfferV3())));
		String description = "description";
		String aditionalInfo = "aditionalInfo";
		int offerExpiredDays = 21;
		
		when(clientsV3Repository.findByIdClient(command.getIdClient())).thenReturn(Option.of(clientsV3Entity));
		when(expiredDays.getOrDefault(isA(String.class), any())).thenReturn(offerExpiredDays);
		when(productOfferValidatorsEnabled.getOrDefault(isA(String.class), any())).thenReturn(Arrays.asList("type"));
		when(productOfferValidators.getOrDefault(isA(String.class), any())).thenReturn(validatorService);
		when(descriptions.getOrDefault(isA(String.class), any())).thenReturn(description);
		when(additionalInfo.getOrDefault(isA(String.class), any())).thenReturn(aditionalInfo);
		when(validatorService.validateProductOffer(clientsV3Entity, command.getAuth())).thenReturn(false);
		
		Either<UseCaseResponseError, ClientProductOffer> response = subject.execute(command);
		assertThat(response.isRight(), is(true));
		assertThat(response.get(), notNullValue());
		assertThat(response.get().getDescription(), is(description));
		assertThat(response.get().getAdditionalInfo(), is(aditionalInfo));
		assertThat(response.get().getExpiredDays(), is(offerExpiredDays));
		
	}
	
	@Test
	public void getClientProductOfferShouldFilterByState() {
		GetClientProductOfferRequest command = buildGetClientProductOfferRequest();
		ClientsV3Entity clientsV3Entity = buildClientsV3Entity(buildApprovedRiskAnalysisV3(buildRiskOfferV3List(buildClosedRiskOfferV3())));
		int offerExpiredDays = 21;
		
		when(clientsV3Repository.findByIdClient(command.getIdClient())).thenReturn(Option.of(clientsV3Entity));
		when(expiredDays.getOrDefault(isA(String.class), any())).thenReturn(offerExpiredDays);
		
		Either<UseCaseResponseError, ClientProductOffer> response = subject.execute(command);
		assertThat(response.isLeft(), is(true));
	}
	
	@Test
	public void getClientProductOfferShouldFilterByExpiredDays() {
		GetClientProductOfferRequest command = buildGetClientProductOfferRequest();
		ClientsV3Entity clientsV3Entity = buildClientsV3Entity(buildApprovedRiskAnalysisV3(buildRiskOfferV3List(buildActiveRiskOfferV3())));
		int offerExpiredDays = 19;
		
		when(clientsV3Repository.findByIdClient(command.getIdClient())).thenReturn(Option.of(clientsV3Entity));
		when(expiredDays.getOrDefault(isA(String.class), any())).thenReturn(offerExpiredDays);
		
		Either<UseCaseResponseError, ClientProductOffer> response = subject.execute(command);
		assertThat(response.isLeft(), is(true));
		
	}
	
	private ClientsV3Entity buildClientsV3Entity(ApprovedRiskAnalysisV3 approvedRiskAnalysis) {
		ClientsV3Entity clientsV3Entity = new ClientsV3Entity();
		clientsV3Entity.setIdClient("idClient");
		clientsV3Entity.setApprovedRiskAnalysis(approvedRiskAnalysis);
		return clientsV3Entity;
	}
	
	private ApprovedRiskAnalysisV3 buildApprovedRiskAnalysisV3(List<RiskOfferV3> results) {
		ApprovedRiskAnalysisV3 approvedRiskAnalysisV3 = new ApprovedRiskAnalysisV3();
		approvedRiskAnalysisV3.setResults(results);
		return approvedRiskAnalysisV3;
	}
	
	private List<RiskOfferV3> buildRiskOfferV3List(RiskOfferV3 riskOfferV3) {
		List<RiskOfferV3> riskOfferV3s = new ArrayList<RiskOfferV3>();
		riskOfferV3s.add(riskOfferV3);
		return riskOfferV3s;
	}
	
	private RiskOfferV3 buildActiveRiskOfferV3() {
		return RiskOfferV3.builder()
				.state(OfferState.ACTIVE)
				.type("type1")
				.offerDate(LocalDateTime.now().minusDays(20))
				.build(); 
	}
	
	private RiskOfferV3 buildClosedRiskOfferV3() {
		return RiskOfferV3.builder()
				.state(OfferState.CLOSED)
				.type("type1")
				.offerDate(LocalDateTime.now().minusDays(20))
				.build(); 
	}

	private GetClientProductOfferRequest buildGetClientProductOfferRequest() {
		return GetClientProductOfferRequest.builder().idClient("idClient").auth(new HashMap<>()).build();
	}

}
