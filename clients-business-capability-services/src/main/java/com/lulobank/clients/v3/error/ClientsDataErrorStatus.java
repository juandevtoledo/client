package com.lulobank.clients.v3.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @apiNote This enum must handle business codes from CLI_100 to CLI_109
 */
@Getter
@RequiredArgsConstructor
public enum ClientsDataErrorStatus {
    CLI_100("Connection error on clients data repository"),
    CLI_101("Client not found in clients data repository"),
    CLI_102("Validation error"),
    CLI_103("Fatca information not found in clients data repository"),
    CLI_104("Error validating unique email"),
    CLI_105("Error validating unique email in customer service"),
    CLI_106("The client already has an address created"),
    CLI_107("Error consuming digital evidence service"),
    CLI_108("Error validating unique phone"),
    CLI_109("Internal Server Error");

    private final String message;

    public static final String DEFAULT_DETAIL = "D";
    public static final String VALIDATION_DETAIL = "V";
}
