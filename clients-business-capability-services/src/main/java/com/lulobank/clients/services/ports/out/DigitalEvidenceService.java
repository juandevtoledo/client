package com.lulobank.clients.services.ports.out;

import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.clients.v3.util.DigitalEvidenceTypes;
import io.vavr.control.Try;

import java.util.Map;

public interface DigitalEvidenceService {

  Try<Boolean> saveDigitalEvidence(Map<String, String> headers, ClientsV3Entity client, DigitalEvidenceTypes digitalEvidenceTypes);

}
