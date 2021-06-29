package com.lulobank.clients.starter.outboundadapters.flexibility.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lulobank.clients.sdk.operations.dto.UpdateClientAddressRequest;
import com.lulobank.clients.starter.outboundadapter.flexibility.mapper.ClientInfoMapper;
import flexibility.client.models.request.UpdateClientRequest;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.springframework.util.ResourceUtils;

import java.io.IOException;


public class ClientInfoMapperTest {

    public static final String ADDRESS_COMPLEMENT = "cmpl";
    public static final String ADDRESS = "address";
    public static final String CODE = "ADL";

    @Test
    public void clientInfoMapperOk() throws IOException {
        UpdateClientAddressRequest updateClientAddressRequest= UpdateClientAddressRequestBuilder();
        UpdateClientRequest.Address address=ClientInfoMapper.INSTANCE.toUpdateClientRequestAddress(updateClientAddressRequest);
        MatcherAssert.assertThat(address.getDescription(), Is.is(ADDRESS_COMPLEMENT));
        MatcherAssert.assertThat(address.getAddress(), Is.is(ADDRESS));
        MatcherAssert.assertThat(address.getDian(), Is.is(CODE));
    }

    public UpdateClientAddressRequest UpdateClientAddressRequestBuilder() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(ResourceUtils.getFile("classpath:mocks/updateClientAddressRequest.json"), UpdateClientAddressRequest.class);

    }


}
