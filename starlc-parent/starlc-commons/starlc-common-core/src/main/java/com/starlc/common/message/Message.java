package com.starlc.common.message;

import java.util.Map;

/**
 * 消息实体类，封装不同中间件的特殊参数
 */
public class Message {
    private String topic;              // 主题，通用属性
    private String tags;              // 标签，RocketMQ特有
    private String keys;              // 消息键，RocketMQ特有
    private String exchange;          // 交换机，RabbitMQ特有
    private String routingKey;        // 路由键，RabbitMQ特有
    private Integer partition;        // 分区，Kafka特有
    private Integer priority;         // 优先级
    private Long delayTimeMs;         // 延迟时间（毫秒）
    private String messageType;       // 消息类型（NORMAL, FIFO, DELAY, TRANSACTION）
    private Map<String, Object> headers;  // 消息头键值对，主要用于RabbitMQ

    // Getters and Setters
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    public String getKeys() { return keys; }
    public void setKeys(String keys) { this.keys = keys; }
    public String getExchange() { return exchange; }
    public void setExchange(String exchange) { this.exchange = exchange; }
    public String getRoutingKey() { return routingKey; }
    public void setRoutingKey(String routingKey) { this.routingKey = routingKey; }
    public Integer getPartition() { return partition; }
    public void setPartition(Integer partition) { this.partition = partition; }
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
    public Long getDelayTimeMs() { return delayTimeMs; }
    public void setDelayTimeMs(Long delayTimeMs) { this.delayTimeMs = delayTimeMs; }
    public String getMessageType() { return messageType; }
    public void setMessageType(String messageType) { this.messageType = messageType; }
    public Map<String, Object> getHeaders() { return headers; }
    public void setHeaders(Map<String, Object> headers) { this.headers = headers; }
}