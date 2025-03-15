package com.starlc.common.mq.interceptor;


import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.UUID;

import lombok.extern.slf4j.Slf4j;

/**
 * 消息追踪拦截器
 * 用于增强消息追踪能力，便于问题排查
 */
@Slf4j
@Component
public class MessageTraceInterceptor {

    private static final String TRACE_ID_HEADER = "X-Trace-Id";
    private static final String TIMESTAMP_HEADER = "X-Timestamp";
    private static final String SOURCE_APP_HEADER = "X-Source-App";

    /**
     * 在消息发送前处理
     * 添加追踪信息到消息头
     *
     * @param message           消息内容
     */
    public void beforeSend(Object message) {
        try {
            // 如果消息是Spring消息类型，添加追踪信息到消息头
            if (message instanceof Message) {
                Message<?> springMessage = (Message<?>) message;
                
                // 使用MessageBuilder创建新消息，保留原始消息的payload和headers
                MessageBuilder<?> builder = MessageBuilder.fromMessage(springMessage);
                
                // 添加追踪ID
                builder.setHeader(TRACE_ID_HEADER, generateTraceId());
                
                // 添加时间戳
                builder.setHeader(TIMESTAMP_HEADER, System.currentTimeMillis());
                
                // 添加来源应用
                builder.setHeader(SOURCE_APP_HEADER, getApplicationName());
                
                // 替换原始消息
                message = builder.build();
            }
            
            log.debug("消息追踪信息已添加");
        } catch (Exception e) {
            log.warn("添加消息追踪信息失败", e);
        }
    }

    /**
     * 在消息接收时处理
     * 记录追踪信息
     *
     * @param message 消息内容
     * @param headers 消息头
     */
    public void afterReceive(Object message, java.util.Map<String, Object> headers) {
        try {
            if (headers != null) {
                String traceId = (String) headers.get(TRACE_ID_HEADER);
                Long timestamp = (Long) headers.get(TIMESTAMP_HEADER);
                String sourceApp = (String) headers.get(SOURCE_APP_HEADER);
                
                if (traceId != null) {
                    log.info("接收到消息，追踪ID：{}，来源应用：{}，发送时间：{}", 
                            traceId, 
                            sourceApp != null ? sourceApp : "unknown", 
                            timestamp != null ? new java.util.Date(timestamp) : "unknown");
                }
            }
        } catch (Exception e) {
            log.warn("处理消息追踪信息失败", e);
        }
    }

    /**
     * 生成追踪ID
     *
     * @return 追踪ID
     */
    private String generateTraceId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 获取应用名称
     *
     * @return 应用名称
     */
    private String getApplicationName() {
        try {
            return System.getProperty("spring.application.name", "unknown");
        } catch (Exception e) {
            return "unknown";
        }
    }
}