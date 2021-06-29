package com.lulobank.clients.v3.adapters.port.out.dynamo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AttachmentV3 {
    private String key;
    private String link;
}
