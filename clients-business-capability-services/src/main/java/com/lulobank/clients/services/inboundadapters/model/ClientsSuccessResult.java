package com.lulobank.clients.services.inboundadapters.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ClientsSuccessResult<T> extends ClientsResult{

    private final T content;

}
