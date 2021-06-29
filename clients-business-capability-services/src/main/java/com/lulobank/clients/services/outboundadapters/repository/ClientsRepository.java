package com.lulobank.clients.services.outboundadapters.repository;

import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.outboundadapters.model.IdentityBiometric;
import java.util.Optional;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

@EnableScan
public interface ClientsRepository extends CrudRepository<ClientEntity, String> {

  ClientEntity findByEmailAddress(String emailAddress);

  Optional<ClientEntity> findByPhonePrefixAndPhoneNumber(Integer phonePrefix, String phoneNumber);

  ClientEntity findByIdCard(String idCard);


  Optional<ClientEntity> findByIdClient(String idClient);

  Optional<ClientEntity> findByIdClientAndIdCard(String idClient, String idCard);

  Optional<ClientEntity> findByIdClientAndEmailAddress(String idClient, String emailAddress);

  Optional<ClientEntity> findByIdClientAndEmailAddressAndQualityCode(
      String idClient, String emailAddress, String qualityCode);

  Optional<ClientEntity> findByIdCardAndEmailAddress(String idCard, String email);

  Optional<ClientEntity> findByIdClientAndOnBoardingStatusNotNull(String idClient);

  Optional<ClientEntity> findByIdentityBiometric(IdentityBiometric identityBiometric);

  Optional<ClientEntity> findByIdClientAndIdentityBiometric(
      String idClient, IdentityBiometric identityBiometric);
}
