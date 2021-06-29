package com.lulobank.clients.sdk.operations;

import io.vavr.control.Try;

import java.util.Map;

public interface AcceptancesDocumentOperation {
    Try<Boolean> createAcceptancesDocument(Map<String, String> headers, String idClient);
}
