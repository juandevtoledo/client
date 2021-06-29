package com.lulobank.clients.starter.exception;

import com.lulobank.clients.sdk.operations.dto.ClientResult;

public class ClientErrorV2  implements ClientResult {

    private String errors;

    public ClientErrorV2(String errors) {
        this.errors = errors;
    }

    public String getErrors() {
        return errors;
    }

    public void setErrors(String errors) {
        this.errors = errors;
    }
}
