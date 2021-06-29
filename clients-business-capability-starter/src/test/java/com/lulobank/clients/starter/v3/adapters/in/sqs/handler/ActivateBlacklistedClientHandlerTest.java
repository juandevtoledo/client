package com.lulobank.clients.starter.v3.adapters.in.sqs.handler;

import com.lulobank.clients.services.domain.RiskLevelBlackList;
import com.lulobank.clients.services.domain.StateBlackList;
import com.lulobank.clients.services.domain.activateblacklistedclient.ActivateBlacklistedClientRequest;
import com.lulobank.clients.starter.v3.adapters.in.sqs.event.ActivateBlacklistedClient;
import com.lulobank.clients.v3.adapters.port.in.activateblacklistedclient.ActivateBlacklistedClientPort;
import io.vavr.control.Try;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static com.lulobank.clients.starter.v3.adapters.in.sqs.factory.EntityFactory.ActivateBlacklistedClientFactory.getActivateBlacklistedClient;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class ActivateBlacklistedClientHandlerTest {

    private static final String CARD_ID = "26341120";
    private static final String BIOMETRIC_TRANSACTION = "4711120";
    private static final LocalDateTime REPORT_DATE_BLACKLIST = LocalDateTime.parse("2020-11-26T09:53:04.545");
    private static final LocalDateTime WHITELIST_EXPIRATION = LocalDateTime.parse("2020-12-26T09:53:04.545");

    @Mock
    private ActivateBlacklistedClientPort activateBlacklistedClientPort;
    @InjectMocks
    private ActivateBlacklistedClientHandler activateBlacklistedClientHandler;

    @Captor
    private ArgumentCaptor<ActivateBlacklistedClientRequest> captorRequest;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void processLoanAutomaticPaymentOk() {
        when(activateBlacklistedClientPort.execute(captorRequest.capture())).thenReturn(Try.run(System.out::println));
        Try<Void> response = activateBlacklistedClientHandler.execute(buildEvent());
        assertThat(response.isSuccess(),is(true));
        assertThat(captorRequest.getValue().getIdTransactionBiometric(),is(BIOMETRIC_TRANSACTION));
        assertThat(captorRequest.getValue().getClientPersonalInformation().getDocument().getIdCard(),is(CARD_ID));
        assertThat(captorRequest.getValue().getBlacklist().getRiskLevel(),is(RiskLevelBlackList.MID_RISK.getLevel()));
        assertThat(captorRequest.getValue().getBlacklist().getReportDate(),is(REPORT_DATE_BLACKLIST));
        assertThat(captorRequest.getValue().getBlacklist().getStatus(),is(StateBlackList.WHITELISTED));
        assertThat(captorRequest.getValue().getWhitelistExpirationDate(),is(WHITELIST_EXPIRATION));
    }

    @Test
    public void processLoanAutomaticPaymentError() {
        when(activateBlacklistedClientPort.execute(captorRequest.capture())).thenReturn(Try.failure(new RuntimeException()));
        Try<Void> response = activateBlacklistedClientHandler.execute(buildEvent());
        assertThat(response.isFailure(),is(true));
        assertThat(captorRequest.getValue().getIdTransactionBiometric(),is(BIOMETRIC_TRANSACTION));
        assertThat(captorRequest.getValue().getClientPersonalInformation().getDocument().getIdCard(),is(CARD_ID));
        assertThat(captorRequest.getValue().getBlacklist().getRiskLevel(),is(RiskLevelBlackList.MID_RISK.getLevel()));
        assertThat(captorRequest.getValue().getBlacklist().getReportDate(),is(REPORT_DATE_BLACKLIST));
        assertThat(captorRequest.getValue().getBlacklist().getStatus(),is(StateBlackList.WHITELISTED));
        assertThat(captorRequest.getValue().getWhitelistExpirationDate(),is(WHITELIST_EXPIRATION));
    }

    private ActivateBlacklistedClient buildEvent() {
       return getActivateBlacklistedClient();
    }


}
