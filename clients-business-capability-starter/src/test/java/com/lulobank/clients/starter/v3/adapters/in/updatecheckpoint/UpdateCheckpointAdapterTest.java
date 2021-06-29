package com.lulobank.clients.starter.v3.adapters.in.updatecheckpoint;

import com.lulobank.clients.sdk.operations.util.CheckPoints;
import com.lulobank.clients.services.utils.HttpCodes;
import com.lulobank.clients.starter.AbstractBaseIntegrationTest;
import com.lulobank.clients.starter.adapter.in.dto.ErrorResponse;
import com.lulobank.clients.starter.adapter.in.util.AdapterResponseUtil;
import com.lulobank.clients.starter.v3.adapters.in.dto.UpdateCheckpointRequest;
import com.lulobank.clients.starter.v3.adapters.in.dto.UpdateCheckpointResponse;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.lulobank.clients.starter.utils.Sample.buildUpdateCheckpointRequest;
import static com.lulobank.clients.starter.utils.Sample.buildUpdateCheckpointResponse;
import static com.lulobank.clients.v3.error.ClientsDataErrorStatus.CLI_102;
import static com.lulobank.clients.v3.error.UseCaseErrorStatus.CLI_180;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
public class UpdateCheckpointAdapterTest extends AbstractBaseIntegrationTest {

    private UpdateCheckpointRequest request;
    private UpdateCheckpointResponse response;
    private static final String URL = "/api/v1/{idClient}/checkpoints";
    private static final String CLIENT_ID = "5d3da802-0301-43ae-b992-cb269f4dfe8g";
    private static final String RESPONSE_BAD_REQUEST = "{\"failure\":\"checkpoint is empty or null\",\"code\":\"GEN_001\",\"detail\":\"V\"}";
    private static final String RESPONSE_BAD_REQUEST_VALIDATION_ERROR = "{\"failure\":\"CLI_102\",\"code\":\"400\",\"detail\":\"Validation error\"}";
    private static final String RESPONSE_INTERNAL_ERROR = "{\"failure\":\"CLI_180\",\"code\":\"500\",\"detail\":\"Error default\"}";


    @Override
    protected void init() {
        request = buildUpdateCheckpointRequest();
        response = buildUpdateCheckpointResponse();
    }

    @Test
    public void shouldReturn200Ok() throws Exception {
        request.setCheckpoint(CheckPoints.BLACKLIST_STARTED.name());
        response.setCheckpoint(CheckPoints.BLACKLIST_STARTED);
        when(updateCheckpointHandler.execute(anyString(),any()))
                .thenReturn(AdapterResponseUtil.ok(response));
        mockMvc.perform(
                MockMvcRequestBuilders.put(URL, CLIENT_ID)
                        .contentType(CONTENT_TYPE_JSON)
                        .with(bearerToken())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(request)));
        verify(updateCheckpointHandler, times(1)).execute(anyString(),any());
    }

    @Test
    public void shouldReturn400BadRequest() throws Exception {
        request.setCheckpoint("Invalid Checkpoint");
        ErrorResponse errorResponse = new ErrorResponse(CLI_102.name(), HttpCodes.BAD_REQUEST,CLI_102.getMessage());
        when(updateCheckpointHandler.execute(anyString(),any()))
                .thenReturn(AdapterResponseUtil.badRequest(errorResponse));
        mockMvc.perform(
                MockMvcRequestBuilders.put(URL, CLIENT_ID)
                        .contentType(CONTENT_TYPE_JSON)
                        .with(bearerToken())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(RESPONSE_BAD_REQUEST_VALIDATION_ERROR));
        verify(updateCheckpointHandler, times(1)).execute(anyString(),any());
    }

    @Test
    public void shouldReturn400BadRequestEmptyField() throws Exception {
        request.setCheckpoint("");
        mockMvc.perform(
                MockMvcRequestBuilders.put(URL, CLIENT_ID)
                        .contentType(CONTENT_TYPE_JSON)
                        .with(bearerToken())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(RESPONSE_BAD_REQUEST));
        verify(updateCheckpointHandler, times(0)).execute(anyString(),any());
    }


    @Test
    public void shouldReturn500InternalServerError() throws Exception {
        request.setCheckpoint(CheckPoints.ON_BOARDING.name());
        ErrorResponse errorResponse = new ErrorResponse(CLI_180.name(), HttpCodes.INTERNAL_SERVER_ERROR,CLI_180.getMessage());
        when(updateCheckpointHandler.execute(anyString(),any()))
                .thenReturn(AdapterResponseUtil.error(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR));
        mockMvc.perform(
                MockMvcRequestBuilders.put(URL, CLIENT_ID)
                        .contentType(CONTENT_TYPE_JSON)
                        .with(bearerToken())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(RESPONSE_INTERNAL_ERROR));
        verify(updateCheckpointHandler, times(1)).execute(anyString(),any());
    }
}