package com.lulobank.clients.starter.adapter.in;

import com.lulobank.clients.services.domain.error.UseCaseResponseError;
import com.lulobank.clients.starter.adapter.BaseUnitTest;
import com.lulobank.clients.starter.adapter.in.dto.ErrorResponse;
import com.lulobank.clients.starter.adapter.in.dto.GenericResponse;
import com.lulobank.clients.starter.utils.Sample;
import com.lulobank.clients.starter.v3.adapters.in.notification.SendNotificationAdapter;
import com.lulobank.clients.v3.usecase.notification.NotificationDisabledType;
import io.vavr.control.Either;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import static com.lulobank.clients.starter.adapter.Constant.ID_CLIENT;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;


public class SendNotificationAdapterTest extends BaseUnitTest {

    private SendNotificationAdapter testedClass;

    @Before
    public void init(){
        testedClass = new SendNotificationAdapter(notificationDisabledPort);
    }

    @Test
    public void shouldReturnOk(){
        when(notificationDisabledPort.execute(notificationDisabledArgumentCaptor.capture()))
                .thenReturn(Either.right(null));
        ResponseEntity<GenericResponse> responseEntity =
                testedClass.sendMessageNotificationDisabled(ID_CLIENT, Sample.getNotificationDisabledRequest(),
                        bindingResult);
        assertEquals(ID_CLIENT, notificationDisabledArgumentCaptor.getValue().getIdClient());
        assertEquals(NotificationDisabledType.PUSH_NOTIFICATIONS_DISABLED,
                notificationDisabledArgumentCaptor.getValue().getNotificationType());
        assertEquals(OK, responseEntity.getStatusCode());
    }

    @Test
    public void shouldReturnError(){
        UseCaseResponseError useCaseResponseError = Sample.getInternalServerError();
        when(notificationDisabledPort.execute(notificationDisabledArgumentCaptor.capture()))
                .thenReturn(Either.left(useCaseResponseError));
        ResponseEntity<GenericResponse> responseEntity =
                testedClass.sendMessageNotificationDisabled(ID_CLIENT, Sample.getNotificationDisabledRequest(),
                        bindingResult);
        ErrorResponse response = (ErrorResponse) responseEntity.getBody();
        assertEquals(ID_CLIENT, notificationDisabledArgumentCaptor.getValue().getIdClient());
        assertEquals(NotificationDisabledType.PUSH_NOTIFICATIONS_DISABLED,
                notificationDisabledArgumentCaptor.getValue().getNotificationType());
        assertEquals(INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals(useCaseResponseError.getProviderCode(), response.getFailure());
        assertEquals(useCaseResponseError.getBusinessCode(), response.getCode());
        assertEquals(useCaseResponseError.getDetail(), response.getDetail());
    }

}
