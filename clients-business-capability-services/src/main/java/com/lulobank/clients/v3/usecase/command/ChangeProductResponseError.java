package com.lulobank.clients.v3.usecase.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ChangeProductResponseError {
    private String code;
    private String message;
}
