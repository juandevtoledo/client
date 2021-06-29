package com.lulobank.clients.starter.v3.adapters.out.dynamo;

import com.lulobank.clients.starter.v3.adapters.out.dynamo.dto.ClientEntity;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

@EnableScan
public interface ClientsDataRepository extends CrudRepository<ClientEntity, String> {
    Optional<ClientEntity> findByIdClient(String idClient);
    Optional<ClientEntity> findByIdCard(String idCard);
    Optional<ClientEntity> findByEmailAddress(String emailAddress);
}
