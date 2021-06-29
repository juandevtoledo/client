package com.lulobank.clients.v3.adapters.port.out.dynamo;

import com.lulobank.clients.services.domain.error.UseCaseResponseError;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.IdentityBiometricV3;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;

public interface ClientsV3Repository {

    Option<ClientsV3Entity> findByIdClient(String idClient);

    Option<ClientsV3Entity> findByIdentityBiometric(IdentityBiometricV3 identityBiometricV3);
    
    Option<ClientsV3Entity> findByIdCbs(String idCbs);

    Option<ClientsV3Entity> findByIdCard(String idCard);

    Option<ClientsV3Entity> findByEmailAddress(String email);

    Option<ClientsV3Entity> findByPhonePrefixAndPhoneNumber(Integer phonePrefix, String phoneNumber);

    Try<ClientsV3Entity> save(ClientsV3Entity clientsV3Entity);

    void updateOnBoarding(ClientsV3Entity clientsV3Entity);

    Try<Void> updateClientBlacklisted(ClientsV3Entity clientsV3Entity);

    Either<UseCaseResponseError, Boolean> updatePhoneNumber(String idClient, String phoneNumber, Integer prefix);


    Try<Void> updateEmailByIdClient(String idClient, String emailAddress);

}
