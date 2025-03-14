package com.starlc.common.mq.producer;

import java.util.concurrent.CompletableFuture;
import com.starlc.common.message.Message;
import com.starlc.common.message.MessageResult;

import lombok.extern.slf4j.Slf4j;

/**
 * 通用消息发送接口，支持RocketMQ、RabbitMQ和Kafka
 */
@Slf4j
public interface MessageSender<T> {
    /**
     * 同步发送消息
     *
     * @param message 消息内容
     * @param messageProperties 消息属性
     * @return 发送结果
     */
    MessageResult syncSend(T message, Message messageProperties);

    /**
     * 异步发送消息
     *
     * @param message 消息内容
     * @param messageProperties 消息属性
     * @return 发送结果的Future
     */
    CompletableFuture<MessageResult> asyncSend(T message, Message messageProperties);

    /**
     * 处理消息发送失败的情况
     *
     * @param message 消息内容
     * @param messageProperties 消息属性
     * @param failureCount 失败次数
     * @param lastError 最后一次失败的异常
     */
    default void handleFailure(T message, Message messageProperties, int failureCount, Throwable lastError) {
        // 默认实现为记录错误日志
        log.error("消息发送失败，exchange/topic: {}, routingKey/partition: {}, message: {}, 失败次数: {}",
            messageProperties.getExchange() != null ? messageProperties.getExchange() : messageProperties.getTopic(),
            messageProperties.getRoutingKey() != null ? messageProperties.getRoutingKey() : messageProperties.getPartition(),
            message, failureCount, lastError);
    }
}