package com.lulobank.clients.starter.v3.adapters.in.sqs.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ActivateBlacklistedClient {
    private String idTransactionBiometric;
    private ClientPersonalInformation clientPersonalInformation;
    private Blacklist blacklist;
    private String whitelistExpirationDate;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ClientPersonalInformation {
        private Document idDocument;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Blacklist {
        private String status;
        private String reportDate;
        private String riskLevel;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Document {
        private String documentType;
        private String idCard;
        private String expeditionDate;
    }
}
