package com.lulobank.clients.services.utils;

import com.lulobank.clients.services.events.AbstractRetryFeature;
import com.lulobank.clients.services.features.RetriesOption;
import io.vavr.control.Option;

import java.util.Map;

public class SQSUtil {

    public static final String RETRY_COUNT_HEADER = "retryCount";

    private SQSUtil() {
    }

    private static Integer getNextRetry(Map<String, Object> header) {
        return Option.of(header.get(RETRY_COUNT_HEADER))
                .map(attempt -> (int) attempt+1)
                .getOrElse(0);
    }

    private static void setRetryOptions(AbstractRetryFeature retryObject, int delay, int nextRetry) {
        retryObject.setDelayInSeconds(delay);
        retryObject.setRetryCount(nextRetry);

    }

    public static boolean retryEvent(AbstractRetryFeature payload, Map<String, Object> headersSqs, RetriesOption retriesOption) {
        int nextRetry = getNextRetry(headersSqs);
        return Option.of(nextRetry < retriesOption.getMaxRetries())
                .filter(Boolean::booleanValue)
                .peek(retry ->
                        setRetryOptions(payload, retriesOption.getDelayOptions().get(nextRetry), nextRetry))
                .getOrElse(false);
    }
}
