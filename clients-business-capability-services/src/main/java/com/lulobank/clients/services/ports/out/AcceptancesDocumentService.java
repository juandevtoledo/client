package com.lulobank.clients.services.ports.out;

import com.lulobank.reporting.sdk.operations.dto.AcceptancesDocument;
import io.vavr.control.Try;

import java.util.Map;

public interface AcceptancesDocumentService {

    Try<Boolean> generateAcceptancesDocument(Map<String, String> headers, String idClient,
                                             AcceptancesDocument acceptancesDocument);
}
