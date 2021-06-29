package com.lulobank.clients.services.inboundadapters;

import com.google.firebase.database.DatabaseReference;
import com.lulobank.clients.services.application.port.out.clientnotify.ClientNotifyService;
import com.lulobank.clients.services.application.port.out.reporting.ReportingMessagingPort;
import com.lulobank.clients.services.application.port.out.reporting.TransactionsMessagingPort;
import com.lulobank.clients.services.events.IdentityInformation;
import com.lulobank.clients.services.features.RetriesOption;
import com.lulobank.clients.services.features.clientverificationresult.BlacklistedProcessService;
import com.lulobank.clients.services.features.clientverificationresult.NonBlacklistedProcessService;
import com.lulobank.clients.services.features.onboardingclients.action.MessageToNotifySQSEconomicInformation;
import com.lulobank.clients.services.features.onboardingclients.action.MessageToNotifySQSRiskEngine;
import com.lulobank.clients.services.outboundadapters.ClientsOutboundAdapter;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.outboundadapters.repository.ClientsRepository;
import com.lulobank.clients.services.ports.out.MessageService;
import com.lulobank.clients.services.ports.out.corebanking.ClientInfoCoreBankingPort;
import com.lulobank.clients.services.ports.repository.ClientsRepositoryV2;
import com.lulobank.clients.services.utils.LuloUserTokenGenerator;
import com.lulobank.clients.v3.adapters.port.out.digitalevidence.DigitalEvidenceServicePort;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.credits.sdk.operations.InitialOffersOperations;
import com.lulobank.credits.sdk.operations.impl.RetrofitGetLoanDetailOperations;
import com.lulobank.savingsaccounts.sdk.operations.ISavingsAccount;
import flexibility.client.models.request.GetAccountRequest;
import flexibility.client.models.request.GetLoanRequest;
import flexibility.client.sdk.FlexibilitySdk;
import org.junit.Before;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.validation.BindingResult;

import java.util.Map;

public abstract class AbstractBaseUnitTest {
    protected static final String ID_CLIENT = "1106bc49-4a0f-4f52-86ca-1994bb3c26d9";
    @Mock
    protected ClientsOutboundAdapter clientsOutboundAdapter;
    @Mock
    protected ClientsRepository clientsRepository;
    @Mock
    protected ClientsRepositoryV2 clientsRepositoryV2;
    @Mock
    protected MessageService messageService;
    @Mock
    protected ClientInfoCoreBankingPort clientInfoCoreBankingPort;
    @Mock
    protected DatabaseReference databaseReference;
    @Mock
    protected BindingResult bindingResult;
    @Mock
    protected FlexibilitySdk flexibilitySdk;
    @Mock
    protected LuloUserTokenGenerator luloUserTokenGenerator;
    @Mock
    protected RetrofitGetLoanDetailOperations retrofitGetLoanDetailOperations;
    @Mock
    protected InitialOffersOperations initialOffersOperations;
    @Mock
    protected MessageToNotifySQSRiskEngine messageToNotifySQSRiskEngine;
    @Mock
    protected RetriesOption mobileRetriesOption;
    @Mock
    protected ISavingsAccount savingsAccount;
    @Mock
    protected ClientsV3Repository clientsV3Repository;
    @Mock
    protected ReportingMessagingPort reportingMessagingPort;
    @Mock
    protected MessageToNotifySQSEconomicInformation messageToNotifySQSEconomicInformation;
    @Mock
    protected ClientNotifyService clientNotifyService;
    @Mock
    protected BlacklistedProcessService blacklistedProcessService;
    @Mock
    protected NonBlacklistedProcessService nonBlacklistedProcessService;
    @Mock
    protected TransactionsMessagingPort transactionsMessagingService;
    @Mock
    protected DigitalEvidenceServicePort digitalEvidenceServicePort;
    @Captor
    protected ArgumentCaptor<Map> firebaseParametersCaptor;
    @Captor
    protected ArgumentCaptor<ClientEntity> clientEntityArgumentCaptor;
    @Captor
    protected ArgumentCaptor<ClientsV3Entity> clientEntityV3ArgumentCaptor;
    @Captor
    protected ArgumentCaptor<String> stringArgumentCaptor;
    @Captor
    protected ArgumentCaptor<GetAccountRequest> getAccountRequestArgumentCaptor;
    @Captor
    protected ArgumentCaptor<GetLoanRequest> getLoanRequestArgumentCaptor;
    @Captor
    protected ArgumentCaptor<IdentityInformation> identityInformationCaptor;
    @Captor
    protected ArgumentCaptor<Map<String, Object>> firebaseUpdateCaptor;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        clientsOutboundAdapter = new ClientsOutboundAdapter();
        clientsOutboundAdapter.setClientsRepository(clientsRepository);
        clientsOutboundAdapter.setDatabaseReference(databaseReference);
        clientsOutboundAdapter.setLuloUserTokenGenerator(luloUserTokenGenerator);
        clientsOutboundAdapter.setInitialOffersOperations(initialOffersOperations);
        clientsOutboundAdapter.setMessageToNotifySQSRiskEngine(messageToNotifySQSRiskEngine);
        clientsOutboundAdapter.setSavingsAccount(savingsAccount);
        clientsOutboundAdapter.setMessageToNotifySQSEconomicInformation(messageToNotifySQSEconomicInformation);
        init();
    }

    protected abstract void init();
}
