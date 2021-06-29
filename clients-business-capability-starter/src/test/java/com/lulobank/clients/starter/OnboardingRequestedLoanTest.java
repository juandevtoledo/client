package com.lulobank.clients.starter;

import static com.lulobank.clients.services.utils.ClientHelper.REFERENCE_ON_BOARDING_FIREBASE_CLIENTS;
import static com.lulobank.clients.services.utils.StatusClientVerificationFirebaseEnum.OK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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

/**
 * @deprecated as security issues were identified with the related endpoint
 * Please delete once the endpoint <i>/clients/onboarding/requested/loan</i> has been removed
 */
@Deprecated
public class OnboardingRequestedLoanTest extends AbstractBaseIntegrationTest {
    private static final String TESTED_URL = "/onboarding/requested/loan";
    private ClientLoanRequested clientLoanRequested;
    private ClientsV3Entity clientEntityFound;

    @Value("classpath:mocks/onboardingclients/ClientNotFoundResponse.json")
    private Resource responseClientOnboardingNotFound;

    @Override
    protected void init() {
        clientLoanRequested = new ClientLoanRequested();
        clientLoanRequested.setAmount(500000d);
        clientLoanRequested.setLoanPurpose("Viajes");
        clientLoanRequested.setIdClient(ID_CLIENT);

        OnBoardingStatusV3 onBoardingStatus = new OnBoardingStatusV3();
        onBoardingStatus.setProductSelected(ProductTypeEnum.CREDIT_ACCOUNT.name());
        clientEntityFound = new ClientsV3Entity();
        clientEntityFound.setIdClient(ID_CLIENT);
        clientEntityFound.setDateOfIssue(LocalDate.now());
        clientEntityFound.setIdCard("12345678");
        clientEntityFound.setOnBoardingStatus(onBoardingStatus);
    }

    @Test
    public void flow_credits_from_onboarding() throws Exception {
        when(clientsV3Repository.findByIdClient(any(String.class))).thenReturn(Option.of(clientEntityFound));
        when(clientsV3Repository.save(any())).thenReturn(Try.of(()->clientEntityFound));

        mockMvc.perform(MockMvcRequestBuilders.post(TESTED_URL)
                .contentType(CONTENT_TYPE_JSON)
                .with(bearerToken())
                .content(objectMapper.writeValueAsString(clientLoanRequested)))
                .andExpect(status().isCreated());

        verify(clientsV3Repository).save(refEq(clientEntityFound));
    }

    // TODO: migrar a pruebas unitarias
    @Test
    public void flow_credits_from_onboarding_generate_offers() throws Exception {
        clientEntityFound.setCreditRiskAnalysis(getCreditRiskAnalysis());
        clientEntityFound.getOnBoardingStatus().setCheckpoint(CheckPoints.CLIENT_VERIFICATION.name());

        when(clientsV3Repository.findByIdClient(any(String.class))).thenReturn(Option.of(clientEntityFound));
        when(databaseReference.child(anyString())).thenReturn(databaseReference);
        when(clientsV3Repository.save(any())).thenReturn(Try.of(()->clientEntityFound));

        mockMvc.perform(MockMvcRequestBuilders.post(TESTED_URL)
                .contentType(CONTENT_TYPE_JSON)
                .with(bearerToken())
                .content(objectMapper.writeValueAsString(clientLoanRequested)))
                .andExpect(status().isCreated());

        verify(clientsRepository).save(clientEntityCaptor.capture());
        verify(clientsV3Repository).save(refEq(clientEntityFound));
        verify(databaseReference, times(2)).child(stringArgumentCaptor.capture());
        verify(databaseReference).updateChildrenAsync(updateChild.capture());

        List<String> firebaseUdpate = stringArgumentCaptor.getAllValues();
        Map<String, Object> updateChildvalue = updateChild.getValue();
        ClientVerificationFirebase firebaseUpdateValue = (ClientVerificationFirebase) updateChildvalue.get("clientVerification");
        assertTrue("Loan requested saved",
                Objects.nonNull(clientEntityCaptor.getValue().getOnBoardingStatus().getLoanClientRequested()));
        assertTrue("Firebase child contains", firebaseUdpate.contains(REFERENCE_ON_BOARDING_FIREBASE_CLIENTS));
        assertTrue("Firebase child contains", firebaseUdpate.contains(clientEntityFound.getIdClient()));
        assertEquals("Firebase update child verification result select", OK.name(),
                firebaseUpdateValue.getVerificationResult());
    }

    // TODO: migrar a pruebas unitarias
    @Test
    public void flow_credits_from_home() throws Exception {
        clientEntityFound.getOnBoardingStatus().setProductSelected(ProductTypeEnum.SAVING_ACCOUNT.name());
        clientEntityFound.setLoanRequested(new LoanRequestedV3(LoanRequestedStatus.IN_PROGRESS.name()));
        when(clientsV3Repository.findByIdClient(any(String.class))).thenReturn(Option.of(clientEntityFound));
        when(clientsV3Repository.save(any())).thenReturn(Try.of(()->clientEntityFound));

        mockMvc.perform(MockMvcRequestBuilders.post(TESTED_URL)
                .contentType(CONTENT_TYPE_JSON)
                .with(bearerToken())
                .content(objectMapper.writeValueAsString(clientLoanRequested)))
                .andExpect(status().isCreated());

        verify(clientsV3Repository).save(refEq(clientEntityFound));
    }

    // TODO: migrar a pruebas unitarias
    @Test
    public void flow_credits_from_home_generate_offers() throws Exception {
        clientEntityFound.getOnBoardingStatus().setProductSelected(ProductTypeEnum.SAVING_ACCOUNT.name());
        clientEntityFound.setLoanRequested(new LoanRequestedV3(LoanRequestedStatus.IN_PROGRESS.name()));
        clientEntityFound.setCreditRiskAnalysis(getCreditRiskAnalysis());

        when(clientsV3Repository.findByIdClient(any(String.class))).thenReturn(Option.of(clientEntityFound));
        when(databaseReference.child(anyString())).thenReturn(databaseReference);
        when(clientsV3Repository.save(any())).thenReturn(Try.of(()->clientEntityFound));

        mockMvc.perform(MockMvcRequestBuilders.post(TESTED_URL)
                .contentType(CONTENT_TYPE_JSON)
                .with(bearerToken())
                .content(objectMapper.writeValueAsString(clientLoanRequested)))
                .andExpect(status().isCreated());

        verify(clientsRepository).save(clientEntityCaptor.capture());
        verify(databaseReference, times(2)).child(stringArgumentCaptor.capture());
        verify(databaseReference).updateChildrenAsync(updateChild.capture());
        verify(clientsV3Repository).save(refEq(clientEntityFound));

        List<String> firebaseUpdate = stringArgumentCaptor.getAllValues();
        Map<String, Object> updateChildValue = updateChild.getValue();
        LoanRequestedStatusFirebase firebaseUpdateValue = (LoanRequestedStatusFirebase) updateChildValue.get("loanVerification");
        assertTrue(Objects.nonNull(clientEntityCaptor.getValue().getLoanRequested().getLoanClientRequested()));
        assertTrue("Firebase child contains", firebaseUpdate.contains(ClientHelper.REFERENCE_LOAN_REQUESTED_FIREBASE_CLIENTS));
        assertTrue("Firebase child contains", firebaseUpdate.contains(clientEntityFound.getIdClient()));
        assertEquals("Firebase update child verification result select", "FINISHED", firebaseUpdateValue.getStatus());
    }

    @Test
    public void shouldReturnNotFoundClientNotExistInDBRequestedLoan() throws Exception {
        when(clientsV3Repository.findByIdClient(any(String.class))).thenReturn(Option.none());

        mockMvc.perform(MockMvcRequestBuilders.post(TESTED_URL)
                .contentType(CONTENT_TYPE_JSON)
                .with(bearerToken())
                .content(objectMapper.writeValueAsString(clientLoanRequested)))
                .andExpect(status().isNotFound())
                .andExpect(content().json(FileUtils.readFileToString(responseClientOnboardingNotFound.getFile(), StandardCharsets.UTF_8)));
    }

    @Test
    public void shouldReturnNotFoundErrorSavingInDBRequestedLoan() throws Exception {
        when(clientsV3Repository.findByIdClient(any(String.class))).thenReturn(Option.of(clientEntityFound));
        when(clientsRepository.save(any(ClientEntity.class))).thenThrow(new SdkClientException("ERROR SAVING"));
        doThrow(new SdkClientException("ERROR SAVING")).when(clientsV3Repository).save(refEq(clientEntityFound));


        mockMvc.perform(MockMvcRequestBuilders.post(TESTED_URL)
                .contentType(CONTENT_TYPE_JSON)
                .with(bearerToken())
                .content(objectMapper.writeValueAsString(clientLoanRequested)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void shouldReturnAcceptedOnRequestedLoan_and_updateOffers() throws Exception {
        when(clientsV3Repository.save(any())).thenReturn(Try.of(()->clientEntityFound));

        LoanClientRequestedV3 loanClientRequested = new LoanClientRequestedV3();
        loanClientRequested.setAmount(50000d);
        loanClientRequested.setLoanPurpose("VIAJES");
        updateOffers(loanClientRequested);
        verify(initialOffersOperations).initialOffers(hashMapArgumentCaptor.capture(), getOfferToClient.capture(), anyString());
        GetOfferToClient getOfferToClientValue = getOfferToClient.getValue();
        assertEquals("Inital Offers id Client is right", clientLoanRequested.getIdClient(), getOfferToClientValue.getIdClient());
        assertEquals("Inital Offers Purpose is right", clientLoanRequested.getLoanPurpose(), getOfferToClientValue.getLoanPurpose());
        ResultV3 creditRiskAnalysisResult = clientEntityFound.getCreditRiskAnalysis().getResults().stream().findFirst().get();

        assertEquals("Inital Offers Risk Engine amount right", creditRiskAnalysisResult.getAmount(),
                getOfferToClientValue.getRiskEngineAnalysis().getAmount());
        assertEquals("Inital Offers Risk Engine amount installments right", creditRiskAnalysisResult.getMaxAmountInstallment(),
                getOfferToClientValue.getRiskEngineAnalysis().getMaxAmountInstallment());
        assertEquals("Inital Offers Risk Engine interest rate right", creditRiskAnalysisResult.getInterestRate(),
                getOfferToClientValue.getRiskEngineAnalysis().getInterestRate());
    }

    public void updateOffers(LoanClientRequestedV3 loanClientRequested) throws Exception {
        clientEntityFound.getOnBoardingStatus().setCheckpoint(CheckPoints.CLIENT_VERIFICATION.name());
        clientEntityFound.setCreditRiskAnalysis(getCreditRiskAnalysis());
        clientEntityFound.getOnBoardingStatus().setLoanClientRequested(loanClientRequested);

        when(databaseReference.child(anyString())).thenReturn(databaseReference);
        when(clientsV3Repository.findByIdClient(any(String.class))).thenReturn(Option.of(clientEntityFound));
        when(initialOffersOperations.initialOffers(any(HashMap.class), any(GetOfferToClient.class), anyString())).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.post(TESTED_URL)
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
