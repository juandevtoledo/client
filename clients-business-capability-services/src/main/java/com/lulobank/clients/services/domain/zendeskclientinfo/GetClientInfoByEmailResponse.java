package com.lulobank.clients.services.domain.zendeskclientinfo;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class GetClientInfoByEmailResponse {
    private String idClient;
    private Customer customer;
    private List<Product> products;

    public GetClientInfoByEmailResponse() {
        products = new ArrayList<>();
    }
}
