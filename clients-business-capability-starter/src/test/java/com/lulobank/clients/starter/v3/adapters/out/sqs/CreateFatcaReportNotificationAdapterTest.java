package com.lulobank.clients.starter.v3.adapters.out.sqs;

import co.com.lulobank.tracing.sqs.SqsBraveTemplate;
import com.lulobank.clients.services.events.LoanAutomaticDebitMessage;
import com.lulobank.clients.starter.v3.adapters.out.Sample;
import com.lulobank.clients.starter.v3.adapters.out.sqs.dto.CreateReportMessage;
import com.lulobank.clients.starter.v3.adapters.out.sqs.dto.EmailNotificationMessage;
import com.lulobank.events.api.Event;
import io.vavr.control.Try;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

public class CreateFatcaReportNotificationAdapterTest {

    @Mock
    private SqsBraveTemplate sqsBraveTemplate;

    private CreateFatcaReportNotificationAdapter target;

    @Captor
    private ArgumentCaptor<String> endpointCaptor;
    @Captor
    private ArgumentCaptor<Event<CreateReportMessage>> eventArgumentCaptor;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        target = new CreateFatcaReportNotificationAdapter("sqs.endpoint", sqsBraveTemplate);
    }

    @Test
    public void sendEventFail(){
        doThrow(RuntimeException.class).when(sqsBraveTemplate).convertAndSend(endpointCaptor.capture(),
                eventArgumentCaptor.capture());
        Try<Void> response = target.sendReport(Sample.CLIENT_ID,"FATCA","FATCA_Report",null);
        assertThat(endpointCaptor.getValue(), is("sqs.endpoint"));
        assertThat(eventArgumentCaptor.getValue().getPayload().getProductType(), is("FATCA"));
        assertThat(eventArgumentCaptor.getValue().getPayload().getReportType(), is("FATCA_Report"));
        assertThat(response.isFailure(), is(true));
        assertThat(response.isSuccess(), is(false));


    }

    @Test
    public void sendEventSuccess(){
        doNothing().when(sqsBraveTemplate).convertAndSend(endpointCaptor.capture(), eventArgumentCaptor.capture());
        Try<Void> response = target.sendReport(Sample.CLIENT_ID,"FATCA","FATCA_Report",null);
        assertThat(endpointCaptor.getValue(), is("sqs.endpoint"));
        assertThat(eventArgumentCaptor.getValue().getPayload().getProductType(), is("FATCA"));
        assertThat(eventArgumentCaptor.getValue().getPayload().getReportType(), is("FATCA_Report"));
        assertThat(response.isSuccess(), is(true));
        assertThat(response.isFailure(), is(false));


    }
}
