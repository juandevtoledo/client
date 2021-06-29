package com.lulobank.clients.starter.adapter.in.util;

import com.lulobank.clients.starter.adapter.in.dto.ErrorResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindingResult;

import static com.lulobank.clients.v3.error.GeneralErrorStatus.DEFAULT_DETAIL;
import static com.lulobank.clients.v3.error.GeneralErrorStatus.GEN_001;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ValidationUtil {

    public static ErrorResponse getResponseBindingResult(BindingResult bindingResult) {
        String error = bindingResult
            .getAllErrors()
            .stream()
            .findFirst()
            .map(DefaultMessageSourceResolvable::getDefaultMessage)
            .orElse(StringUtils.EMPTY);
        return new ErrorResponse(error, GEN_001.name(), DEFAULT_DETAIL);
    }
}
