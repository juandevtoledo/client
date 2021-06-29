package com.lulobank.clients.services.domain.activateblacklistedclient;

import com.lulobank.clients.services.domain.StateBlackList;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class Blacklist {
    private StateBlackList status;
    private LocalDateTime reportDate;
    private String riskLevel;
}
