package com.lulobank.clients.services.events;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

import static org.apache.commons.lang3.StringUtils.SPACE;

@Getter
@Builder
public class LoanAutomaticDebitMessage {
    private final String idClient;
    private final BigDecimal valuePaid;
    private final String paymentStatus;
    private final String creditId;
    private final String email;
    private final String fullName;

    public static class LoanAutomaticDebitMessageBuilder {
        public LoanAutomaticDebitMessageBuilder fullName(String name, String lastName) {
            this.fullName = name.concat(SPACE).concat(lastName);
            return this;
        }
    }
}
