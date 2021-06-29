package com.lulobank.clients.starter.adapter.in.mapper;

import com.lulobank.clients.starter.adapter.in.util.InboundAdapterErrorCode;
import io.vavr.collection.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import static com.lulobank.clients.starter.adapter.in.util.InboundAdapterErrorCode.INTERNAL_SERVER_ERROR;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InboundAdapterErrorMapper {
    public static HttpStatus getHttpStatusFromBusinessCode(String businessCode) {
        return List.of(InboundAdapterErrorCode.values())
            .filter(inboundAdapterErrorCode -> inboundAdapterErrorCode.getBusinessCodes().contains(businessCode))
            .map(InboundAdapterErrorCode::getHttpStatus)
            .getOrElse(INTERNAL_SERVER_ERROR.getHttpStatus());
    }
}
