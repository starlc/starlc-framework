package com.starlc.common.mq.strategy;

import org.apache.rocketmq.client.producer.SendResult;

/**
 * 消息发送失败处理策略接口
 */
public interface FailureHandler {
    /**
     * 处理失败的消息
     *
     * @param topic 主题
     * @param tags 标签
     * @param keys 消息键
     * @param message 消息内容
     * @param failureCount 失败次数
     * @param lastError 最后一次失败的异常
     */
    void handleFailure(String topic, String tags, String keys, Object message, int failureCount, Throwable lastError);
}