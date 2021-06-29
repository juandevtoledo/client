package com.lulobank.clients.starter.v3.adapters.in.sqs.handler;

import com.lulobank.clients.services.application.usecase.loanautomaticpayment.NotifyAutomaticPaymentUseCase;
import com.lulobank.clients.services.domain.findclientbyidbsc.NotifyAutomaticPaymentRequest;
import com.lulobank.clients.starter.v3.adapters.in.sqs.event.LoanAutomaticPayment;
import io.vavr.control.Try;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.lulobank.clients.starter.adapter.Constant.PAYMENT_STATUS;
import static com.lulobank.clients.starter.adapter.Constant.VALUED_PAID;
import static com.lulobank.clients.starter.v3.adapters.out.Constant.ID_CBS;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.is;

public class LoanAutomaticPaymentHandlerTest {

    @Mock
    private NotifyAutomaticPaymentUseCase notifyAutomaticPaymentUseCase;
    private LoanAutomaticPaymentHandler loanAutomaticPaymentHandler;
    @Captor
    private ArgumentCaptor<NotifyAutomaticPaymentRequest> notifyAutomaticPaymentCaptor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        loanAutomaticPaymentHandler = new LoanAutomaticPaymentHandler(notifyAutomaticPaymentUseCase);
    }

    @Test
    public void processLoanAutomaticPaymentOk() {
        when(notifyAutomaticPaymentUseCase.execute(notifyAutomaticPaymentCaptor.capture())).thenReturn(Try.run(System.out::println));
        Try<Void> response = loanAutomaticPaymentHandler.execute(buildEvent());
        assertThat(response.isSuccess(),is(true));
        assertThat(notifyAutomaticPaymentCaptor.getValue().getCbsId(),is(ID_CBS));
        assertThat(notifyAutomaticPaymentCaptor.getValue().getPaymentStatus(),is(PAYMENT_STATUS));
        assertThat(notifyAutomaticPaymentCaptor.getValue().getValuePaid(),is(VALUED_PAID));
    }

    private LoanAutomaticPayment buildEvent() {
        LoanAutomaticPayment loanAutomaticPayment = new LoanAutomaticPayment();
        loanAutomaticPayment.setCbsId(ID_CBS);
        loanAutomaticPayment.setPaymentStatus(PAYMENT_STATUS);
        loanAutomaticPayment.setValuePaid(VALUED_PAID);
        return loanAutomaticPayment;
    }
}
