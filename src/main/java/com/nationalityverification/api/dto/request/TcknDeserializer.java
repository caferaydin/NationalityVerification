package com.nationalityverification.api.dto.request;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * Deserialises the {@code tckn} field regardless of whether it arrives
 * as a JSON number or a JSON string. Always returns a String.
 */
public class TcknDeserializer extends JsonDeserializer<String> {

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        // Number token (e.g. 11111111111) or string token ("11111111111")
        return p.getValueAsString();
    }
}
