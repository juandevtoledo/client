package com.lulobank.clients.starter.outboundadapter.repository;

import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.outboundadapters.repository.ClientsRepository;
import com.lulobank.clients.services.ports.repository.ClientsRepositoryV2;
import io.vavr.control.Option;

public class ClientsRepositoryAdapter implements ClientsRepositoryV2 {

    private ClientsRepository clientsRepository;

    public ClientsRepositoryAdapter(ClientsRepository clientsRepository){
        this.clientsRepository = clientsRepository;

    }

    @Override
    public void save(ClientEntity clientEntity) {
        clientsRepository.save(clientEntity);
    }

    @Override
    public Option<ClientEntity> findByPhonePrefixAndPhoneNumber(Integer phonePrefix, String phoneNumber) {
        return Option.ofOptional(clientsRepository.findByPhonePrefixAndPhoneNumber(phonePrefix,phoneNumber));
    }

    @Override
    public ClientEntity findByIdCard(String idCard) {
        return clientsRepository.findByIdCard(idCard);
    }

    @Override
    public Option<ClientEntity> findByEmailAddress(String emailAddress) {
        return Option.of(clientsRepository.findByEmailAddress(emailAddress));
    }

    @Override
    public Option<ClientEntity> findByIdClient(String idClient) {
        return Option.ofOptional(clientsRepository.findByIdClient(idClient));
    }

    @Override
    public Option<ClientEntity> findByIdClientAndIdCard(String idClient, String idCard) {
        return Option.ofOptional(clientsRepository.findByIdClientAndIdCard(idClient,idCard));
    }

    @Override
    public Option<ClientEntity> findByIdClientAndEmailAddress(String idClient, String emailAddress) {
        return Option.ofOptional(clientsRepository.findByIdClientAndEmailAddress(idClient,emailAddress));
    }

    @Override
    public Option<ClientEntity> findByIdClientAndEmailAddressAndQualityCode(String idClient, String emailAddress, String qualityCode) {
        return Option.ofOptional(clientsRepository.findByIdClientAndEmailAddressAndQualityCode(idClient,emailAddress,qualityCode));
    }

    @Override
    public Option<ClientEntity> findByIdCardAndEmailAddress(String idCard, String email) {
        return Option.ofOptional(clientsRepository.findByIdClientAndEmailAddress(idCard,email));
    }

    @Override
    public Option<ClientEntity> findByIdClientAndOnBoardingStatusNotNull(String idClient) {
        return Option.ofOptional(clientsRepository.findByIdClientAndOnBoardingStatusNotNull(idClient));
    }
}
