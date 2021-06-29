package com.lulobank.clients.services.application.port.in;

@FunctionalInterface
public interface UseCase<T, R> {
    R execute(T command);
}