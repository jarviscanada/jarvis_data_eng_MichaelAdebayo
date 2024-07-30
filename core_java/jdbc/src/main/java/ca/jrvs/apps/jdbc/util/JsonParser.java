package ca.jrvs.apps.jdbc.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;

public class JsonParser {

    /**
     * Convert a java object to JSON string
     *
     * @param object inout object
     * @return JSON string
     * @throws JsonProcessingException
     */
    public static String toJson(Object object, boolean prettyJson, boolean includeNullValues) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        if (!includeNullValues) {
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        }
        if (prettyJson) {
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        }
        return objectMapper.writeValueAsString(object);
    }

    /**
     * Parse JSON string to an Object
     * @param json JSON string
     * @param clazz the class of the object to be returned
     * @param <T> the type of the object to be returned
     * @return the parsed object
     * @throws IOException if there is a problem reading the JSON string
     */
    public static <T> T fromJson(String json, Class<T> clazz) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return (T)objectMapper.readValue(json, clazz);
    }
}
