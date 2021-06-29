package com.lulobank.clients.starter.config;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Sample {

    protected static List<String> fieldsCompletes() {
        return Stream.of("/password,/newPin,/content/currentCard/pin".split(","))
                .collect(Collectors.toList());
    }

    protected static List<String> fieldsPartially() {
        return Stream.of("/cardNumber".split(","))
                .collect(Collectors.toList());
    }
}
