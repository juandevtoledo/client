package com.lulobank.clients.v3.util;

public enum DocumentTypes {
    CC("C\u00e9dula de Ciudadan\u00eda");

    DocumentTypes(String text) {
        this.text = text;
    }

    private final String text;

    public String getText() {
        return text;
    }

}
