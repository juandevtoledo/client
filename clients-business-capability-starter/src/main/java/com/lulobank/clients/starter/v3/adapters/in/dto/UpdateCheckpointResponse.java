package com.lulobank.clients.starter.v3.adapters.in.dto;

import com.lulobank.clients.sdk.operations.util.CheckPoints;
import com.lulobank.clients.starter.adapter.in.dto.GenericResponse;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdateCheckpointResponse extends GenericResponse {
    private CheckPoints checkpoint;
}

