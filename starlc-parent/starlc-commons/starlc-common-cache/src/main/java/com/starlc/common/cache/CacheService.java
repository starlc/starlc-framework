package com.starlc.common.cache;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 统一缓存服务接口
 */
public interface CacheService {
    /**
     * 设置缓存
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    void set(String key, Object value);

    /**
     * 设置缓存并指定过期时间
     *
     * @param key      缓存键
     * @param value    缓存值
     * @param timeout  过期时间
     * @param timeUnit 时间单位
     */
    void set(String key, Object value, long timeout, TimeUnit timeUnit);

    /**
     * 获取缓存
     *
     * @param key   缓存键
     * @param clazz 返回值类型
     * @return 缓存值
     */
    <T> T get(String key, Class<T> clazz);

    /**
     * 删除缓存
     *
     * @param key 缓存键
     */
    void delete(String key);

    /**
     * 判断缓存是否存在
     *
     * @param key 缓存键
     * @return true存在，false不存在
     */
    boolean exists(String key);

    /**
     * 设置缓存过期时间
     *
     * @param key      缓存键
     * @param timeout  过期时间
     * @param timeUnit 时间单位
     * @return true成功，false失败
     */
    boolean expire(String key, long timeout, TimeUnit timeUnit);

    // List操作
    /**
     * 从列表左端插入元素
     *
     * @param key   键
     * @param value 值
     * @return 插入后列表长度
     */
    Long lpush(String key, Object value);

    /**
     * 从列表右端插入元素
     *
     * @param key   键
     * @param value 值
     * @return 插入后列表长度
     */
    Long rpush(String key, Object value);

    /**
     * 获取列表指定范围内的元素
     *
     * @param key   键
     * @param start 开始位置
     * @param end   结束位置
     * @return 元素列表
     */
    <T> List<T> lrange(String key, long start, long end, Class<T> clazz);

    // Set操作
    /**
     * 向集合添加元素
     *
     * @param key   键
     * @param value 值
     * @return 是否添加成功
     */
    boolean sadd(String key, Object value);

    /**
     * 获取集合所有元素
     *
     * @param key   键
     * @param clazz 元素类型
     * @return 元素集合
     */
    <T> Set<T> smembers(String key, Class<T> clazz);

    /**
     * 判断元素是否是集合的成员
     *
     * @param key   键
     * @param value 值
     * @return 是否为成员
     */
    boolean sismember(String key, Object value);

    // Hash操作
    /**
     * 设置哈希表字段的值
     *
     * @param key     键
     * @param hashKey 哈希表字段
     * @param value   值
     */
    void hset(String key, String hashKey, Object value);

    /**
     * 获取哈希表字段的值
     *
     * @param key     键
     * @param hashKey 哈希表字段
     * @param clazz   返回值类型
     * @return 字段值
     */
    <T> T hget(String key, String hashKey, Class<T> clazz);

    /**
     * 获取哈希表所有字段和值
     *
     * @param key   键
     * @param clazz 值类型
     * @return 哈希表
     */
    <T> Map<String, T> hgetAll(String key, Class<T> clazz);

    /**
     * 删除哈希表字段
     *
     * @param key     键
     * @param hashKey 哈希表字段
     * @return 是否删除成功
     */
    boolean hdel(String key, String hashKey);
}