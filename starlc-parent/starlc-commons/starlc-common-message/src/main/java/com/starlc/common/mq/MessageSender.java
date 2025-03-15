package com.starlc.common.mq;

import java.util.concurrent.CompletableFuture;

/**
 * 通用消息发送接口，支持RocketMQ
 * 提供同步发送、异步发送和事务消息发送等标准方法
 */
public interface MessageSender {

    /**
     * 同步发送消息
     *
     * @param destination 目标主题
     * @param message 消息内容
     * @return 发送结果
     */
    Object syncSend(String destination, Object message);
    
    /**
     * 同步发送消息（带标签）
     *
     * @param destination 目标主题
     * @param message 消息内容
     * @param tags 消息标签
     * @return 发送结果
     */
    Object syncSend(String destination, Object message, String tags);
    
    /**
     * 异步发送消息
     *
     * @param destination 目标主题
     * @param message 消息内容
     * @return 异步发送结果
     */
    CompletableFuture<Object> asyncSend(String destination, Object message);
    
    /**
     * 发送事务消息
     *
     * @param destination 目标主题
     * @param message 消息内容
     * @param arg 事务参数
     * @return 发送结果
     */
    Object sendMessageInTransaction(String destination, Object message, Object arg);
    
    /**
     * 发送延迟消息
     *
     * @param destination 目标主题
     * @param message 消息内容
     * @param delayLevel 延迟级别
     * @return 发送结果
     */
    Object sendDelayMessage(String destination, Object message, int delayLevel);
}