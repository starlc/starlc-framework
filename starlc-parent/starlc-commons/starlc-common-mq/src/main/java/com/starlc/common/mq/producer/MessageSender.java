package com.starlc.common.mq.producer;

import org.apache.rocketmq.client.producer.SendResult;

import java.util.concurrent.CompletableFuture;

/**
 * 消息发送接口
 */
public interface MessageSender {
    /**
     * 同步发送消息
     *
     * @param topic 主题
     * @param message 消息内容
     * @return 发送结果
     */
    SendResult syncSend(String topic, Object message);

    /**
     * 同步发送消息
     *
     * @param topic 主题
     * @param tags 标签
     * @param message 消息内容
     * @return 发送结果
     */
    SendResult syncSend(String topic, String tags, Object message);

    /**
     * 同步发送消息
     *
     * @param topic 主题
     * @param tags 标签
     * @param keys 消息键
     * @param message 消息内容
     * @return 发送结果
     */
    SendResult syncSend(String topic, String tags, String keys, Object message);

    /**
     * 异步发送消息
     *
     * @param topic 主题
     * @param message 消息内容
     * @return 发送结果的Future
     */
    CompletableFuture<SendResult> asyncSend(String topic, Object message);

    /**
     * 异步发送消息
     *
     * @param topic 主题
     * @param tags 标签
     * @param message 消息内容
     * @return 发送结果的Future
     */
    CompletableFuture<SendResult> asyncSend(String topic, String tags, Object message);

    /**
     * 异步发送消息
     *
     * @param topic 主题
     * @param tags 标签
     * @param keys 消息键
     * @param message 消息内容
     * @return 发送结果的Future
     */
    CompletableFuture<SendResult> asyncSend(String topic, String tags, String keys, Object message);
}