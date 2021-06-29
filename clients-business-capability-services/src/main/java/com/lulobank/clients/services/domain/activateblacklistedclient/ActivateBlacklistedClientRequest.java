package com.lulobank.clients.services.domain.activateblacklistedclient;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ActivateBlacklistedClientRequest {
    private String idTransactionBiometric;
    private ClientPersonalInformation clientPersonalInformation;
    private Blacklist blacklist;
    private LocalDateTime whitelistExpirationDate;

}
