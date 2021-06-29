package com.lulobank.clients.starter.v3.adapters.in.dto;

import com.lulobank.clients.starter.adapter.in.dto.GenericResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TransactionBiometricResponse extends GenericResponse {
    private String idTransactionBiometric;
}
