package com.lulobank.clients.services.domain.productoffers;

import com.lulobank.clients.v3.vo.AdapterCredentials;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProductOffersRequest {
    @NotNull(message = "idProductOffer is null or empty")
    private String idProductOffer;
    @NotNull(message = "status is null or empty")
    private OfferStatus status;
    private String idClient;
    private AdapterCredentials adapterCredentials;
}
