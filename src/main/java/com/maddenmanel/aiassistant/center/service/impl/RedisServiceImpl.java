package com.maddenmanel.aiassistant.center.service.impl;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.maddenmanel.aiassistant.center.service.RedisService;
import com.maddenmanel.aiassistant.center.utils.JacksonUtil;

@Service
public class RedisServiceImpl implements RedisService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public void saveData(String key, Object value) {
        try {
            // Convert object to JSON string
            String jsonData = JacksonUtil.objectToJsonStr(value);
            redisTemplate.opsForValue().set(key, jsonData);
        } catch (Exception e) {
            throw new RuntimeException("Error serializing object to JSON", e);
        }
    }

    @Override
    public <T> T getData(String key, Class<T> clazz) {
        try {
            String jsonValue = redisTemplate.opsForValue().get(key);
            if (jsonValue == null) {
                return null;
            }
            return  JacksonUtil.jsonToObject(jsonValue, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing JSON to object", e);
        }
    }

    @Override
    public void deleteData(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public void setExpire(String redisKey, int second) {
        redisTemplate.expire(redisKey, second, TimeUnit.SECONDS);
    }

}

