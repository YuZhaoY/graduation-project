package com.scu.stu.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisLock {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public boolean lock(String key, String value, long expireTime) {
        Boolean success = redisTemplate.opsForValue().setIfAbsent(key, value, expireTime, TimeUnit.MILLISECONDS);
        return success != null && success;
    }

    public void unlock(String key, String value) {
        String currentValue = redisTemplate.opsForValue().get(key);
        if (currentValue != null && currentValue.equals(value)) {
            redisTemplate.delete(key);
        }
    }
}
