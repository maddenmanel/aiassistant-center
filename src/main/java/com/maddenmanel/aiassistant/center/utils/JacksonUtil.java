package com.maddenmanel.aiassistant.center.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Jackson对象转化工具类.
 */
@Slf4j
public class JacksonUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        // Configure ObjectMapper to avoid errors on unknown properties
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        OBJECT_MAPPER.findAndRegisterModules(); // Automatically registers date and time modules
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private JacksonUtil() {
    }

    /**
     * Convert object to JSON string.
     *
     * @param detail Object to convert
     * @return JSON String
     */
    public static String objectToJsonStr(Object detail) {
        if (detail == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(detail);
        } catch (JsonProcessingException e) {
            String message = String.format("Failed to convert object to JSON, detail: %s, e=%s", detail, e);
            log.error(message);
            throw new RuntimeException(message);
        }
    }

    /**
     * Convert JSON string to Object.
     *
     * @param jsonStr JSON String
     * @param clazz   Target class
     * @param <T>     Type
     * @return Object
     */
    public static <T> T jsonToObject(String jsonStr, Class<T> clazz) {
        if (jsonStr == null || jsonStr.isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(jsonStr, clazz);
        } catch (IOException e) {
            String message = String.format("Failed to convert JSON string to object, jsonStr: %s, e=%s", jsonStr, e);
            log.error(message);
            throw new RuntimeException(message);
        }
    }

    /**
     * Convert JSON string to Object with TypeReference.
     *
     * @param jsonStr JSON String
     * @param type    Type
     * @param <T>     Type
     * @return Object
     */
    public static <T> T jsonToObject(String jsonStr, Type type) {
        if (jsonStr == null || jsonStr.isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(jsonStr, OBJECT_MAPPER.constructType(type));
        } catch (IOException e) {
            String message = String.format("Failed to convert JSON string to object, jsonStr: %s, e=%s", jsonStr, e);
            log.error(message);
            throw new RuntimeException(message);
        }
    }
}
