package com.lulobank.clients.starter.v3.adapters.in.sqs.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivateSelfCertifiedPEPClient {
    private boolean whitelisted;
    private ClientPersonalInformation clientPersonalInformation;
    private String whitelistExpirationDate;

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ClientPersonalInformation {
        @JsonProperty("idDocument")
        private Document document;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Document {
        private String documentType;
        @JsonProperty("idCard")
        private String cardId;
    }

}
