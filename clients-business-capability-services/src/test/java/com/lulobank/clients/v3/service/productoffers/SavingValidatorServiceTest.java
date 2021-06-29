package com.lulobank.clients.v3.service.productoffers;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.clients.v3.adapters.port.out.saving.SavingAccountService;
import com.lulobank.clients.v3.adapters.port.out.saving.dto.GetSavingAcountTypeResponse;

import io.vavr.control.Either;

public class SavingValidatorServiceTest {

	private SavingValidatorService subject;
	
	@Mock
	private SavingAccountService savingAccountService;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		subject = new SavingValidatorService(savingAccountService);
	}
	
	@Test
	public void savingValidatorShouldReturnTrue() {
		ClientsV3Entity clientsV3Entity = buildClientsV3Entity();
		Map<String, String> headears = new HashMap<>();
		GetSavingAcountTypeResponse getSavingAcountTypeResponse = buildGetSavingAcountTypeResponse("ACTIVE");
		
		when(savingAccountService.getSavingAccount(clientsV3Entity.getIdClient(), headears)).thenReturn(Either.right(getSavingAcountTypeResponse));
		
		boolean response = subject.validateProductOffer(clientsV3Entity, headears);
		assertThat(response, is(true));
	}
	
	@Test
	public void savingValidatorShouldReturnFalse() {
		ClientsV3Entity clientsV3Entity = buildClientsV3Entity();
		Map<String, String> headears = new HashMap<>();
		GetSavingAcountTypeResponse getSavingAcountTypeResponse = buildGetSavingAcountTypeResponse("CLOSED");
		
		when(savingAccountService.getSavingAccount(clientsV3Entity.getIdClient(), headears)).thenReturn(Either.right(getSavingAcountTypeResponse));
		
		boolean response = subject.validateProductOffer(clientsV3Entity, headears);
		assertThat(response, is(false));
	}
	
	private GetSavingAcountTypeResponse buildGetSavingAcountTypeResponse(String state) {
		return GetSavingAcountTypeResponse.builder()
				.state(state)
				.build();
	}

	private ClientsV3Entity buildClientsV3Entity() {
		ClientsV3Entity clientsV3Entity = new ClientsV3Entity();
		clientsV3Entity.setIdClient("idClient");
		return clientsV3Entity;
	}
}
