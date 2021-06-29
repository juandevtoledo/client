package com.lulobank.clients.starter;

import com.amazonaws.SdkClientException;
import com.lulobank.blacklist.operations.BlackListClientOperations;
import com.lulobank.clients.sdk.operations.dto.ClientResult;
import com.lulobank.clients.services.inboundadapters.ClientAdapter;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.core.crosscuttingconcerns.ValidatorDecoratorHandler;
import com.lulobank.credits.sdk.operations.IClientProductOfferOperations;
import com.lulobank.credits.sdk.operations.impl.RetrofitGetLoanDetailOperations;
import com.lulobank.savingsaccounts.sdk.operations.ICreateSavingAccountService;
import flexibility.client.connector.ProviderException;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DemographicInfoByClientTest extends AbstractBaseIntegrationTest {

    private static final String ID_CLIENT = "1106bc49-4a0f-4f52-86ca-1994bb3c26d9";
    private static final String ADDRESS = "address_test";
    private static final String ADDRESS_PREFIX = "prefix_test";
    private static final String ADDRESS_COMPLEMENT = "complement_test";
    private static final String DEPARTMENT = "department_test";
    private static final String DEPARTMENT_ID = "1";
    private static final String CITY = "city_test";
    private static final String CITY_ID = "1";
    public static final String CODE = "ADL";

    private ClientEntity clientEntityFound;

    @Mock
    private RetrofitGetLoanDetailOperations getLoanDetailOperationsService;

    @Mock
    private BlackListClientOperations blackListClientService;

    @Mock
    private ICreateSavingAccountService createSavingAccountService;

    @Mock
    private IClientProductOfferOperations clientProductOfferOperations;

    @Mock
    private ValidatorDecoratorHandler getClientInfoByIdCardHandler;

    @Override
    protected void init() {
        clientEntityFound = new ClientEntity();
        clientEntityFound.setIdClient(ID_CLIENT);
        clientEntityFound.setDateOfIssue(LocalDate.now());
        clientEntityFound.setIdCard("12345678");
        clientEntityFound.setAddress(ADDRESS);
        clientEntityFound.setAddressPrefix(ADDRESS_PREFIX);
        clientEntityFound.setAddressComplement(ADDRESS_COMPLEMENT);
        clientEntityFound.setCity(CITY);
        clientEntityFound.setCityId(CITY_ID);
        clientEntityFound.setDepartment(DEPARTMENT);
        clientEntityFound.setDepartmentId(DEPARTMENT_ID);
        clientEntityFound.setCode(CODE);
    }

    @Test
    public void getDemographicInfoByClientOk() throws Exception {
        when(clientsRepository.findByIdClient(ID_CLIENT)).thenReturn(Optional.of(clientEntityFound));
        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/demographic/".concat(ID_CLIENT))
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(bearerToken()))
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("content.idClient", is(ID_CLIENT)))
                .andExpect(jsonPath("content.address", is(ADDRESS)))
                .andExpect(jsonPath("content.addressPrefix", is(ADDRESS_PREFIX)))
                .andExpect(jsonPath("content.addressComplement", is(ADDRESS_COMPLEMENT)))
                .andExpect(jsonPath("content.city", is(CITY)))
                .andExpect(jsonPath("content.cityId", is(CITY_ID)))
                .andExpect(jsonPath("content.department", is(DEPARTMENT)))
                .andExpect(jsonPath("content.departmentId", is(DEPARTMENT_ID)))
                .andExpect(jsonPath("content.code", is(CODE)))
                .andReturn();
    }

    @Test
    public void getDemographicInfoByClientNotFoundInDatabase() throws Exception {
        when(clientsRepository.findByIdClient(ID_CLIENT)).thenReturn(Optional.empty());

        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/demographic/".concat(ID_CLIENT))
                                .contentType(CONTENT_TYPE_JSON)
                                .with(bearerToken()))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    public void getDemographicInfoByClient_InternalServerError_CLIENT_DB_ERROR()
            throws ProviderException {

        ClientAdapter clientAdapter =
                new ClientAdapter(
                        clientsRepository,
                        queueMessagingTemplate,
                        cognitoProperties,
                        flexibilitySdk,
                        getLoanDetailOperationsService,
                        loginAttemptsService,
                        getClientInfoByIdCardHandler,
                        null,
                        transactionsPort);

        when(clientsRepository.findByIdClient(any(String.class))).thenThrow(SdkClientException.class);

        ResponseEntity<ClientResult> response =
                clientAdapter.getDemographicInfoByClient(new HttpHeaders(), ID_CLIENT);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
