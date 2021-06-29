package com.lulobank.clients.v3.adapters.port.out.dynamo.dto;

import java.util.EnumSet;
import java.util.Set;

public enum OfferState {
    ACTIVE,
    ACTIVE_HOME,
    CLOSED,
    EXPIRED;

    public static final Set<OfferState> ACTIVE_STATES = EnumSet.of(ACTIVE, ACTIVE_HOME);
}
