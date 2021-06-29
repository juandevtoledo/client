package com.lulobank.clients.v3.adapters.port.out.digitalevidence;

import com.lulobank.clients.services.domain.error.UseCaseResponseError;
import com.lulobank.clients.v3.adapters.port.out.digitalevidence.dto.AcceptancesDocumentRequest;
import com.lulobank.clients.v3.adapters.port.out.digitalevidence.dto.AppAcceptanceDocuments;
import com.lulobank.clients.v3.adapters.port.out.digitalevidence.dto.DigitalEvidenceDocuments;
import com.lulobank.clients.v3.adapters.port.out.digitalevidence.dto.DigitalEvidenceRequest;
import io.vavr.control.Either;

import java.util.Map;

public interface DigitalEvidenceServicePort {

    Either<UseCaseResponseError, DigitalEvidenceDocuments> saveDigitalEvidence(Map<String,String> headers,
                                                                               String idClient,
                                                                               DigitalEvidenceRequest digitalEvidenceRequest);
    Either<UseCaseResponseError, AppAcceptanceDocuments> createAcceptancesDocument(Map<String, String> headers,
                                                                                   String idClient,
                                                                                   AcceptancesDocumentRequest acceptancesDocumentRequest);
}
