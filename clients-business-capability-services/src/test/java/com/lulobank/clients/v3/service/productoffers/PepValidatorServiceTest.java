package com.lulobank.clients.v3.service.productoffers;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;

public class PepValidatorServiceTest {
	
	private PepValidatorService subject;
	
	@Before
	public void setup() {
		subject = new PepValidatorService();
	}
	
	@Test
	public void pepValidatorShouldReturnTrue() {
		ClientsV3Entity clientsV3Entity = buildClientsV3Entity("0");
		Map<String, String> headears = new HashMap<>();
		
		boolean response = subject.validateProductOffer(clientsV3Entity, headears);
		assertThat(response, is(true));
	}
	
	@Test
	public void pepValidatorShouldReturnFalse() {
		ClientsV3Entity clientsV3Entity = buildClientsV3Entity("2");
		Map<String, String> headears = new HashMap<>();
		
		boolean response = subject.validateProductOffer(clientsV3Entity, headears);
		assertThat(response, is(false));
	}
	
	private ClientsV3Entity buildClientsV3Entity(String pep) {
		ClientsV3Entity clientsV3Entity = new ClientsV3Entity();
		clientsV3Entity.setIdClient("idClient");
		clientsV3Entity.setPep(pep);
		clientsV3Entity.setDateResponsePep(LocalDateTime.now());
		return clientsV3Entity;
	}

}
