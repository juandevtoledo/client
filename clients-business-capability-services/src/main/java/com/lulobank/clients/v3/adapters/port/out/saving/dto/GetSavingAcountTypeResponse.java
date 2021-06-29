package com.lulobank.clients.v3.adapters.port.out.saving.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetSavingAcountTypeResponse {
    private String idSavingAccount;
    private String state;
    private String type;
}
