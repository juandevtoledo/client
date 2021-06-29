package com.lulobank.clients.v3.usecase.mapper;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

import com.lulobank.clients.services.SamplesV3;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.clients.v3.usecase.command.ClientDemographicInfo;

public class ClientsDemographicMapperTest {
	
	@Test
	public void clientsDemographicMapperTest() {
		ClientsV3Entity clientsV3Entity= SamplesV3.clientEntityV3Builder();
		ClientDemographicInfo clientDemographicInfo = ClientsDemographicMapper.INSTANCE.clientEntityToDemographicInfo(clientsV3Entity);
		
		assertThat(clientDemographicInfo.getName(),is(clientsV3Entity.getName()));
		assertThat(clientDemographicInfo.getLastName(),is(clientsV3Entity.getLastName()));
	}
}


