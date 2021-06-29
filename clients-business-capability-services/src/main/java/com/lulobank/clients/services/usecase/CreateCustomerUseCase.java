package com.lulobank.clients.services.usecase;

import com.lulobank.clients.services.application.port.in.UseCase;
import com.lulobank.clients.services.ports.out.CustomerService;
import com.lulobank.clients.services.usecase.command.CreateCustomer;
import com.lulobank.clients.v3.adapters.port.out.dynamo.ClientsV3Repository;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import io.vavr.control.Try;

import java.util.Map;

public class CreateCustomerUseCase implements UseCase<CreateCustomer, Try<Boolean>> {

    private final CustomerService customerService;
    private final ClientsV3Repository clientsRepository;

    public CreateCustomerUseCase(CustomerService customerService, ClientsV3Repository clientsRepository) {
        this.customerService = customerService;
        this.clientsRepository = clientsRepository;
    }

    @Override
    public Try<Boolean> execute(CreateCustomer command) {
        return clientsRepository.findByIdClient(command.getIdClient()).toTry()
                .flatMap(client -> createCustomer(command.getAuthorizationHeader(), client));
    }

    private Try<Boolean> createCustomer(Map<String, String> headers, ClientsV3Entity clientEntity){
        return customerService.createUserCustomer(headers, clientEntity)
                .peek(response-> clientEntity.setCustomerCreatedStatus(true))
                .peek(response-> clientsRepository.save(clientEntity));
    }
}
