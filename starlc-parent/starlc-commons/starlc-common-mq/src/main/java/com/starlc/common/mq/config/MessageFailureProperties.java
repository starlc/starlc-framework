package com.starlc.common.mq.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 消息发送失败处理配置
 */
@Data
@ConfigurationProperties(prefix = "starlc.rocketmq.failure")
public class MessageFailureProperties {
    /**
     * 最大重试次数
     */
    private int maxRetryCount = 3;

    /**
     * 失败处理策略（dead-letter-queue 或 email-alert）
     */
    private String failureStrategy = "dead-letter-queue";
}