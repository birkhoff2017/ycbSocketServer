package com.ycb.socket.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class JsonUtils {

    private static Logger logger = LoggerFactory.getLogger(JsonUtils.class);

    private final static ObjectMapper objectMapper = new ObjectMapper();

    public static String writeValueAsString(Object value) {
        try {
            objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> readValue(String content) {
        try {
            return objectMapper.readValue(content, Map.class);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return Maps.newHashMap();
    }
}
