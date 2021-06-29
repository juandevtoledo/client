package com.lulobank.clients.v3.events;

import com.lulobank.clients.services.events.EconomicInformationEvent;
import com.lulobank.clients.services.events.IdentityInformation;
import io.vavr.control.Try;

import java.util.Map;

public interface RiskEngineNotificationService {

    Try<Void> setEconomicInformation(EconomicInformationEvent economicInformationEvent, Map<String, Object> header, String idClient);

    Try<Void> setIdentityInformation(IdentityInformation identityInformation, Map<String, Object> header, String idClient);

}
