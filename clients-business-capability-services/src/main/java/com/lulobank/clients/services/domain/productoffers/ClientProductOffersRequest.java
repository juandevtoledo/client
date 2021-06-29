package com.lulobank.clients.services.domain.productoffers;

import com.lulobank.clients.v3.vo.AdapterCredentials;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ClientProductOffersRequest {
    private final String idClient;
    private final AdapterCredentials adapterCredentials;
}
