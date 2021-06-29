package com.lulobank.clients.v3.service.fatca;

import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import com.lulobank.clients.v3.usecase.command.ClientFatcaInformation;
import com.lulobank.clients.v3.usecase.mapper.FatcaInformationMapper;

public class FatcaInformationService {

    public static ClientsV3Entity buildFatcaInformation(ClientsV3Entity entity, ClientFatcaInformation command){
        entity.setFatcaInformation(FatcaInformationMapper.INSTANCE.fromRequest(command));
        return entity;
    }
}
