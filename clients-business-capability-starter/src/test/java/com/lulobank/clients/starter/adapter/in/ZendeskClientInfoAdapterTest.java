package com.lulobank.clients.starter.adapter.in;

import com.lulobank.clients.v3.error.ClientsDataError;
import com.lulobank.clients.services.domain.zendeskclientinfo.GetClientInfoByEmailResponse;
import com.lulobank.clients.starter.AbstractBaseIntegrationTest;
import com.lulobank.clients.starter.adapter.Sample;
import io.vavr.control.Either;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;

import static com.lulobank.clients.starter.adapter.Constant.MAIL;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ZendeskClientInfoAdapterTest extends AbstractBaseIntegrationTest {

    public static final String GET_ZENDESK_CLIENT_INFO = "/info";

    @Value("classpath:mocks/zendesk/client-info.json")
    private Resource clientInfoResponse;

    @Value("classpath:mocks/zendesk/client-error-repository.json")
    private Resource clientInfoErrorResponseRepository;

    @Override
    protected void init() {

    }

    @Test
    public void shouldReturnClientInfo() throws Exception {
        GetClientInfoByEmailResponse getClientInfoByEmailResponse = Sample.getClientInfoByEmailResponse();
        when(zendeskClientInfoPort.execute(any())).thenReturn(Either.right(getClientInfoByEmailResponse));

        mockMvc.perform(MockMvcRequestBuilders
                .get(GET_ZENDESK_CLIENT_INFO)
                .param("email",MAIL)
                .with(bearerTokenAWSZendesk())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andExpect(content().json(FileUtils.readFileToString(clientInfoResponse.getFile(), StandardCharsets.UTF_8)));
    }
    @Test
    public void shouldReturnErrorRepository() throws Exception {
        when(zendeskClientInfoPort.execute(any())).thenReturn(Either.left(ClientsDataError.connectionFailure()));

        mockMvc.perform(MockMvcRequestBuilders
                .get(GET_ZENDESK_CLIENT_INFO)
                .param("email",MAIL)
                .with(bearerTokenAWSZendesk())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"))
                .andExpect(status().isBadGateway())
                .andExpect(content().json(FileUtils.readFileToString(clientInfoErrorResponseRepository.getFile(), StandardCharsets.UTF_8)));
    }



}
