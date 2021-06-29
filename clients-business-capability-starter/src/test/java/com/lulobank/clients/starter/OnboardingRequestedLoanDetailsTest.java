package com.lulobank.clients.starter;

import static com.lulobank.clients.services.utils.ClientHelper.REFERENCE_ON_BOARDING_FIREBASE_CLIENTS;
import static com.lulobank.clients.services.utils.StatusClientVerificationFirebaseEnum.OK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.amazonaws.SdkClientException;
import com.lulobank.clients.sdk.operations.dto.ClientLoanRequested;
import com.lulobank.clients.sdk.operations.util.CheckPoints;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.outboundadapters.model.CreditRiskAnalysis;
import com.lulobank.clients.services.outboundadapters.model.LoanClientRequested;
import com.lulobank.clients.services.outboundadapters.model.LoanRequested;
import com.lulobank.clients.services.outboundadapters.model.OnBoardingStatus;
import com.lulobank.clients.services.outboundadapters.model.Result;
import com.lulobank.clients.services.utils.ClientHelper;
import com.lulobank.clients.services.utils.ClientVerificationFirebase;
import com.lulobank.clients.services.utils.LoanRequestedStatus;
import com.lulobank.clients.services.utils.LoanRequestedStatusFirebase;
import com.lulobank.clients.services.utils.ProductTypeEnum;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.CreditRiskAnalysisV3;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.LoanClientRequestedV3;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.LoanRequestedV3;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.OnBoardingStatusV3;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ResultV3;
import com.lulobank.credits.sdk.dto.initialofferv2.GetOfferToClient;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import io.vavr.control.Option;
import io.vavr.control.Try;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

// TODO: pasar estas pruebas, a unitarias
public class OnboardingRequestedLoanDetailsTest extends AbstractBaseIntegrationTest {

    private static final String TESTED_URL = "/onboarding/{idClient}/requested/loan";
    private ClientLoanRequested clientLoanRequested;
    private ClientsV3Entity clientEntityResponse;

    @Value("classpath:mocks/onboardingclients/ClientNotFoundResponse.json")
    private Resource responseClientOnboardingNotFound;

    @Override
    protected void init() {
        clientLoanRequested = new ClientLoanRequested();
        clientLoanRequested.setAmount(500000d);
        clientLoanRequested.setLoanPurpose("Viajes");

        OnBoardingStatusV3 onBoardingStatus = new OnBoardingStatusV3();
        onBoardingStatus.setProductSelected(ProductTypeEnum.CREDIT_ACCOUNT.name());
        clientEntityResponse = new ClientsV3Entity();
        clientEntityResponse.setIdClient(ID_CLIENT);
        clientEntityResponse.setDateOfIssue(LocalDate.now());
        clientEntityResponse.setIdCard("12345678");
        clientEntityResponse.setOnBoardingStatus(onBoardingStatus);
    }

    @Test
    public void flow_credits_from_onboarding() throws Exception {
        when(clientsV3Repository.findByIdClient(any(String.class)))
                .thenReturn(Option.of(clientEntityResponse));
        when(clientsV3Repository.save(any())).thenReturn(Try.of(()->clientEntityResponse));

        mockMvc.perform(MockMvcRequestBuilders.post(TESTED_URL, ID_CLIENT)
                .contentType(CONTENT_TYPE_JSON)
                .with(bearerToken())
                .content(objectMapper.writeValueAsString(clientLoanRequested)))
                .andExpect(status().isCreated());

        verify(clientsV3Repository).save(refEq(clientEntityResponse));
    }

    // TODO: migrar a pruebas unitarias
    @Test
    public void flow_credits_from_onboarding_generate_offers() throws Exception {
        clientEntityResponse.setCreditRiskAnalysis(getCreditRiskAnalysis());
        clientEntityResponse.getOnBoardingStatus().setCheckpoint(CheckPoints.CLIENT_VERIFICATION.name());

        when(clientsV3Repository.findByIdClient(any(String.class))).thenReturn(Option.of(clientEntityResponse));
        when(clientsV3Repository.save(any())).thenReturn(Try.of(()->clientEntityResponse));

        when(databaseReference.child(anyString())).thenReturn(databaseReference);

        mockMvc.perform(MockMvcRequestBuilders.post(TESTED_URL, ID_CLIENT)
                .contentType(CONTENT_TYPE_JSON)
                .with(bearerToken())
                .content(objectMapper.writeValueAsString(clientLoanRequested)))
                .andExpect(status().isCreated());

        verify(clientsV3Repository).save(refEq(clientEntityResponse));
        verify(databaseReference, times(2)).child(stringArgumentCaptor.capture());
        verify(databaseReference).updateChildrenAsync(updateChild.capture());

        List<String> firebaseUdpate = stringArgumentCaptor.getAllValues();
        Map<String, Object> updateChildvalue = updateChild.getValue();
        ClientVerificationFirebase firebaseUpdateValue = (ClientVerificationFirebase) updateChildvalue.get("clientVerification");
        assertTrue("Firebase child contains", firebaseUdpate.contains(REFERENCE_ON_BOARDING_FIREBASE_CLIENTS));
        assertTrue("Firebase child contains", firebaseUdpate.contains(clientEntityResponse.getIdClient()));
        assertEquals("Firebase update child verification result select", OK.name(), firebaseUpdateValue.getVerificationResult());
    }

    // TODO: migrar a pruebas unitarias
    @Test
    public void flow_credits_from_home() throws Exception {
        clientEntityResponse.getOnBoardingStatus().setProductSelected(ProductTypeEnum.SAVING_ACCOUNT.name());
        clientEntityResponse.setLoanRequested(new LoanRequestedV3(LoanRequestedStatus.IN_PROGRESS.name()));

        when(clientsV3Repository.findByIdClient(any(String.class))).thenReturn(Option.of(clientEntityResponse));
        when(clientsV3Repository.save(any())).thenReturn(Try.of(()->clientEntityResponse));

        mockMvc.perform(MockMvcRequestBuilders.post(TESTED_URL, ID_CLIENT)
                .contentType(CONTENT_TYPE_JSON)
                .with(bearerToken())
                .content(objectMapper.writeValueAsString(clientLoanRequested)))
                .andExpect(status().isCreated());

        verify(clientsV3Repository).save(refEq(clientEntityResponse));
        verifyZeroInteractions(clientsRepository);
    }

    // TODO: migrar a pruebas unitarias
    @Test
    public void flow_credits_from_home_generate_offers() throws Exception {
        clientEntityResponse.getOnBoardingStatus().setProductSelected(ProductTypeEnum.SAVING_ACCOUNT.name());
        clientEntityResponse.setLoanRequested(new LoanRequestedV3(LoanRequestedStatus.IN_PROGRESS.name()));
        clientEntityResponse.setCreditRiskAnalysis(getCreditRiskAnalysis());

        when(clientsV3Repository.findByIdClient(any(String.class))).thenReturn(Option.of(clientEntityResponse));
        when(databaseReference.child(anyString())).thenReturn(databaseReference);
        when(clientsV3Repository.save(any())).thenReturn(Try.of(()->clientEntityResponse));

        mockMvc.perform(MockMvcRequestBuilders.post(TESTED_URL, ID_CLIENT)
                .contentType(CONTENT_TYPE_JSON)
                .with(bearerToken())
                .content(objectMapper.writeValueAsString(clientLoanRequested)))
                .andExpect(status().isCreated());

        verify(clientsRepository).save(clientEntityCaptor.capture());
        verify(clientsV3Repository).save( refEq(clientEntityResponse));
        verify(databaseReference, times(2)).child(stringArgumentCaptor.capture());
        verify(databaseReference).updateChildrenAsync(updateChild.capture());
        List<String> firebaseUdpate = stringArgumentCaptor.getAllValues();
        Map<String, Object> updateChildvalue = updateChild.getValue();
        LoanRequestedStatusFirebase firebaseUpdateValue = (LoanRequestedStatusFirebase) updateChildvalue.get("loanVerification");
        assertTrue(Objects.nonNull(clientEntityCaptor.getValue().getLoanRequested().getLoanClientRequested()));
        assertTrue("Firebase child contains", firebaseUdpate.contains(ClientHelper.REFERENCE_LOAN_REQUESTED_FIREBASE_CLIENTS));
        assertTrue("Firebase child contains", firebaseUdpate.contains(clientEntityResponse.getIdClient()));
        assertEquals("Firebase update child verification result select", "FINISHED", firebaseUpdateValue.getStatus());
    }

    @Test
    public void shouldReturnNotFoundClientNotExistInDBRequestedLoan() throws Exception {
        when(clientsV3Repository.findByIdClient(any(String.class))).thenReturn(Option.none());

        mockMvc.perform(MockMvcRequestBuilders.post(TESTED_URL, ID_CLIENT)
                .contentType(CONTENT_TYPE_JSON)
                .with(bearerToken())
                .content(objectMapper.writeValueAsString(clientLoanRequested)))
                .andExpect(status().isNotFound())
                .andExpect(content().json(FileUtils.readFileToString(responseClientOnboardingNotFound.getFile(), StandardCharsets.UTF_8)));
    }

    @Test
    public void shouldReturnNotFoundErrorSavingInDBRequestedLoan() throws Exception {
        when(clientsV3Repository.findByIdClient(any(String.class))).thenReturn(Option.of(clientEntityResponse));
        doThrow(new SdkClientException("ERROR SAVING")).when(clientsV3Repository).save(any());

        mockMvc.perform(MockMvcRequestBuilders.post(TESTED_URL, ID_CLIENT)
                .contentType(CONTENT_TYPE_JSON)
                .with(bearerToken())
                .content(objectMapper.writeValueAsString(clientLoanRequested)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void shouldReturnAcceptedOnRequestedLoan_and_updateOffers() throws Exception {
        when(clientsV3Repository.save(any())).thenReturn(Try.of(()->clientEntityResponse));

        LoanClientRequestedV3 loanClientRequested = new LoanClientRequestedV3();
        loanClientRequested.setAmount(50000d);
        loanClientRequested.setLoanPurpose("VIAJES");
        updateOffers(loanClientRequested);
        verify(initialOffersOperations)
                .initialOffers(hashMapArgumentCaptor.capture(), getOfferToClient.capture(), anyString());
        GetOfferToClient getOfferToClientValue = getOfferToClient.getValue();
        assertEquals("Inital Offers id Client is right", ID_CLIENT, getOfferToClientValue.getIdClient());
        assertEquals("Inital Offers Purpose is right", clientLoanRequested.getLoanPurpose(), getOfferToClientValue.getLoanPurpose());
        ResultV3 creditRiskAnalysisResult = clientEntityResponse.getCreditRiskAnalysis().getResults().stream().findFirst().get();
        assertEquals("Inital Offers Risk Engine amount right", creditRiskAnalysisResult.getAmount(),
                getOfferToClientValue.getRiskEngineAnalysis().getAmount());
        assertEquals("Inital Offers Risk Engine amount installments right", creditRiskAnalysisResult.getMaxAmountInstallment(),
                getOfferToClientValue.getRiskEngineAnalysis().getMaxAmountInstallment());
        assertEquals("Inital Offers Risk Engine interest rate right", creditRiskAnalysisResult.getInterestRate(),
                getOfferToClientValue.getRiskEngineAnalysis().getInterestRate());
    }

    public void updateOffers(LoanClientRequestedV3 loanClientRequested) throws Exception {
        clientEntityResponse.getOnBoardingStatus().setCheckpoint(CheckPoints.CLIENT_VERIFICATION.name());
        clientEntityResponse.setCreditRiskAnalysis(getCreditRiskAnalysis());
        clientEntityResponse.getOnBoardingStatus().setLoanClientRequested(loanClientRequested);

        when(databaseReference.child(anyString())).thenReturn(databaseReference);
        when(clientsV3Repository.findByIdClient(any(String.class))).thenReturn(Option.of(clientEntityResponse));

        when(initialOffersOperations.initialOffers(any(HashMap.class), any(GetOfferToClient.class),any()))
                .thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.post(TESTED_URL, ID_CLIENT)
                .contentType(CONTENT_TYPE_JSON)
                .with(bearerToken())
                .content(objectMapper.writeValueAsString(clientLoanRequested)))
                .andExpect(status().isCreated());
    }

    private CreditRiskAnalysisV3 getCreditRiskAnalysis() {
        CreditRiskAnalysisV3 creditRiskAnalysis = new CreditRiskAnalysisV3();
        List<ResultV3> resultList = new ArrayList<>();
        ResultV3 result = new ResultV3();
        result.setAmount(100d);
        result.setInstallments(12);
        result.setMaxAmountInstallment(50d);
        result.setInterestRate(5.5f);
        result.setType("dummy");
        resultList.add(result);
        creditRiskAnalysis.setResults(resultList);
        return creditRiskAnalysis;
    }
}
