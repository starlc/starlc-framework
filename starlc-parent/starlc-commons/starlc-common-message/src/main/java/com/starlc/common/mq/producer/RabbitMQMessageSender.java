package com.starlc.common.mq.producer;

import com.starlc.common.message.Message;
import com.starlc.common.message.MessageResult;
import com.starlc.common.mq.config.MessageFailureProperties;
import com.starlc.common.mq.interceptor.MessageTraceInterceptor;
import com.starlc.common.mq.strategy.FailureHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.core.MessageProperties;

import java.util.concurrent.CompletableFuture;

/**
 * RabbitMQ消息发送实现类
 */
@Slf4j
@RequiredArgsConstructor
public class RabbitMQMessageSender implements MessageSender<Object> {

    private final RabbitTemplate rabbitTemplate;
    private final FailureHandler failureHandler;
    private final MessageFailureProperties failureProperties;
    private final MessageTraceInterceptor traceInterceptor;

    @Override
    public MessageResult syncSend(Object message, Message messageProperties) {
        try {
            // 添加消息追踪信息
            traceInterceptor.beforeSend(message, messageProperties);

            CorrelationData correlationData = new CorrelationData();
            org.springframework.amqp.core.Message amqpMessage = createAmqpMessage(message, messageProperties);
            rabbitTemplate.convertAndSend(
                    messageProperties.getExchange(),
                    messageProperties.getRoutingKey(),
                    amqpMessage,
                    correlationData
            );
            return MessageResult.success();
        } catch (Exception e) {
            log.error("同步发送消息失败，exchange: {}, routingKey: {}, message: {}",
                    messageProperties.getExchange(), messageProperties.getRoutingKey(), message, e);
            failureHandler.handleFailure(message, messageProperties, 1, e);
            return MessageResult.failure(e.getMessage());
        }
    }

    @Override
    public CompletableFuture<MessageResult> asyncSend(Object message, Message messageProperties) {
        CompletableFuture<MessageResult> future = new CompletableFuture<>();
        try {
            // 添加消息追踪信息
            traceInterceptor.beforeSend(message, messageProperties);

            CorrelationData correlationData = new CorrelationData();
            rabbitTemplate.setConfirmCallback((correlation, ack, cause) -> {
                if (ack) {
                    future.complete(MessageResult.success());
                } else {
                    future.complete(MessageResult.failure(cause));
                }
            });

            org.springframework.amqp.core.Message amqpMessage = createAmqpMessage(message, messageProperties);
            rabbitTemplate.convertAndSend(
                    messageProperties.getExchange(),
                    messageProperties.getRoutingKey(),
                    amqpMessage,
                    correlationData
            );
        } catch (Exception e) {
            log.error("异步发送消息失败，exchange: {}, routingKey: {}, message: {}",
                    messageProperties.getExchange(), messageProperties.getRoutingKey(), message, e);
            failureHandler.handleFailure(message, messageProperties, 1, e);
            future.complete(MessageResult.failure(e.getMessage()));
        }
        return future;
    }

    private org.springframework.amqp.core.Message createAmqpMessage(Object message, Message messageProperties) {
        MessageProperties properties = new MessageProperties();

        // 设置消息头
        if (messageProperties.getHeaders() != null) {
            messageProperties.getHeaders().forEach(properties::setHeader);
        }

        // 设置优先级
        if (messageProperties.getPriority() != null) {
            properties.setPriority(messageProperties.getPriority());
        }

        // 设置延迟时间
        if (messageProperties.getDelayTimeMs() != null) {
            properties.setDelay(messageProperties.getDelayTimeMs().intValue());
        }

        // 转换消息内容
        byte[] messageBytes = rabbitTemplate.getMessageConverter().toMessage(message, properties).getBody();
        return new org.springframework.amqp.core.Message(messageBytes, properties);
    }
}