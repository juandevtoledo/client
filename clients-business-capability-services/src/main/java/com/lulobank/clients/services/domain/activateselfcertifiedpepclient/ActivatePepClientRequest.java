package com.lulobank.clients.services.domain.activateselfcertifiedpepclient;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ActivatePepClientRequest {

    private boolean whitelisted;
    private ClientPersonalInformation clientPersonalInformation;
    private LocalDateTime whitelistExpirationDate;

}
