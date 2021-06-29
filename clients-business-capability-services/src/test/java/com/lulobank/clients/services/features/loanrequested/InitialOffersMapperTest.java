package com.lulobank.clients.services.features.loanrequested;

import com.lulobank.clients.sdk.operations.dto.ClientLoanRequested;
import com.lulobank.clients.services.Constants;
import com.lulobank.clients.services.Sample;
import com.lulobank.clients.services.outboundadapters.model.AdditionalPersonalInformation;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.outboundadapters.model.Result;
import com.lulobank.credits.sdk.dto.initialofferv2.GetOfferToClient;
import org.junit.Assert;
import org.junit.Test;

import static com.lulobank.clients.services.Constants.AMOUNT;
import static com.lulobank.clients.services.Constants.INTEREST_RATE;
import static com.lulobank.clients.services.Constants.LAST_NAME;
import static com.lulobank.clients.services.Constants.MAX_AMOUNT_INSTALLMENT;
import static com.lulobank.clients.services.Constants.NAME;
import static org.hamcrest.CoreMatchers.is;

public class InitialOffersMapperTest {

    @Test
    public void initialsOffers() {
        ClientEntity clientEntity = Sample.clientEntityBuilder();
        clientEntity.setAdditionalPersonalInformation(additionalPersonalInformation());
        clientEntity.setCreditRiskAnalysis(Sample.creditRiskAnalysisBuilder(AMOUNT, MAX_AMOUNT_INSTALLMENT, INTEREST_RATE));
        ClientLoanRequested clientLoanRequested = Sample.clientLoanRequestedBuilder(clientEntity.getIdClient(), AMOUNT, "viajes");
        GetOfferToClient getOfferToClient = InitialOffersMapper.INSTANCE.getOfferClientFrom(clientEntity, clientLoanRequested);
        Assert.assertThat(getOfferToClient.getIdClient(), is(clientEntity.getIdClient()));
        Assert.assertThat(getOfferToClient.getClientLoanRequestedAmount(), is(clientLoanRequested.getAmount()));
        Assert.assertThat(getOfferToClient.getLoanPurpose(), is(clientLoanRequested.getLoanPurpose()));
        Assert.assertThat(getOfferToClient.getClientInformation().getName(), is(clientEntity.getAdditionalPersonalInformation().getFirstName()));
        Assert.assertThat(getOfferToClient.getClientInformation().getLastName(), is(clientEntity.getAdditionalPersonalInformation().getFirstSurname()));
        Assert.assertThat(getOfferToClient.getClientInformation().getMiddleName(), is(clientEntity.getAdditionalPersonalInformation().getSecondName()));
        Assert.assertThat(getOfferToClient.getClientInformation().getSecondSurname(), is(clientEntity.getAdditionalPersonalInformation().getSecondSurname()));
        Assert.assertThat(getOfferToClient.getClientInformation().getEmail(), is(clientEntity.getEmailAddress()));
        Assert.assertThat(getOfferToClient.getClientInformation().getGender(), is(clientEntity.getGender()));
        Assert.assertThat(getOfferToClient.getClientInformation().getPhone().getNumber(), is(clientEntity.getPhoneNumber()));
        Assert.assertThat(getOfferToClient.getClientInformation().getPhone().getPrefix(), is(String.valueOf(clientEntity.getPhonePrefix())));
        Result creditsRiskAnalysis = clientEntity.getCreditRiskAnalysis().getResults().stream().findFirst().get();
        Assert.assertThat(getOfferToClient.getRiskEngineAnalysis().getAmount(), is(creditsRiskAnalysis.getAmount()));
        Assert.assertThat(getOfferToClient.getRiskEngineAnalysis().getInstallments(), is(creditsRiskAnalysis.getInstallments()));
        Assert.assertThat(getOfferToClient.getRiskEngineAnalysis().getInterestRate(), is(creditsRiskAnalysis.getInterestRate()));
        Assert.assertThat(getOfferToClient.getRiskEngineAnalysis().getMaxAmountInstallment(), is(creditsRiskAnalysis.getMaxAmountInstallment()));
    }

    public AdditionalPersonalInformation additionalPersonalInformation() {
        AdditionalPersonalInformation additionalPersonalInformation = new AdditionalPersonalInformation();
        additionalPersonalInformation.setSecondName(NAME);
        additionalPersonalInformation.setSecondSurname(LAST_NAME);
        return additionalPersonalInformation;
    }

}