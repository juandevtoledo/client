package com.lulobank.clients.services.domain.zendeskclientinfo;

import com.lulobank.clients.sdk.operations.dto.AbstractCommandFeatures;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetClientInfoByEmailRequest extends AbstractCommandFeatures {
    private String email;
}
