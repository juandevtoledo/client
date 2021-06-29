package com.lulobank.clients.v3.adapters.port.out.digitalevidence.dto;

import com.lulobank.clients.v3.util.DigitalEvidenceTypes;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class DigitalEvidenceRequest {
    private String idCard;
    private String name;
    private String lastName;
    private String emailAddress;
    private LocalDateTime acceptanceTimestamp;
    private DigitalEvidenceTypes evidenceType;
}
