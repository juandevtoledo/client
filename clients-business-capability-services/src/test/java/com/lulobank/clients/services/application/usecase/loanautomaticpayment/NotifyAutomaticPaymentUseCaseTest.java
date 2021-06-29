package com.lulobank.clients.services.application.usecase.loanautomaticpayment;

import com.lulobank.clients.services.application.port.out.clientnotify.ClientNotifyService;
import com.lulobank.clients.services.events.LoanAutomaticDebitMessage;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static com.github.jknack.handlebars.internal.lang3.StringUtils.SPACE;
import static com.lulobank.clients.services.application.Constant.ID_CBS;
import static com.lulobank.clients.services.application.Constant.PAYMENT_STATUS;
import static com.lulobank.clients.services.application.Constant.VALUE_PAID;
import static com.lulobank.clients.services.application.Sample.getClientsV3Entity;
import static com.lulobank.clients.services.application.Sample.notifyAutomaticPaymentRequestBuilder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

public class NotifyAutomaticPaymentUseCaseTest {

    @Mock
    private ClientsV3Repository clientsV3Repository;
    @Mock
    private ClientNotifyService clientNotifyService;
    
    private NotifyAutomaticPaymentUseCase notifyAutomaticPaymentUseCase;
    @Captor
    private ArgumentCaptor<LoanAutomaticDebitMessage> loanAutomaticDebitMessageCaptor;
    @Captor
    private ArgumentCaptor<String> idCbsCaptor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        notifyAutomaticPaymentUseCase = new NotifyAutomaticPaymentUseCase(clientsV3Repository, clientNotifyService);
    }

    @Test
    public void processEventOk() {
        ClientsV3Entity clientsV3Entity = getClientsV3Entity();
        when(clientNotifyService.automaticPayment(loanAutomaticDebitMessageCaptor.capture())).thenReturn(Try.run(() -> System.out.println("success process")));
        when(clientsV3Repository.findByIdCbs(idCbsCaptor.capture())).thenReturn(Option.of(getClientsV3Entity()));
        Try<Void> process = notifyAutomaticPaymentUseCase.execute(notifyAutomaticPaymentRequestBuilder().build());
        assertThat(process.isSuccess(), is(true));
        assertThat(idCbsCaptor.getValue(), is(ID_CBS));
        assertThat(loanAutomaticDebitMessageCaptor.getValue().getIdClient(), is(getClientsV3Entity().getIdClient()));
        assertThat(loanAutomaticDebitMessageCaptor.getValue().getPaymentStatus(), is(PAYMENT_STATUS));
        assertThat(loanAutomaticDebitMessageCaptor.getValue().getValuePaid(), is(VALUE_PAID));
        assertThat(loanAutomaticDebitMessageCaptor.getValue().getEmail(), is(clientsV3Entity.getEmailAddress()));
        assertThat(loanAutomaticDebitMessageCaptor.getValue().getFullName(), is(clientsV3Entity.getName().concat(SPACE).concat(clientsV3Entity.getLastName())));

    }


    @Test
    public void processFailedSinceRepositoryFailed() {

        when(clientsV3Repository.findByIdCbs(idCbsCaptor.capture())).thenReturn(Option.none());
        Try<Void> process = notifyAutomaticPaymentUseCase.execute(notifyAutomaticPaymentRequestBuilder().build());
        Mockito.verify(clientNotifyService, never()).automaticPayment(any());
        assertThat(process.isFailure(), is(true));
        assertThat(idCbsCaptor.getValue(), is(ID_CBS));

    }
}
