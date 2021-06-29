package com.lulobank.clients.services.ports.out.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CustomerServiceResponse {
    private Boolean created;
}
