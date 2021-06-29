package com.lulobank.clients.starter.v3.adapters.out.sqs.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;


@Getter
@Builder
public class EmailNotificationMessage {
    private String clientId;
    private String notificationType;
    private String to;
    private Map<String, Object> attributes;
}
