package com.lulobank.clients.starter.adapter.out.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;

import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WebClientUtil {
    public static HttpHeaders mapToHttpHeaders(Map<String, String> headers, HttpHeaders httpHeaders) {
        headers.entrySet().stream()
            .forEach(entry -> httpHeaders.set(entry.getKey(), entry.getValue()));
        return httpHeaders;
    }
}
