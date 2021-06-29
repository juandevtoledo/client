package com.lulobank.clients.starter.outboundadapters.flexibility;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lulobank.clients.sdk.operations.dto.UpdateClientAddressRequest;
import com.lulobank.clients.services.exception.CoreBankingException;
import com.lulobank.clients.starter.outboundadapter.flexibility.ClientInfoMambuAdapter;
import flexibility.client.connector.ProviderException;
import flexibility.client.models.request.GetClientRequest;
import flexibility.client.models.request.UpdateClientRequest;
import flexibility.client.models.response.GetClientResponse;
import flexibility.client.models.response.UpdateClientResponse;
import flexibility.client.sdk.FlexibilitySdk;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.util.ResourceUtils;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ClientInfoMambuAdapterTest {

    private static final String CBS_ID = "123456";
    private static final String FULL_ADDRESS = "PREFIX address cmpl";

    @Mock
    private FlexibilitySdk flexibilitySdk;

    @InjectMocks
    private ClientInfoMambuAdapter clientInfoMambuAdapter;

    private UpdateClientAddressRequest updateClientAddressRequest;
    private GetClientResponse getClientResponse;

    @Captor
    private ArgumentCaptor<GetClientRequest> getClientRequestArgumentCaptor;

    @Captor
    private ArgumentCaptor<UpdateClientRequest> updateClientRequestArgumentCaptor;


    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        updateClientAddressRequest = updateClientAddressRequestBuilder();
        getClientResponse = getClientRespone();
    }


    @Test(expected = CoreBankingException.class)
    public void shouldFailUpdatingAddress() throws ProviderException {
        when(flexibilitySdk.getClientById(any())).thenReturn(getClientResponse);
        when(flexibilitySdk.updateClient(any())).thenThrow(new ProviderException("", ""));
        clientInfoMambuAdapter.updateAddressCoreBanking(updateClientAddressRequest, CBS_ID);
        verify(flexibilitySdk).getClientById(getClientRequestArgumentCaptor.capture());
        verify(flexibilitySdk).updateClient(any());
        assertEquals(CBS_ID, getClientRequestArgumentCaptor.getValue().getClientId());
    }

    @Test
    public void shouldUpdatingAddress() throws ProviderException {
        when(flexibilitySdk.getClientById(any())).thenReturn(getClientResponse);
        when(flexibilitySdk.updateClient(any())).thenReturn(new UpdateClientResponse());
        clientInfoMambuAdapter.updateAddressCoreBanking(updateClientAddressRequest, CBS_ID);
        verify(flexibilitySdk).getClientById(getClientRequestArgumentCaptor.capture());
        verify(flexibilitySdk).updateClient(updateClientRequestArgumentCaptor.capture());
        assertEquals(CBS_ID, getClientRequestArgumentCaptor.getValue().getClientId());
        assertEquals(updateClientAddressRequest.getCityId(),
                updateClientRequestArgumentCaptor.getValue().getAddress().getCity().getCode());
        assertEquals(updateClientAddressRequest.getCity(),
                updateClientRequestArgumentCaptor.getValue().getAddress().getCity().getDescription());
        assertEquals(updateClientAddressRequest.getDepartment(),
                updateClientRequestArgumentCaptor.getValue().getAddress().getState().getDescription());
        assertEquals(updateClientAddressRequest.getDepartmentId(),
                updateClientRequestArgumentCaptor.getValue().getAddress().getState().getCode());
        assertEquals(updateClientAddressRequest.getDepartmentId(),
                updateClientRequestArgumentCaptor.getValue().getAddress().getDepartment());
        assertEquals(updateClientAddressRequest.getCode(),
                updateClientRequestArgumentCaptor.getValue().getAddress().getDian());
        assertEquals(updateClientAddressRequest.getCityId(),
                updateClientRequestArgumentCaptor.getValue().getAddress().getMunicipality());
        assertEquals(FULL_ADDRESS,
                updateClientRequestArgumentCaptor.getValue().getAddress().getDescription());
        assertEquals(FULL_ADDRESS,
                updateClientRequestArgumentCaptor.getValue().getAddress().getAddress());
    }

    private UpdateClientAddressRequest updateClientAddressRequestBuilder() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(ResourceUtils.getFile("classpath:mocks/updateClientAddressRequest.json"),
                UpdateClientAddressRequest.class);

    }

    private GetClientResponse getClientRespone() {
        GetClientResponse getClientResponse = new GetClientResponse();
        getClientResponse.setId(CBS_ID);
        getClientResponse.setAddress(new GetClientResponse.Address());
        return getClientResponse;
    }
}