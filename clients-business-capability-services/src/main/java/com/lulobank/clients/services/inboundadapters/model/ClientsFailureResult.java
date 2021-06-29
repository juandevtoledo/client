package com.lulobank.clients.services.inboundadapters.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import static com.lulobank.clients.services.utils.ClientsErrorResponse.UNPROCESSABLE_REQUEST;

@Getter
@Setter
@Accessors(chain = true)
public class ClientsFailureResult extends ClientsResult {

    private static final String GENERIC_DETAIL = "An internal process has failed, try again later";

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String code = UNPROCESSABLE_REQUEST.code();
    private String failure = UNPROCESSABLE_REQUEST.name();
    private String detail = GENERIC_DETAIL;

}
