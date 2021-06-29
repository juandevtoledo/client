package com.lulobank.clients.starter.v3.adapters.in.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeProductResponse  extends GenericResponse {

    public ChangeProductResponse(ErrorResult error) {
        super(error);
    }
}
