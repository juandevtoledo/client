package com.lulobank.clients.sdk.operations;

import io.vavr.control.Try;

import java.util.Map;

public interface DigitalEvidenceOperation {
    Try<Boolean> createDigitalEvidence(Map<String, String> headers, String idClient);
}
