package com.lulobank.clients.v3.usecase.mapper;


import com.lulobank.clients.services.SamplesV3;
import com.lulobank.clients.v3.adapters.port.out.savingsaccount.dto.SavingsAccountRequest;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class SavingsAccountRequestMapperTest {

    @Test
    public void savingsAccountRequestMapper(){
        ClientsV3Entity clientsV3Entity= SamplesV3.clientEntityV3Builder();
        SavingsAccountRequest savingsAccountRequest= SavingsAccountRequestMapper.INSTANCE.toSavingsAccountRequest(clientsV3Entity);
        assertThat(savingsAccountRequest.getIdClient(),is(clientsV3Entity.getIdClient()));
        assertThat(savingsAccountRequest.getClientInformation().getName(),is(clientsV3Entity.getName()));
        assertThat(savingsAccountRequest.getClientInformation().getLastName(),is(clientsV3Entity.getLastName()));
        assertThat(savingsAccountRequest.getClientInformation().getGender(),is(clientsV3Entity.getGender()));
    }

}
