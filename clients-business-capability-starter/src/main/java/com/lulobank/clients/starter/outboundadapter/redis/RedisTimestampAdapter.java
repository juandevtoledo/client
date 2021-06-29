package com.lulobank.clients.starter.outboundadapter.redis;

import com.lulobank.clients.services.exception.TimestampDigitalEvidenceException;
import com.lulobank.clients.services.ports.out.dto.TimestampDescriptor;
import com.lulobank.clients.services.ports.repository.TimestampRepository;
import com.lulobank.redis.service.LuloRedis;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.LocaleUtils;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RedisTimestampAdapter implements TimestampRepository {

    private static final String ERROR_GETTING = "Error getting timestamp for client %s";
    private static final String ERROR_SAVING = "Error saving timestamp for client %s";
    private static final String ERROR_DELETING = "Error deleting timestamp for client %s";
    private static final String DELETE_NOTICE = "Timestamp for new client {} was {}";

    private static final String NAMESPACE = "DIGITAL_EVIDENCE";
    private static final String TIMESTAMP_TOKEN = "DOCUMENT_ACCEPTANCES_TIMESTAMP";
    private static final int TEN_YEARS = 365 * 10;
    private static final Locale LOCALE = LocaleUtils.toLocale("es_CO");

    private final LuloRedis luloRedis;

    public RedisTimestampAdapter(LuloRedis luloRedis) {
        this.luloRedis = luloRedis;
    }

    @Override
    public void saveTimestamp(String emailAddress, String timestamp) {
        TimestampDescriptor timestampWrapper = new TimestampDescriptor(timestamp);
        luloRedis.set(timestampWrapper, TEN_YEARS, TimeUnit.DAYS, NAMESPACE, TIMESTAMP_TOKEN, emailAddress.toLowerCase(LOCALE))
                .onFailure(e -> {
                    throw new TimestampDigitalEvidenceException(String.format(ERROR_SAVING, emailAddress), e);
                });
    }

    @Override
    public Option<TimestampDescriptor> getTimestamp(String emailAddress) {
        return Try.of(() -> luloRedis.get(TimestampDescriptor.class, NAMESPACE, TIMESTAMP_TOKEN, emailAddress.toLowerCase(LOCALE)))
                .map(Option::ofOptional)
                .getOrElseThrow(e -> new TimestampDigitalEvidenceException(String.format(ERROR_GETTING, emailAddress), e));
    }

    @Override
    public Try<Boolean> deleteTimestamp(String emailAddress) {
        return luloRedis.delete(NAMESPACE, TIMESTAMP_TOKEN, emailAddress.toLowerCase(LOCALE))
                .peek(result -> log.info(DELETE_NOTICE, emailAddress, result ? "deleted" : "not found"))
                .onFailure(e -> {
                    throw new TimestampDigitalEvidenceException(String.format(ERROR_DELETING, emailAddress), e);
                });
    }

}
