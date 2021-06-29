package com.lulobank.clients.services.application.port.out.reporting.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class StoreDigitalEvidenceRequest {
    private String idCard;
    private String name;
    private String lastName;
    private String emailAddress;
    private LocalDateTime acceptanceTimestamp;
    private List<EvidenceDocument> documentsToStore;
}
