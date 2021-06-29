package com.lulobank.clients.v3.usecase.command;

import com.lulobank.clients.v3.vo.AdapterCredentials;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ValidateEmailIsUnique {

    private final String email;
    private final AdapterCredentials credentials;

}
