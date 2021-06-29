package com.lulobank.clients.v3.usecase.pep;

public enum PepStatus {
    EMPTY_PEP("-1"),
    NO_PEP("0"),
    PEP_WAIT_LIST("1"),
    PEP_WHITELISTED("3"),
    PEP_BLACKLISTED("4");

    private String value;

    PepStatus(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
