package com.lulobank.clients.v3.usecase.command;

import com.lulobank.clients.sdk.operations.util.CheckPoints;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateCheckpointInfo {
    private String clientId;
    private CheckPoints checkpoint;
}
