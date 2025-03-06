package com.starlc.common.cache.impl;

import com.starlc.common.cache.CacheService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * Redis缓存实现类
 */
public class RedisCacheServiceImpl implements CacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;

    public RedisCacheServiceImpl(RedisTemplate<String, Object> redisTemplate, StringRedisTemplate stringRedisTemplate) {
        this.redisTemplate = redisTemplate;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    // List操作
    @Override
    public Long lpush(String key, Object value) {
        return redisTemplate.opsForList().leftPush(key, value);
    }

    @Override
    public Long rpush(String key, Object value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> lrange(String key, long start, long end, Class<T> clazz) {
        return (List<T>) redisTemplate.opsForList().range(key, start, end);
    }

    // Set操作
    @Override
    public boolean sadd(String key, Object value) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().add(key, value));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Set<T> smembers(String key, Class<T> clazz) {
        return (Set<T>) redisTemplate.opsForSet().members(key);
    }

    @Override
    public boolean sismember(String key, Object value) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, value));
    }

    // Hash操作
    @Override
    public void hset(String key, String hashKey, Object value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T hget(String key, String hashKey, Class<T> clazz) {
        return (T) redisTemplate.opsForHash().get(key, hashKey);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Map<String, T> hgetAll(String key, Class<T> clazz) {
        return (Map<String, T>) redisTemplate.opsForHash().entries(key);
    }

    @Override
    public boolean hdel(String key, String hashKey) {
        return redisTemplate.opsForHash().delete(key, hashKey) > 0;
    }

    @Override
    public void set(String key, Object value) {
        if (value instanceof String) {
            stringRedisTemplate.opsForValue().set(key, (String) value);
        } else {
            redisTemplate.opsForValue().set(key, value);
        }
    }

    @Override
    public void set(String key, Object value, long timeout, TimeUnit timeUnit) {
        if (value instanceof String) {
            stringRedisTemplate.opsForValue().set(key, (String) value, timeout, timeUnit);
        } else {
            redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> clazz) {
        if (String.class.equals(clazz)) {
            return (T) stringRedisTemplate.opsForValue().get(key);
        }
        return (T) redisTemplate.opsForValue().get(key);
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public boolean exists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    @Override
    public boolean expire(String key, long timeout, TimeUnit timeUnit) {
        return Boolean.TRUE.equals(redisTemplate.expire(key, timeout, timeUnit));
    }
}