package com.lulobank.clients.starter.v3.adapters.in.notification;

import com.lulobank.clients.services.domain.notification.NotificationDisabledRequest;
import com.lulobank.clients.starter.adapter.in.dto.GenericResponse;
import com.lulobank.clients.starter.adapter.in.mapper.NotificationDisabledAdapterMapper;
import com.lulobank.clients.starter.adapter.in.util.AdapterResponseUtil;
import com.lulobank.clients.starter.adapter.in.util.ValidationUtil;
import com.lulobank.clients.starter.v3.adapters.in.notification.dto.NotificationDisabledAdapterRequest;
import com.lulobank.clients.v3.adapters.port.in.notification.NotificationDisabledPort;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.lulobank.clients.starter.adapter.in.util.AdapterResponseUtil.error;
import static com.lulobank.clients.starter.adapter.in.mapper.InboundAdapterErrorMapper.getHttpStatusFromBusinessCode;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class SendNotificationAdapter {

    private final NotificationDisabledPort notificationDisabledPort;

    @PostMapping(value = "/notifications/{idClient}")
    public ResponseEntity<GenericResponse> sendMessageNotificationDisabled(@PathVariable("idClient")
                                                                             final String idClient,
                                                                           @Valid @RequestBody NotificationDisabledAdapterRequest
                                                                                 request,
                                                                           BindingResult bindingResult){
        return Option.of(bindingResult)
                .filter(BindingResult::hasErrors)
                .map(ValidationUtil::getResponseBindingResult)
                .map(AdapterResponseUtil::badRequest)
                .getOrElse(executeNotificationDisabled(request, idClient));
    }

    private ResponseEntity<GenericResponse> executeNotificationDisabled(NotificationDisabledAdapterRequest request,
                                                                        String idClient){
        NotificationDisabledRequest notificationDisabledRequest =
                NotificationDisabledAdapterMapper.INSTANCE.toNotificationDisabledRequest(request);
        notificationDisabledRequest.setIdClient(idClient);
        return notificationDisabledPort.execute(notificationDisabledRequest)
                .map(response->AdapterResponseUtil.ok())
                .getOrElseGet(error->error(NotificationDisabledAdapterMapper.INSTANCE.toErrorResponse(error),
                        getHttpStatusFromBusinessCode(error.getBusinessCode())));
    }

}
