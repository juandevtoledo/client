package com.lulobank.clients.services.util;

import com.lulobank.clients.services.events.CreditAccepted;
import com.lulobank.clients.services.utils.EventUtils;
import com.lulobank.core.events.Event;
import org.junit.Test;

import java.util.Objects;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class EventUtilsTest {

    @Test
    public void getEventAddicionalFields() {
        String events = "{\n" +
                "    \"id\": \"661f8f88-a235-46cc-8dec-6b540dd4c8b8\",\n" +
                "    \"eventType\": \"CreditAccepted\",\n" +
                "    \"payload\": {\n" +
                "        \"idClient\": \"93246305-f867-4b78-9594-9dacb1af8a95\",\n" +
                "        \"checkpoint\": \"LOAN_CREATED\"\n" +
                "    },\n" +
                "    \"receiveCount\": 0,\n" +
                "    \"maximumReceives\": 5,\n" +
                "    \"delay\": 5\n" +
                "}";
        Event<CreditAccepted> e = EventUtils.getEvent(events, "com.lulobank.clients.services");
        assertThat(e.getEventType(), is(CreditAccepted.class.getSimpleName()));
        assertThat(e.getId(), is("661f8f88-a235-46cc-8dec-6b540dd4c8b8"));
    }

    @Test
    public void getEvent() {
        String events = "{\n" +
                "    \"id\": \"661f8f88-a235-46cc-8dec-6b540dd4c8b8\",\n" +
                "    \"eventType\": \"CreditAccepted\",\n" +
                "    \"payload\": {\n" +
                "        \"idClient\": \"93246305-f867-4b78-9594-9dacb1af8a95\",\n" +
                "        \"checkpoint\": \"LOAN_CREATED\"\n" +
                "    }\n" +
                "}";
        Event<CreditAccepted> e = EventUtils.getEvent(events, "com.lulobank.clients.services");
        assertThat(e.getEventType(), is(CreditAccepted.class.getSimpleName()));
        assertThat(e.getId(), is("661f8f88-a235-46cc-8dec-6b540dd4c8b8"));
    }

    @Test
    public void getEventError() {
        String events = "{\n" +
                "    \"id-2\": \"661f8f88-a235-46cc-8dec-6b540dd4c8b8\",\n" +
                "    \"eventType\": \"CreditAccepted\",\n" +
                "    \"payload\": {\n" +
                "        \"idClient\": \"93246305-f867-4b78-9594-9dacb1af8a95\",\n" +
                "        \"checkpoint\": \"LOAN_CREATED\"\n" +
                "    }\n" +
                "}";
        Event e = EventUtils.getEvent(events, "com.lulobank.clients.services.test");
        assertTrue(Objects.isNull(e));
    }
}
