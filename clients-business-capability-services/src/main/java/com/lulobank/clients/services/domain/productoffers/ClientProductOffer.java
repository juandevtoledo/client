package com.lulobank.clients.services.domain.productoffers;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ClientProductOffer {

    private final List<ProductOffer> offers;
}
