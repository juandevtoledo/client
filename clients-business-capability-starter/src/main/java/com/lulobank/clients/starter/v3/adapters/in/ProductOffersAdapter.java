package com.lulobank.clients.starter.v3.adapters.in;

import com.lulobank.clients.services.domain.productoffers.UpdateProductOffersRequest;
import com.lulobank.clients.starter.adapter.in.dto.ErrorResponse;
import com.lulobank.clients.starter.adapter.in.dto.GenericResponse;
import com.lulobank.clients.starter.v3.handler.ProductOfferHandler;
import com.lulobank.clients.starter.v3.handler.ProductOffersHandler;
import io.vavr.collection.Iterator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.lulobank.clients.v3.error.ClientsDataErrorStatus.CLI_100;
import static com.lulobank.clients.v3.error.ClientsDataErrorStatus.CLI_102;
import static com.lulobank.clients.v3.error.ClientsDataErrorStatus.DEFAULT_DETAIL;
import static com.lulobank.clients.v3.error.ClientsDataErrorStatus.VALIDATION_DETAIL;

@Slf4j
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/client/{idClient}/product-offers")
@RequiredArgsConstructor
public class ProductOffersAdapter {

    private final ProductOffersHandler productOffersHandler;
    private final ProductOfferHandler productOfferHandler;

    @GetMapping
    public ResponseEntity<GenericResponse> getApprovedOffers(@RequestHeader final HttpHeaders headers,
                                                             @PathVariable("idClient") String idClient) {
        return productOfferHandler.getClientProductOffers(headers, idClient);
    }

    @PutMapping
    public ResponseEntity<GenericResponse> updateOffer(@RequestHeader final HttpHeaders headers,
                                                       @PathVariable("idClient") String idClient,
                                                       @Valid @RequestBody final UpdateProductOffersRequest request) {
        return productOffersHandler.updateClientProductOffers(headers, idClient, request);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleValidationExceptions(MethodArgumentNotValidException ex) {
        return Iterator.ofAll(ex.getBindingResult().getAllErrors())
                .peek(error -> log.error("Validation error: {}", error.getDefaultMessage()))
                .map(error -> new ErrorResponse("400", CLI_102.name(), VALIDATION_DETAIL))
                .getOrElse(new ErrorResponse("500", CLI_100.name(), DEFAULT_DETAIL));
    }
}
