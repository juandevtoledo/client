package com.lulobank.clients.v3.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @apiNote This enum must handle business codes from CLI_180 to CLI_199
 */
@Getter
@RequiredArgsConstructor
public enum UseCaseErrorStatus {
    CLI_180("Error default"),
    ;

    private final String message;

    public static final String DEFAULT_DETAIL = "U";
}
