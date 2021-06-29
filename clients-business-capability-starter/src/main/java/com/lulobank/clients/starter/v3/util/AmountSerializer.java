package com.lulobank.clients.starter.v3.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class AmountSerializer extends JsonSerializer<Double> {

    @Override
    public void serialize(Double value, JsonGenerator generator, SerializerProvider serializers) throws IOException {
        generator.writeNumber(String.format("%.0f", value));
    }
}
