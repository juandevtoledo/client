package com.lulobank.clients.services.ports.repository;

import com.lulobank.clients.services.ports.out.dto.TimestampDescriptor;
import io.vavr.control.Option;
import io.vavr.control.Try;

public interface TimestampRepository {

    void saveTimestamp(String emailAddress, String timestamp);

    Option<TimestampDescriptor> getTimestamp(String emailAddress);

    Try<Boolean> deleteTimestamp(String emailAddress);
}
