package com.starlc.common.mq.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * RocketMQ配置属性
 */
@Data
@ConfigurationProperties(prefix = "rocketmq")
public class RocketMQProperties {
    /**
     * 命名服务器地址
     */
    private String namesrvAddr;

    /**
     * 生产者组
     */
    private String producerGroup;

    /**
     * 发送消息超时时间（毫秒）
     */
    private Integer sendMsgTimeout = 3000;

    /**
     * 消息最大长度
     */
    private Integer maxMessageSize = 4194304;

    /**
     * 压缩消息体阈值
     */
    private Integer compressMsgBodyOverHowmuch = 4096;

    /**
     * 在同步模式下发送失败时重试的次数
     */
    private Integer retryTimesWhenSendFailed = 2;

    /**
     * 在异步模式下发送失败时重试的次数
     */
    private Integer retryTimesWhenSendAsyncFailed = 2;

    /**
     * 消息轨迹开关
     */
    private Boolean enableMessageTrace = false;

    /**
     * 消息轨迹主题
     */
    private String customizedTraceTopic;
}