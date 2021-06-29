package com.lulobank.clients.starter.adapter.in.util;

import io.vavr.collection.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static com.lulobank.clients.services.application.port.out.savingsaccounts.error.SavingsAccountsErrorStatus.CLI_110;
import static com.lulobank.clients.services.application.port.out.savingsaccounts.error.SavingsAccountsErrorStatus.CLI_111;
import static com.lulobank.clients.v3.error.ClientsDataErrorStatus.CLI_100;
import static com.lulobank.clients.v3.error.ClientsDataErrorStatus.CLI_101;
import static com.lulobank.clients.v3.error.ClientsDataErrorStatus.CLI_102;
import static com.lulobank.clients.v3.error.ClientsDataErrorStatus.CLI_103;
import static com.lulobank.clients.v3.error.ClientsDataErrorStatus.CLI_104;
import static com.lulobank.clients.v3.error.ClientsDataErrorStatus.CLI_105;
import static com.lulobank.clients.v3.error.ClientsDataErrorStatus.CLI_106;
import static com.lulobank.clients.v3.error.ClientsDataErrorStatus.CLI_109;
import static com.lulobank.clients.v3.error.GeneralErrorStatus.GEN_001;
import static com.lulobank.clients.v3.error.UseCaseErrorStatus.CLI_180;


@Getter
@AllArgsConstructor
public enum InboundAdapterErrorCode {
    BAD_REQUEST(List.of(GEN_001.name(), CLI_102.name()), HttpStatus.BAD_REQUEST),

    NOT_FOUND(List.of(CLI_101.name(), CLI_111.name(), CLI_103.name()), HttpStatus.NOT_FOUND),

    INTERNAL_SERVER_ERROR(List.of(CLI_180.name(), CLI_109.name()), HttpStatus.INTERNAL_SERVER_ERROR),

    BAD_GATEWAY(List.of(CLI_100.name(), CLI_110.name()), HttpStatus.BAD_GATEWAY),

    PRECONDITION_FAILED(List.of(CLI_104.name()), HttpStatus.PRECONDITION_FAILED),

    CONFLICT(List.of(CLI_105.name(), CLI_106.name()), HttpStatus.CONFLICT),

    ;

    private final List<String> businessCodes;
    private final HttpStatus httpStatus;
}
