package com.lulobank.clients.v3.usecase.notification;

import com.lulobank.clients.services.SamplesV3;
import com.lulobank.clients.services.application.BaseUnitTest;
import com.lulobank.clients.services.domain.error.UseCaseResponseError;
import com.lulobank.clients.services.domain.notification.NotificationDisabledRequest;
import io.vavr.control.Either;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.springframework.messaging.MessagingException;

import static com.lulobank.clients.v3.error.UseCaseErrorStatus.CLI_180;
import static com.lulobank.clients.v3.error.UseCaseErrorStatus.DEFAULT_DETAIL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

public class NotificationDisabledUseCaseTest extends BaseUnitTest {

    @InjectMocks
    NotificationDisabledUseCase testedClass;

    @Test
    public void shouldReturnOkSendNotificationDisabled(){

        when(clientAlertsProperties.getNotificationDisabled())
                .thenReturn(SamplesV3.getNotificationDisabledDetails());
        doNothing().when(messageService).sendNotificationDisabled(notificationDisabledCaptor.capture());
        NotificationDisabledRequest request = SamplesV3.getNotificationDisabledRequest();
        Either<UseCaseResponseError, Void> response = testedClass.execute(request);
        assertTrue(response.isRight());
        assertEquals(request.getIdClient(), notificationDisabledCaptor.getValue().getIdClient());
        assertEquals(NotificationDisabledType.PUSH_NOTIFICATIONS_DISABLED.toString(),
                notificationDisabledCaptor.getValue().getOperation());
    }

    @Test
    public void shouldReturnFailNotificationDisabled(){
        when(clientAlertsProperties.getNotificationDisabled())
                .thenReturn(SamplesV3.getNotificationDisabledDetails());
        doThrow(MessagingException.class).when(messageService).sendNotificationDisabled(notificationDisabledCaptor.capture());
        NotificationDisabledRequest request = SamplesV3.getNotificationDisabledRequest();
        Either<UseCaseResponseError, Void> response = testedClass.execute(request);
        assertTrue(response.isLeft());
        assertEquals(CLI_180.name(), response.getLeft().getBusinessCode());
        assertEquals("500", response.getLeft().getProviderCode());
        assertEquals(DEFAULT_DETAIL, response.getLeft().getDetail());
    }

}
