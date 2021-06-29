package com.lulobank.clients.starter.inboundadapter;

import com.lulobank.clients.services.exception.CreateCustomerException;
import com.lulobank.clients.services.inboundadapters.model.ClientsFailureResult;
import com.lulobank.clients.services.inboundadapters.model.ClientsResult;
import com.lulobank.clients.services.usecase.CreateCustomerUseCase;
import com.lulobank.clients.services.usecase.command.CreateCustomer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.lulobank.clients.services.utils.ClientsErrorResponse.CUSTOMER_SERVICE_ERROR;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/")
@Slf4j
public class CreateCustomerAdapter {

    private static final String ERROR_UNKNOWN = "An error occur while creating customer dara. Cause: {}";
    private static final String ERROR_CREATING_CUSTOMER_DATA = "Error creating customer data. Cause: {}";
    private static final String CUSTOMER_SERVICE_FAILURE = "500";

    private final CreateCustomerUseCase createCustomerUseCase;

    public CreateCustomerAdapter(CreateCustomerUseCase createCustomerUseCase) {
        this.createCustomerUseCase = createCustomerUseCase;
    }

    @PutMapping("{idClient}/create-customer")
    public ResponseEntity<ClientsResult> creatCustomer(@RequestHeader final HttpHeaders headers,
                                                                @PathVariable("idClient") final String idClient){
        CreateCustomer createCustomer = new CreateCustomer();
        createCustomer.setIdClient(idClient);
        createCustomer.setHttpHeaders(headers.toSingleValueMap());

        return createCustomerUseCase.execute(createCustomer)
                .map(response -> new ResponseEntity<ClientsResult>(HttpStatus.OK))
                .recover(CreateCustomerException.class, this::createCustomerError)
                .onFailure(e -> log.error(ERROR_UNKNOWN, e.getMessage(), e))
                .recover(e -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ClientsFailureResult()))
                .get();
    }

    private ResponseEntity<ClientsResult> createCustomerError(CreateCustomerException e) {
        log.error(ERROR_CREATING_CUSTOMER_DATA, e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ClientsFailureResult()
                        .setCode(CUSTOMER_SERVICE_ERROR.code())
                        .setFailure(CUSTOMER_SERVICE_FAILURE)
                        .setDetail("U")
                );
    }
}