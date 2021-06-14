package com.statemachine.statemachine.domain.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.AttributeConverter;
import java.io.IOException;
import java.util.Map;

@Slf4j
public class MapJsonConverter implements AttributeConverter<Map<String, Object>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<String, Object> attributes) {
        String dbData = null;
        try {
            dbData = objectMapper.writeValueAsString(attributes);
        } catch (final JsonProcessingException e) {
            log.error("JSON writing error", e);
        }

        return dbData;
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String dbData) {
        Map<String, Object> attributes = null;
        try {
            attributes = objectMapper.readValue(dbData, Map.class);
        } catch (final IOException e) {
            log.error("JSON reading error", e);
        }

        return attributes;
    }
}