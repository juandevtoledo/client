package com.lulobank.clients.v3.usecase.command;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateEmailAddress {
    private final String idClient;
    private final String newEmail;
}
