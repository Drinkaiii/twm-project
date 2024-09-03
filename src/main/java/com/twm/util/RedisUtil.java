package com.twm.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class RedisUtil {

    @Autowired
    private StringRedisTemplate redisTemplate;

    //    public <T> List<T> getOrLoadDataFromCache(String cacheKey,
//                                              Supplier<List<T>> databaseLoader,
//                                              TypeReference<List<T>> typeReference,
//                                              Function<List<T>, List<T>> postProcessor) {
//
//        String cacheData = redisTemplate.opsForValue().get(cacheKey);
//
//        if (isCachedData(cacheKey)) { // Data exists or Redis is disconnected
//            return convertJsonToList(cacheData, typeReference); // Return cache data
//        }
//
//        List<T> data = databaseLoader.get(); // Get SQL data
//        log.info("Data retrieved from the database.");
//
//        if (postProcessor != null) {
//            data = postProcessor.apply(data);
//        }
//
//        String dataJson = convertListToJson(data);
//        setDataToCache(cacheKey, dataJson); // Set SQL data to cache
//
//        return data; // Return SQL data
//    }
    public <T> List<T> getListDataFromCache(String key) {
        String cachedData = getDataFromCache(key); //get cache data, Json
        return convertJsonToList(cachedData); //return cache data
    }

    public <T> void setJsonDataToCache(String key, List<T> value) {
        String dataJson = convertListToJson(value);
        setDataToCache(key, dataJson); //set sql data to cache
    }

    public boolean isCacheExist(String key) {
        return redisTemplate.hasKey(key);
    }

    private String getDataFromCache(String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (RedisConnectionFailureException ex) {
            log.error("Redis connection failed. Falling back to database.", ex);
            return null;
        }
    }

    private void setDataToCache(String key, String value) {
        try {
            redisTemplate.opsForValue().set(key, value);
        } catch (RedisConnectionFailureException ex) {
            log.error("Failed to connect to Redis to set cache.", ex);
        }
    }

    private void clearCache(String key) {
        try {
            redisTemplate.delete(key);
        } catch (RedisConnectionFailureException ex) {
            log.error("Failed to connect to Redis to clear cache.", ex);
        }
    }

    private <T> List<T> convertJsonToList(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, new TypeReference<List<T>>() {
            });
        } catch (Exception e) {
            log.error("Error converting JSON to list", e);
            return Collections.emptyList();
        }
    }

    public <T> String convertListToJson(List<T> list) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(list);
        } catch (Exception e) {
            log.error("Error converting list to JSON", e);
            return "";
        }
    }
}
