package com.lulobank.clients.services.domain.findclientbyidbsc;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class NotifyAutomaticPaymentRequest {
    private final String cbsId;
    private final BigDecimal valuePaid;
    private final String paymentStatus;
}
