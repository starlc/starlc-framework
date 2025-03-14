package com.starlc.common.mq.producer;

import com.starlc.common.message.Message;
import com.starlc.common.message.MessageResult;
import com.starlc.common.mq.config.MessageFailureProperties;
import com.starlc.common.mq.strategy.FailureHandler;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

/**
 * RocketMQ消息发送实现类
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RocketMQMessageSender implements MessageSender {

    private final RocketMQTemplate rocketMQTemplate;
    private final FailureHandler failureHandler;
    private final MessageFailureProperties failureProperties;

    @Override
    public MessageResult syncSend(Object message, Message messageProperties) {
        try {
            String destination = messageProperties.getExchange() + ":" + messageProperties.getRoutingKey();
            org.springframework.messaging.Message<?> springMessage = MessageBuilder.withPayload(message).build();
            rocketMQTemplate.syncSend(destination, springMessage);
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
            String destination = messageProperties.getExchange() + ":" + messageProperties.getRoutingKey();
            org.springframework.messaging.Message<?> springMessage = MessageBuilder.withPayload(message).build();
            
            rocketMQTemplate.asyncSend(destination, springMessage, new org.apache.rocketmq.client.producer.SendCallback() {
                @Override
                public void onSuccess(org.apache.rocketmq.client.producer.SendResult sendResult) {
                    future.complete(MessageResult.success());
                }

                @Override
                public void onException(Throwable throwable) {
                    log.error("异步发送消息失败，exchange: {}, routingKey: {}, message: {}",
                        messageProperties.getExchange(), messageProperties.getRoutingKey(), message, throwable);
                    failureHandler.handleFailure(message, messageProperties, 1, throwable);
                    future.complete(MessageResult.failure(throwable.getMessage()));
                }
            });
        } catch (Exception e) {
            log.error("异步发送消息失败，exchange: {}, routingKey: {}, message: {}",
                messageProperties.getExchange(), messageProperties.getRoutingKey(), message, e);
            failureHandler.handleFailure(message, messageProperties, 1, e);
            future.complete(MessageResult.failure(e.getMessage()));
        }
        return future;
    }
}