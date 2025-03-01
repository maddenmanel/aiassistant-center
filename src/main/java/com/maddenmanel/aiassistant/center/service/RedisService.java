package com.maddenmanel.aiassistant.center.service;

public interface RedisService {
    void saveData(String key, Object value);
    public <T> T getData(String key, Class<T> clazz);
    void deleteData(String key);
    void setExpire(String redisKey, int second);
} 
