package com.lulobank.clients.v3.usecase.productoffers;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

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
import com.lulobank.clients.v3.usecase.productoffers.command.ClientProductOffer;
import com.lulobank.clients.v3.usecase.productoffers.command.CreateClientProductOfferRequest;

import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;

public class CreateClientProductOfferUseCaseTest {

	private CreateClientProductOfferUseCase createClientProductOfferUseCase;
	
	@Mock
	private ClientsV3Repository clientsV3Repository;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		createClientProductOfferUseCase = new CreateClientProductOfferUseCase(clientsV3Repository);
	}
	
	@Test
	public void createClientProductOfferShouldExecuteSuccess() {
		CreateClientProductOfferRequest createClientProductOfferRequest = buildCreateClientProductOfferRequest("type");
		ClientsV3Entity clientsV3Entity = buildClientsV3Entity();
		when(clientsV3Repository.findByIdClient(createClientProductOfferRequest.getIdClient())).thenReturn(Option.of(clientsV3Entity));
		when(clientsV3Repository.save(any())).thenReturn(Try.of(() -> clientsV3Entity));
		Either<UseCaseResponseError, ClientProductOffer> response = createClientProductOfferUseCase.execute(createClientProductOfferRequest);
		assertThat(response.isRight(), is(true));
		assertThat(response.get(), notNullValue());
		assertThat(response.get().getType(), is(createClientProductOfferRequest.getType()));
		assertThat(response.get().getValue(), is(createClientProductOfferRequest.getValue()));
	}
	
	@Test
	public void createClientProductOfferShouldFailed() {
		CreateClientProductOfferRequest createClientProductOfferRequest = buildCreateClientProductOfferRequest("type2");
		when(clientsV3Repository.findByIdClient(createClientProductOfferRequest.getIdClient())).thenReturn(Option.none());
		Either<UseCaseResponseError, ClientProductOffer> response = createClientProductOfferUseCase.execute(createClientProductOfferRequest);
		assertThat(response.isLeft(), is(true));
	}
	
	@Test
	public void createClientProductOfferShouldExecuteSuccessButChangeStates() {
		CreateClientProductOfferRequest createClientProductOfferRequest = buildCreateClientProductOfferRequest("REGISTRY_PREAPPROVED");
		ClientsV3Entity clientsV3Entity = buildClientsV3Entity();
		when(clientsV3Repository.findByIdClient(createClientProductOfferRequest.getIdClient())).thenReturn(Option.of(clientsV3Entity));
		when(clientsV3Repository.save(any())).thenReturn(Try.of(() -> clientsV3Entity));
		Either<UseCaseResponseError, ClientProductOffer> response = createClientProductOfferUseCase.execute(createClientProductOfferRequest);
		assertThat(response.isRight(), is(true));
		assertThat(response.get(), notNullValue());
		assertThat(response.get().getType(), is(createClientProductOfferRequest.getType()));
		assertThat(response.get().getValue(), is(createClientProductOfferRequest.getValue()));
	}

	private ClientsV3Entity buildClientsV3Entity() {
		ClientsV3Entity clientsV3Entity = new ClientsV3Entity();
		ApprovedRiskAnalysisV3 approvedRiskAnalysis = new ApprovedRiskAnalysisV3();
		RiskOfferV3 riskOffer = new RiskOfferV3();
		List<RiskOfferV3> results = new ArrayList<>();
		riskOffer.setState(OfferState.ACTIVE);
		riskOffer.setType("REGISTRY_PREAPPROVED");
		results.add(riskOffer);
		approvedRiskAnalysis.setResults(results);
		clientsV3Entity.setIdClient("idClient");
		clientsV3Entity.setApprovedRiskAnalysis(approvedRiskAnalysis);
		return clientsV3Entity;
	}

	private CreateClientProductOfferRequest buildCreateClientProductOfferRequest(String type) {
		return CreateClientProductOfferRequest.builder()
				.idClient("idClient")
				.type(type)
				.value(10)
				.build();
	}

}
