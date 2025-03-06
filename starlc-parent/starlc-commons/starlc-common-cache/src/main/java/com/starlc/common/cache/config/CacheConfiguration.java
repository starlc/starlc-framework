package com.starlc.common.cache.config;

import com.starlc.common.cache.CacheService;
import com.starlc.common.cache.impl.GuavaCacheServiceImpl;
import com.starlc.common.cache.impl.RedisCacheServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * 缓存配置类
 */
@Configuration
public class CacheConfiguration {

    /**
     * Redis缓存实现
     */
    @Bean
    @ConditionalOnProperty(name = "spring.cache.type", havingValue = "redis", matchIfMissing = true)
    public CacheService redisCacheService(RedisTemplate<String, Object> redisTemplate,
                                        StringRedisTemplate stringRedisTemplate) {
        return new RedisCacheServiceImpl(redisTemplate, stringRedisTemplate);
    }

    /**
     * Guava本地缓存实现
     */
    @Bean
    @ConditionalOnProperty(name = "spring.cache.type", havingValue = "guava")
    public CacheService guavaCacheService() {
        // 默认配置：最大容量1000，过期时间1小时
        return new GuavaCacheServiceImpl(1000, 1, TimeUnit.HOURS);
    }
}