package com.starlc.common.cache.redis.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
* @Description:    cache配置管理文件
* @Author:         liuc
* @CreateDate:     2025/3/14 17:40
* @UpdateRemark:   修改内容
* @Version:        1.0
*/
@Setter
@Getter
@ConfigurationProperties(prefix = "cache-manager")
public class CacheManagerProperties {
    private List<CacheConfig> configs;

    @Setter
    @Getter
    public static class CacheConfig {
        /**
         * cache key
         */
        private String key;
        /**
         * 过期时间，sec
         */
        private long second = 60;
    }
}
