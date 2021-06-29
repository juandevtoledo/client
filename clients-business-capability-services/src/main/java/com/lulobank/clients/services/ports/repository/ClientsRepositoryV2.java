package com.lulobank.clients.services.ports.repository;

import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import io.vavr.control.Option;

public interface ClientsRepositoryV2 {

    void save(ClientEntity clientEntity);

    Option<ClientEntity> findByPhonePrefixAndPhoneNumber(Integer phonePrefix, String phoneNumber);

    ClientEntity findByIdCard(String idCard);

    Option<ClientEntity> findByEmailAddress(String emailAddress);

    Option<ClientEntity> findByIdClient(String idClient);

    Option<ClientEntity> findByIdClientAndIdCard(String idClient, String idCard);

    Option<ClientEntity> findByIdClientAndEmailAddress(String idClient, String emailAddress);

    Option<ClientEntity> findByIdClientAndEmailAddressAndQualityCode(
        String idClient, String emailAddress, String qualityCode);

    Option<ClientEntity> findByIdCardAndEmailAddress(String idCard, String email);

    Option<ClientEntity> findByIdClientAndOnBoardingStatusNotNull(String idClient);

}
