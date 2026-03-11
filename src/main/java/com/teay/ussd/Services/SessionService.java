package com.teay.ussd.Services;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class SessionService {
    private final StringRedisTemplate redisTemplate;


    public SessionService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveState(String sessionId, String state){
        redisTemplate.opsForValue().set(sessionId,state, Duration.ofMinutes(5));
    }

    public String getState(String sessionId){
        return redisTemplate.opsForValue().get(sessionId);
    }

    public void clearState(String sessionId){
        redisTemplate.delete(sessionId);
    }
}
