package com.lulobank.clients.v3.usecase.economicinformation;

import com.lulobank.clients.sdk.operations.AdapterCredentials;
import com.lulobank.clients.sdk.operations.dto.economicinformation.ClientEconomicInformation;
import com.lulobank.clients.sdk.operations.dto.economicinformation.OccupationType;
import com.lulobank.clients.sdk.operations.util.CheckPoints;
import com.lulobank.clients.services.SamplesV3;
import com.lulobank.clients.services.ports.out.ParameterService;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.OnBoardingStatusV3;
import com.lulobank.clients.v3.events.RiskEngineNotificationService;
import com.lulobank.clients.v3.service.economicinformation.EconomicInformationService;
import com.lulobank.clients.v3.usecase.command.UseCaseError;
import com.lulobank.parameters.sdk.dto.parameters.ParameterResponse;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SaveEconomicInformationInOnboardingUseCaseTest {

    private SaveEconomicInformationInOnboardingUseCase subject;

    private EconomicInformationService economicInformationService;

    @Mock
    private ClientsV3Repository clientsRepository;
    @Mock
    private ParameterService parameterService;
    @Mock
    private RiskEngineNotificationService riskEngineNotificationService;

    @Captor
    protected ArgumentCaptor<ClientsV3Entity> clientEntityCaptor;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        economicInformationService = new EconomicInformationService(clientsRepository,parameterService);
        subject = new SaveEconomicInformationInOnboardingUseCase(clientsRepository, economicInformationService, riskEngineNotificationService);
    }

    @Test
    public void shouldSaveEconomicActivitySuccess() {
        ClientEconomicInformation clientEconomicInformation = buildClientEconomicInformation(OccupationType.EMPLOYEE);
        OnBoardingStatusV3 onBoardingStatusV3 = new OnBoardingStatusV3(null, CREDIT_ACCOUNT.name());
        ClientsV3Entity clientEntity = SamplesV3.clientEntityV3Builder(onBoardingStatusV3, null);
        when(clientsRepository.findByIdClient(clientEconomicInformation.getIdClient())).thenReturn(Option.of(clientEntity));
        when(parameterService.getParameterByKey(any(), eq(clientEconomicInformation.getIdClient()), eq(clientEconomicInformation.getEconomicActivity()))).thenReturn(Try.of(()  -> new ParameterResponse(new ArrayList<>())));
        when(clientsRepository.save(clientEntityCaptor.capture())).thenReturn(Try.of(()->clientEntity));
        when(riskEngineNotificationService.setEconomicInformation(any(),any(), any())).thenReturn(Try.run(()-> System.out.println("")));
        when(riskEngineNotificationService.setIdentityInformation(any(),any(), any())).thenReturn(Try.run(()-> System.out.println("")));

        Either<UseCaseError, ClientsV3Entity> result = subject.execute(clientEconomicInformation);
        assertThat(result.isRight(), is(true));
        assertThat(result.isLeft(), is(false));
        assertThat(result.isEmpty(), is(false));
        assertEquals(clientEntityCaptor.getValue().getOnBoardingStatus().getCheckpoint(), CheckPoints.ECONOMIC_INFO_FINISHED.name());
        verify(riskEngineNotificationService).setEconomicInformation(any(), any(), any());
        verify(riskEngineNotificationService).setIdentityInformation(any(), any(), any());
    }

    @Test
    public void shouldNotFoundClientById() {
        ClientEconomicInformation clientEconomicInformation = buildClientEconomicInformation(OccupationType.RETIRED);
        when(clientsRepository.findByIdClient(clientEconomicInformation.getIdClient())).thenReturn(Option.none());
        Either<UseCaseError, ClientsV3Entity> result = subject.execute(clientEconomicInformation);
        assertThat(result.isRight(), is(false));
        assertThat(result.isLeft(), is(true));
        assertThat(result.isEmpty(), is(true));
        assertThat(result.getLeft(), notNullValue());
        verify(riskEngineNotificationService, times(0)).setIdentityInformation(any(), any(), any());
        verify(riskEngineNotificationService, times(0)).setEconomicInformation(any(), any(), any());
    }

    private ClientEconomicInformation buildClientEconomicInformation(OccupationType occupationType) {
        ClientEconomicInformation clientEconomicInformation = new ClientEconomicInformation();
        clientEconomicInformation.setIdClient("idClient");
        clientEconomicInformation.setOccupationType(occupationType);
        clientEconomicInformation.setEconomicActivity("1105");
        clientEconomicInformation.setAdapterCredentials(new AdapterCredentials(new HashMap<>()));
        return clientEconomicInformation;
    }
}
