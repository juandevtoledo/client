package com.lulobank.clients.services.util;

import com.lulobank.clients.services.events.ClientVerificationResult;
import com.lulobank.clients.services.features.RetriesOption;
import com.lulobank.clients.services.utils.SQSUtil;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static com.lulobank.clients.services.utils.SQSUtil.RETRY_COUNT_HEADER;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SQSUtilTest {

    @Test
    public void retryOkFirstAttempt(){
        ClientVerificationResult payload= new ClientVerificationResult();
        HashMap<String,Object> header=new HashMap<>();
        Map<Integer, Integer> delay = getDelay();
        Boolean retry= SQSUtil.retryEvent(payload,header,new RetriesOption(2, delay));
        assertTrue(retry);
    }

    @Test
    public void retryOkLastAttempt(){
        ClientVerificationResult payload= new ClientVerificationResult();
        HashMap<String,Object> header=new HashMap<>();
        header.put(RETRY_COUNT_HEADER,0);
        Map<Integer, Integer> delay = getDelay();
        Boolean retry=SQSUtil.retryEvent(payload,header,new RetriesOption(2, delay));
        assertTrue(retry);
    }

    @Test
    public void notRetry(){
        ClientVerificationResult payload= new ClientVerificationResult();
        HashMap<String,Object> header=new HashMap<>();
        header.put(RETRY_COUNT_HEADER,2);
        Map<Integer, Integer> delay = getDelay();
        Boolean retry=SQSUtil.retryEvent(payload,header,new RetriesOption(2, delay));
        assertFalse(retry);
    }

    @NotNull
    public Map<Integer, Integer> getDelay() {
        Map<Integer, Integer> delay=new HashMap<>();
        delay.put(0,10);
        delay.put(1,2);
        return delay;
    }
}
