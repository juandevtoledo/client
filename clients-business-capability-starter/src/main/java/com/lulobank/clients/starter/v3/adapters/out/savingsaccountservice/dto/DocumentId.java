package com.lulobank.clients.starter.v3.adapters.out.savingsaccountservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentId {
    private String id;
    private String type;
    private String issueDate;
    private String expirationDate;
}