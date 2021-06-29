package com.lulobank.clients.services.outboundadapters.repository;

import com.lulobank.clients.services.outboundadapters.model.LoginAttemptsEntity;
import java.util.Optional;
import java.util.UUID;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

@EnableScan
public interface LoginAttemptsRepository extends CrudRepository<LoginAttemptsEntity, UUID> {

  Optional<LoginAttemptsEntity> findByIdClient(UUID idClient);
}
