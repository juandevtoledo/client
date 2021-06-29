package com.lulobank.clients.starter.adapter.in.zendesk;

import com.lulobank.clients.services.application.port.in.ZendeskClientInfoPort;
import com.lulobank.clients.services.domain.zendeskclientinfo.GetClientInfoByEmailRequest;
import com.lulobank.clients.starter.adapter.in.dto.GenericResponse;
import com.lulobank.clients.starter.adapter.in.util.AdapterResponseUtil;
import com.lulobank.clients.starter.adapter.in.zendesk.mapper.ZendeskClientInfoAdapterMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Email;

import static com.lulobank.clients.starter.adapter.in.mapper.InboundAdapterErrorMapper.getHttpStatusFromBusinessCode;
import static com.lulobank.clients.starter.adapter.in.util.AdapterResponseUtil.error;

@RestController
@RequestMapping("/")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ZendeskClientInfoAdapter {

    private final ZendeskClientInfoPort zendeskClientInfoPort;

    @GetMapping(value = "/info", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GenericResponse> getClientInfoZendesk(
            @RequestHeader final HttpHeaders httpHeaders,
            @Email @RequestParam final String email) {

        return processGetClientInfoRequest(httpHeaders, email);
    }

    private ResponseEntity<GenericResponse> processGetClientInfoRequest(HttpHeaders httpHeaders, String email) {
        GetClientInfoByEmailRequest request = new GetClientInfoByEmailRequest(email);
        request.setHttpHeaders(httpHeaders.toSingleValueMap());
        return zendeskClientInfoPort.execute(request)
                .map(ZendeskClientInfoAdapterMapper.INSTANCE::toGetClientInfoByEmailAdapterResponse)
                .map(AdapterResponseUtil::ok)
                .getOrElseGet(error -> error(ZendeskClientInfoAdapterMapper.INSTANCE.toErrorResponse(error),
                        getHttpStatusFromBusinessCode(error.getBusinessCode())));
    }
}