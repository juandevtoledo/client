package com.lulobank.clients.starter.v3.adapters.in.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Setter
@Getter
public class UpdateCheckpointRequest {
        @NotEmpty(message = "checkpoint is empty or null")
        private String checkpoint;
}
