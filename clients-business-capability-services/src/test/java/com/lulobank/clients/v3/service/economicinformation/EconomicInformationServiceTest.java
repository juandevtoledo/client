package com.lulobank.clients.v3.service.economicinformation;

import com.lulobank.clients.sdk.operations.AdapterCredentials;
import com.lulobank.clients.sdk.operations.dto.economicinformation.ClientEconomicInformation;
import com.lulobank.clients.sdk.operations.dto.economicinformation.EmployeeCompany;
import com.lulobank.clients.sdk.operations.dto.economicinformation.OccupationType;
import com.lulobank.clients.services.SamplesV3;
import com.lulobank.clients.services.ports.out.ParameterService;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.OnBoardingStatusV3;
import com.lulobank.parameters.sdk.dto.parameters.ParameterResponse;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashMap;

import static com.lulobank.clients.services.utils.ProductTypeEnum.CREDIT_ACCOUNT;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class EconomicInformationServiceTest {

    private EconomicInformationService target;

    @Mock
    private ClientsV3Repository clientsRepository;
    @Mock
    private ParameterService parameterService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        target = new EconomicInformationService(clientsRepository, parameterService);
    }

    @Test
    public void clientNotFount() {
        ClientEconomicInformation clientEconomicInformation = buildClientEconomicInformation(OccupationType.EMPLOYEE);
        when(clientsRepository.findByIdClient(clientEconomicInformation.getIdClient())).thenReturn(Option.none());
        when(parameterService.getParameterByKey(any(), eq(clientEconomicInformation.getIdClient()), eq(clientEconomicInformation.getEconomicActivity()))).thenReturn(Try.of(() -> new ParameterResponse(new ArrayList<>())));

        Try<ClientsV3Entity> result = target.build(clientEconomicInformation);

        assertThat(result.isFailure(), is(true));
    }

    @Test
    public void clientFountEmployee() {
        ClientEconomicInformation clientEconomicInformation = buildClientEconomicInformation(OccupationType.EMPLOYEE);
        OnBoardingStatusV3 onBoardingStatusV3 = new OnBoardingStatusV3(null, CREDIT_ACCOUNT.name());
        ClientsV3Entity clientEntity = SamplesV3.clientEntityV3Builder(onBoardingStatusV3, null);
        when(clientsRepository.findByIdClient(clientEconomicInformation.getIdClient())).thenReturn(Option.of(clientEntity));
        when(parameterService.getParameterByKey(any(), eq(clientEconomicInformation.getIdClient()), eq(clientEconomicInformation.getEconomicActivity()))).thenReturn(Try.of(() -> new ParameterResponse(new ArrayList<>())));

        Try<ClientsV3Entity> result = target.build(clientEconomicInformation);

        assertThat(result.isSuccess(), is(true));
        assertEquals(result.get().getEconomicInformation().getEconomicActivity(),OccupationType.EMPLOYEE.getCode());
        assertEquals(result.get().getEconomicInformation().getCompany().getCity(),clientEconomicInformation.getEmployeeCompany().getCity());
        assertEquals(result.get().getEconomicInformation().getCompany().getName(),clientEconomicInformation.getEmployeeCompany().getName());
        assertEquals(result.get().getEconomicInformation().getCompany().getState(),clientEconomicInformation.getEmployeeCompany().getState());
    }

    @Test
    public void clientFountRetired() {
        ClientEconomicInformation clientEconomicInformation = buildClientEconomicInformation(OccupationType.RETIRED);
        OnBoardingStatusV3 onBoardingStatusV3 = new OnBoardingStatusV3(null, CREDIT_ACCOUNT.name());
        ClientsV3Entity clientEntity = SamplesV3.clientEntityV3Builder(onBoardingStatusV3, null);
        when(clientsRepository.findByIdClient(clientEconomicInformation.getIdClient())).thenReturn(Option.of(clientEntity));
        when(parameterService.getParameterByKey(any(), eq(clientEconomicInformation.getIdClient()), eq(clientEconomicInformation.getEconomicActivity()))).thenReturn(Try.of(() -> new ParameterResponse(new ArrayList<>())));

        Try<ClientsV3Entity> result = target.build(clientEconomicInformation);

        assertThat(result.isSuccess(), is(true));
        assertEquals(result.get().getEconomicInformation().getEconomicActivity(),OccupationType.RETIRED.getCode());
        assertEquals(result.get().getEconomicInformation().getCompany().getCity(),clientEconomicInformation.getEmployeeCompany().getCity());
        assertEquals(result.get().getEconomicInformation().getCompany().getName(),clientEconomicInformation.getEmployeeCompany().getName());
        assertEquals(result.get().getEconomicInformation().getCompany().getState(),clientEconomicInformation.getEmployeeCompany().getState());
    }

    private ClientEconomicInformation buildClientEconomicInformation(OccupationType occupationType) {
        EmployeeCompany employeeCompany =new EmployeeCompany();
        employeeCompany.setCity("Bogota");
        employeeCompany.setName("Lulo");
        employeeCompany.setState("1");

        ClientEconomicInformation clientEconomicInformation = new ClientEconomicInformation();
        clientEconomicInformation.setIdClient("idClient");
        clientEconomicInformation.setOccupationType(occupationType);
        clientEconomicInformation.setEconomicActivity("1105");
        clientEconomicInformation.setAdapterCredentials(new AdapterCredentials(new HashMap<>()));
        clientEconomicInformation.setEmployeeCompany(employeeCompany);
        return clientEconomicInformation;
    }

}
