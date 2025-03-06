package com.starlc.common.cache.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.starlc.common.cache.CacheService;

import java.util.concurrent.TimeUnit;

/**
 * Guava本地缓存实现类
 */
public class GuavaCacheServiceImpl implements CacheService {

    private final Cache<String, Object> cache;
    private final Cache<String, List<Object>> listCache;
    private final Cache<String, Set<Object>> setCache;
    private final Cache<String, Map<String, Object>> hashCache;

    public GuavaCacheServiceImpl(long maximumSize, long expireAfterWrite, TimeUnit timeUnit) {
        this.cache = CacheBuilder.newBuilder()
                .maximumSize(maximumSize)
                .expireAfterWrite(expireAfterWrite, timeUnit)
                .build();
        this.listCache = CacheBuilder.newBuilder()
                .maximumSize(maximumSize)
                .expireAfterWrite(expireAfterWrite, timeUnit)
                .build();
        this.setCache = CacheBuilder.newBuilder()
                .maximumSize(maximumSize)
                .expireAfterWrite(expireAfterWrite, timeUnit)
                .build();
        this.hashCache = CacheBuilder.newBuilder()
                .maximumSize(maximumSize)
                .expireAfterWrite(expireAfterWrite, timeUnit)
                .build();
    }

    @Override
    public void set(String key, Object value) {
        cache.put(key, value);
    }

    @Override
    public void set(String key, Object value, long timeout, TimeUnit timeUnit) {
        // Guava Cache不支持单独设置过期时间，使用默认的过期策略
        cache.put(key, value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> clazz) {
        return (T) cache.getIfPresent(key);
    }

    @Override
    public void delete(String key) {
        cache.invalidate(key);
    }

    @Override
    public boolean exists(String key) {
        return cache.getIfPresent(key) != null;
    }

    @Override
    public boolean expire(String key, long timeout, TimeUnit timeUnit) {
        // Guava Cache不支持单独设置过期时间
        return true;
    }

    // List操作
    @Override
    public Long lpush(String key, Object value) {
        List<Object> list = listCache.getIfPresent(key);
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(0, value);
        listCache.put(key, list);
        return (long) list.size();
    }

    @Override
    public Long rpush(String key, Object value) {
        List<Object> list = listCache.getIfPresent(key);
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(value);
        listCache.put(key, list);
        return (long) list.size();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> lrange(String key, long start, long end, Class<T> clazz) {
        List<Object> list = listCache.getIfPresent(key);
        if (list == null) {
            return new ArrayList<>();
        }
        int size = list.size();
        int fromIndex = (int) Math.max(0, start);
        int toIndex = (int) Math.min(size, end + 1);
        if (fromIndex >= size || fromIndex >= toIndex) {
            return new ArrayList<>();
        }
        return (List<T>) list.subList(fromIndex, toIndex);
    }

    // Set操作
    @Override
    public boolean sadd(String key, Object value) {
        Set<Object> set = setCache.getIfPresent(key);
        if (set == null) {
            set = new HashSet<>();
        }
        boolean added = set.add(value);
        setCache.put(key, set);
        return added;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Set<T> smembers(String key, Class<T> clazz) {
        Set<Object> set = setCache.getIfPresent(key);
        if (set == null) {
            return new HashSet<>();
        }
        return (Set<T>) set;
    }

    @Override
    public boolean sismember(String key, Object value) {
        Set<Object> set = setCache.getIfPresent(key);
        return set != null && set.contains(value);
    }

    // Hash操作
    @Override
    public void hset(String key, String hashKey, Object value) {
        Map<String, Object> hash = hashCache.getIfPresent(key);
        if (hash == null) {
            hash = new HashMap<>();
        }
        hash.put(hashKey, value);
        hashCache.put(key, hash);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T hget(String key, String hashKey, Class<T> clazz) {
        Map<String, Object> hash = hashCache.getIfPresent(key);
        if (hash == null) {
            return null;
        }
        return (T) hash.get(hashKey);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Map<String, T> hgetAll(String key, Class<T> clazz) {
        Map<String, Object> hash = hashCache.getIfPresent(key);
        if (hash == null) {
            return new HashMap<>();
        }
        return (Map<String, T>) hash;
    }

    @Override
    public boolean hdel(String key, String hashKey) {
        Map<String, Object> hash = hashCache.getIfPresent(key);
        if (hash == null) {
            return false;
        }
        Object removed = hash.remove(hashKey);
        if (hash.isEmpty()) {
            hashCache.invalidate(key);
        } else {
            hashCache.put(key, hash);
        }
        return removed != null;
    }
}