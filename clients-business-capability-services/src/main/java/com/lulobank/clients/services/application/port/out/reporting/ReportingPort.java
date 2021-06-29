package com.lulobank.clients.services.application.port.out.reporting;

import com.lulobank.clients.services.application.port.out.reporting.model.StoreDigitalEvidenceRequest;
import io.vavr.control.Try;

import java.util.Map;

public interface ReportingPort {

    Try<Boolean> createDigitalEvidence(Map<String, String> headers, String idClient, StoreDigitalEvidenceRequest storeDigitalEvidenceRequest);

}
