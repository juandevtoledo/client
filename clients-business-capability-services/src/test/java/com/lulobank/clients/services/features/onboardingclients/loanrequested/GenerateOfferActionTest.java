package com.lulobank.clients.services.features.onboardingclients.loanrequested;

import com.lulobank.clients.sdk.operations.dto.ClientLoanRequested;
import com.lulobank.clients.sdk.operations.util.CheckPoints;
import com.lulobank.clients.services.Sample;
import com.lulobank.clients.services.features.loanrequested.GenerateOfferAction;
import com.lulobank.clients.services.inboundadapters.AbstractBaseUnitTest;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.outboundadapters.model.CreditRiskAnalysis;
import com.lulobank.clients.services.outboundadapters.model.LoanClientRequested;
import com.lulobank.clients.services.outboundadapters.model.LoanRequested;
import com.lulobank.clients.services.outboundadapters.model.OnBoardingStatus;
import com.lulobank.clients.services.utils.LoanRequestedStatus;
import com.lulobank.clients.services.utils.ProductTypeEnum;
import com.lulobank.core.Response;
import com.lulobank.credits.sdk.dto.initialofferv2.GetOfferToClient;
import com.lulobank.credits.sdk.exceptions.InitialOffersException;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Objects;

import static com.lulobank.clients.services.Constants.AMOUNT;
import static com.lulobank.clients.services.Constants.INTEREST_RATE;
import static com.lulobank.clients.services.Constants.MAX_AMOUNT_INSTALLMENT;
import static com.lulobank.clients.services.Constants.PURPOSE;
import static com.lulobank.clients.services.Sample.onBoardingStatusBuilder;
import static com.lulobank.clients.services.utils.ProductTypeEnum.CREDIT_ACCOUNT;
import static com.lulobank.clients.services.utils.ProductTypeEnum.SAVING_ACCOUNT;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

public class GenerateOfferActionTest extends AbstractBaseUnitTest {
    public static final int SERVICE_CODE = 500;
    public static final String SERVICE_MESSAGE = "ERROR";
    private GenerateOfferAction testClass;
    private Response<ClientEntity> clientEntityResponse;
    private ClientLoanRequested clientLoanRequested;
    private CreditRiskAnalysis riskAnalysis;
    private LoanClientRequested loanClientRequested;

    @Override
    protected void init() {
        riskAnalysis=Sample.creditRiskAnalysisBuilder(AMOUNT,MAX_AMOUNT_INSTALLMENT,INTEREST_RATE);
        loanClientRequested=Sample.loanClientRequestedBuilder(AMOUNT,PURPOSE);
        clientLoanRequested = Sample.clientLoanRequestedBuilder(ID_CLIENT, AMOUNT, PURPOSE);
        testClass = new GenerateOfferAction(clientsOutboundAdapter);
    }

    @Test
    public void generateOfferFromOnboarding() {
        OnBoardingStatus onBoardingStatus=onBoardingStatusBuilder(CREDIT_ACCOUNT,loanClientRequested, CheckPoints.CLIENT_VERIFICATION.name());
        ClientEntity clientEntity = Sample.clientEntityBuilder(onBoardingStatus,riskAnalysis);
        clientEntityResponse = new Response<>(clientEntity);
        when(clientsOutboundAdapter.getInitialOffersOperations().initialOffers(any(HashMap.class), any(GetOfferToClient.class),anyString()))
                .thenReturn(true);
        when(databaseReference.child(anyString())).thenReturn(databaseReference);
        testClass.run(clientEntityResponse, clientLoanRequested);
        Mockito.verify(clientsRepository, times(1)).save(any());
        Mockito.verify(databaseReference, times(1)).updateChildrenAsync(any());
    }
    @Test
    public void generateOfferFromHome() {
        LoanRequested loanRequested=Sample.loanRequestedBuilder(loanClientRequested,LoanRequestedStatus.IN_PROGRESS);
        ClientEntity clientEntity = Sample.clientEntityBuilder(loanRequested,riskAnalysis);
        clientEntityResponse = new Response<>(clientEntity);
        when(clientsOutboundAdapter.getInitialOffersOperations().initialOffers(any(HashMap.class), any(GetOfferToClient.class),anyString()))
                .thenReturn(true);
        when(databaseReference.child(anyString())).thenReturn(databaseReference);
        testClass.run(clientEntityResponse, clientLoanRequested);
        Mockito.verify(clientsRepository, times(1)).save(any());
        Mockito.verify(databaseReference, times(1)).updateChildrenAsync(any());
    }

    @Test()
    public void unsupportedOperationException() {
        ClientEntity clientEntity = Sample.clientEntityBuilder();
        clientEntity.setOnBoardingStatus(Sample.onBoardingStatusBuilder(SAVING_ACCOUNT));
        clientEntityResponse = new Response<>(clientEntity);
        when(databaseReference.child(anyString())).thenReturn(databaseReference);
        testClass.run(clientEntityResponse, clientLoanRequested);
        Mockito.verify(clientsRepository, never()).save(any());

    }

    @Test()
    public void initialsOfferException() {
        OnBoardingStatus onBoardingStatus=onBoardingStatusBuilder(CREDIT_ACCOUNT,loanClientRequested, CheckPoints.CLIENT_VERIFICATION.name());
        ClientEntity clientEntity = Sample.clientEntityBuilder(onBoardingStatus,riskAnalysis);
        clientEntityResponse = new Response<>(clientEntity);
        when(clientsOutboundAdapter.getInitialOffersOperations().initialOffers(any(HashMap.class), any(GetOfferToClient.class),anyString()))
                .thenThrow(new InitialOffersException(SERVICE_CODE, SERVICE_MESSAGE));
        when(databaseReference.child(anyString())).thenReturn(databaseReference);
        testClass.run(clientEntityResponse, clientLoanRequested);
        Mockito.verify(clientsRepository, never()).save(any());
        Mockito.verify(databaseReference, times(1)).updateChildrenAsync(any());

    }
}
