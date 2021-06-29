package com.lulobank.clients.starter.v3.adapters.in.createaddress;

import com.lulobank.clients.services.utils.HttpCodes;
import com.lulobank.clients.starter.AbstractBaseIntegrationTest;
import com.lulobank.clients.starter.adapter.in.dto.ErrorResponse;
import com.lulobank.clients.starter.adapter.in.util.AdapterResponseUtil;
import com.lulobank.clients.starter.v3.adapters.in.dto.CreateAddressRequest;
import com.lulobank.clients.starter.v3.adapters.in.dto.CreateAddressResponse;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.lulobank.clients.starter.utils.Sample.buildCreateAddressRequest;
import static com.lulobank.clients.starter.utils.Sample.buildCreateAddressResponse;
import static com.lulobank.clients.v3.error.ClientsDataErrorStatus.CLI_106;
import static com.lulobank.clients.v3.error.UseCaseErrorStatus.CLI_180;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ClientCreateAddressAdapterTest extends AbstractBaseIntegrationTest {

    private CreateAddressRequest request;
    private CreateAddressResponse response;
    private static final String URL = "/api/v1/clients/{idClient}/main-address";
    private static final String CLIENT_ID = "1106bc49-4a0f-4f52-86ca-1994bb3c26d9";

    private static final String RESPONSE_BAD_REQUEST = "{\"failure\":\"address is null or empty\",\"code\":\"GEN_001\",\"detail\":\"V\"}";
    private static final String RESPONSE_CONFLICT = "{\"failure\":\"CLI_106\",\"code\":\"409\",\"detail\":\"The client already has an address created\"}";
    private static final String RESPONSE_INTERNAL_ERROR = "{\"failure\":\"CLI_180\",\"code\":\"500\",\"detail\":\"Error default\"}";

    @Override
    protected void init() {
        request = buildCreateAddressRequest();
        response = buildCreateAddressResponse();
    }

    @Test
    public void shouldReturn201Created() throws Exception {
        when(clientCreateAddressHandler.execute(anyString(),any()))
                .thenReturn(AdapterResponseUtil.created(response));
        mockMvc.perform(
                MockMvcRequestBuilders.post(URL, CLIENT_ID)
                        .contentType(CONTENT_TYPE_JSON)
                        .with(bearerToken())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().string(objectMapper.writeValueAsString(request)));
        verify(clientCreateAddressHandler, times(1)).execute(anyString(),any());
    }

    @Test
    public void shouldReturn400BadRequest() throws Exception {
        request.setAddress("");
        mockMvc.perform(
                MockMvcRequestBuilders.post(URL, CLIENT_ID)
                        .contentType(CONTENT_TYPE_JSON)
                        .with(bearerToken())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(RESPONSE_BAD_REQUEST));
        verify(clientCreateAddressHandler, times(0)).execute(anyString(),any());
    }

    @Test
    public void shouldReturn409Conflict() throws Exception {
        ErrorResponse errorResponse = new ErrorResponse(CLI_106.name(), HttpCodes.CONFLICT,CLI_106.getMessage());
        when(clientCreateAddressHandler.execute(anyString(),any()))
                .thenReturn(AdapterResponseUtil.error(errorResponse, HttpStatus.CONFLICT));
        mockMvc.perform(
                MockMvcRequestBuilders.post(URL, CLIENT_ID)
                        .contentType(CONTENT_TYPE_JSON)
                        .with(bearerToken())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(content().string(RESPONSE_CONFLICT));
        verify(clientCreateAddressHandler, times(1)).execute(anyString(),any());
    }

    @Test
    public void shouldReturn500InternalServerError() throws Exception {
        ErrorResponse errorResponse = new ErrorResponse(CLI_180.name(), HttpCodes.INTERNAL_SERVER_ERROR,CLI_180.getMessage());
        when(clientCreateAddressHandler.execute(anyString(),any()))
                .thenReturn(AdapterResponseUtil.error(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR));
        mockMvc.perform(
                MockMvcRequestBuilders.post(URL, CLIENT_ID)
                        .contentType(CONTENT_TYPE_JSON)
                        .with(bearerToken())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(RESPONSE_INTERNAL_ERROR));
        verify(clientCreateAddressHandler, times(1)).execute(anyString(),any());
    }
}