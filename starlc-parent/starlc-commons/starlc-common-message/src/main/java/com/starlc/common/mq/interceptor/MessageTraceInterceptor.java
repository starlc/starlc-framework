package com.starlc.common.mq.interceptor;

import com.starlc.common.message.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

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
     * @param messageProperties 消息属性
     */
    public void beforeSend(Object message, Message messageProperties) {
        // 如果消息头为空，初始化一个
        if (messageProperties.getHeaders() == null) {
            messageProperties.setHeaders(new java.util.HashMap<>());
        }

        // 添加追踪ID
        if (!messageProperties.getHeaders().containsKey(TRACE_ID_HEADER)) {
            messageProperties.getHeaders().put(TRACE_ID_HEADER, generateTraceId());
        }

        // 添加时间戳
        messageProperties.getHeaders().put(TIMESTAMP_HEADER, System.currentTimeMillis());

        // 添加来源应用
        messageProperties.getHeaders().put(SOURCE_APP_HEADER, getApplicationName());

        log.debug("消息追踪: 发送消息 TraceId={}, Topic/Exchange={}, Tags/RoutingKey={}",
                messageProperties.getHeaders().get(TRACE_ID_HEADER),
                messageProperties.getTopic() != null ? messageProperties.getTopic() : messageProperties.getExchange(),
                messageProperties.getTags() != null ? messageProperties.getTags() : messageProperties.getRoutingKey());
    }

    /**
     * 在消息接收时处理
     * 记录追踪信息
     *
     * @param message 消息内容
     * @param headers 消息头
     */
    public void afterReceive(Object message, java.util.Map<String, Object> headers) {
        String traceId = headers != null ? (String) headers.get(TRACE_ID_HEADER) : null;
        Long timestamp = headers != null ? (Long) headers.get(TIMESTAMP_HEADER) : null;
        String sourceApp = headers != null ? (String) headers.get(SOURCE_APP_HEADER) : null;

        if (traceId != null) {
            long costTime = timestamp != null ? System.currentTimeMillis() - timestamp : -1;
            log.debug("消息追踪: 接收消息 TraceId={}, SourceApp={}, CostTime={}ms",
                    traceId, sourceApp, costTime);
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