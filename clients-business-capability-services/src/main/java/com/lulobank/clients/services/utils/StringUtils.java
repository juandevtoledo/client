package com.lulobank.clients.services.utils;

import com.lulobank.clients.services.events.ClientPersonalInformationResult;
import com.lulobank.clients.services.events.ClientVerificationResult;
import io.vavr.control.Option;

import static org.apache.commons.lang3.StringUtils.EMPTY;

public class StringUtils {


    public static String concatNames(ClientVerificationResult clientVerificationResult) {
        return Option.of(clientVerificationResult)
                .map(ClientVerificationResult::getClientPersonalInformation)
                .map(ClientPersonalInformationResult::getAdditionalPersonalInformation)
                .map(additionalPersonalInformation -> additionalPersonalInformation.getFirstName() +
                        Option.of(additionalPersonalInformation.getSecondName())
                                .map(s -> " " + s)
                                .getOrElse(""))
                .getOrElse("");
    }

    public static String concatLastNames(ClientVerificationResult clientVerificationResult) {
        return Option.of(clientVerificationResult)
                .map(ClientVerificationResult::getClientPersonalInformation)
                .map(ClientPersonalInformationResult::getAdditionalPersonalInformation)
                .map(additionalPersonalInformation -> additionalPersonalInformation.getFirstSurname() +
                        Option.of(additionalPersonalInformation.getSecondSurname())
                                .map(s -> " " + s)
                                .getOrElse(""))
                .getOrElse("");
    }

    public static String getStringWithOutSpaces(String string) {
        return Option.of(string)
                .map(String::trim)
                .filter(value -> !EMPTY.equals(value))
                .getOrNull();
    }

}
