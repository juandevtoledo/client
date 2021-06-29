package com.lulobank.clients.services.usecase.command;

import com.lulobank.clients.sdk.operations.dto.AbstractCommandFeatures;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class CreateCustomer extends AbstractCommandFeatures {
    private String idClient ;
}
