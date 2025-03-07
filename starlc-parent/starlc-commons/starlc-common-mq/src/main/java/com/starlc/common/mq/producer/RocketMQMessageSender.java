package com.starlc.common.mq.producer;

import com.starlc.common.mq.config.MessageFailureProperties;
import com.starlc.common.mq.strategy.DeadLetterQueueHandler;
import com.starlc.common.mq.strategy.EmailAlertHandler;
import com.starlc.common.mq.strategy.FailureHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * RocketMQ消息发送实现类
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RocketMQMessageSender implements MessageSender {

    private final RocketMQTemplate rocketMQTemplate;
    private final MessageFailureProperties failureProperties;
    private final DeadLetterQueueHandler deadLetterQueueHandler;
    private final EmailAlertHandler emailAlertHandler;
    private final ThreadLocal<Integer> retryCount = new ThreadLocal<>();

    @Override
    public SendResult syncSend(String topic, Object message) {
        return syncSend(topic, "", "", message);
    }

    @Override
    public SendResult syncSend(String topic, String tags, Object message) {
        return syncSend(topic, tags, "", message);
    }

    @Override
    public SendResult syncSend(String topic, String tags, String keys, Object message) {
        try {
            Message<?> msg = createMessage(message, tags, keys);
            String destination = buildDestination(topic, tags);
            return rocketMQTemplate.syncSend(destination, msg);
        } catch (Exception e) {
            log.error("同步发送消息失败，topic: {}, tags: {}, keys: {}, message: {}", topic, tags, keys, message, e);
            throw new RuntimeException("发送消息失败", e);
        }
    }

    @Override
    public CompletableFuture<SendResult> asyncSend(String topic, Object message) {
        return asyncSend(topic, "", "", message);
    }

    @Override
    public CompletableFuture<SendResult> asyncSend(String topic, String tags, Object message) {
        return asyncSend(topic, tags, "", message);
    }

    @Override
    public CompletableFuture<SendResult> asyncSend(String topic, String tags, String keys, Object message) {
        CompletableFuture<SendResult> future = new CompletableFuture<>();
        try {
            Message<?> msg = createMessage(message, tags, keys);
            String destination = buildDestination(topic, tags);
            rocketMQTemplate.asyncSend(destination, msg, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    future.complete(sendResult);
                }

                @Override
                public void onException(Throwable throwable) {
                    log.error("异步发送消息失败，topic: {}, tags: {}, keys: {}, message: {}", topic, tags, keys, message, throwable);
                    future.completeExceptionally(throwable);
                }
            });
        } catch (Exception e) {
            log.error("异步发送消息失败，topic: {}, tags: {}, keys: {}, message: {}", topic, tags, keys, message, e);
            future.completeExceptionally(e);
        }
        return future;
    }

    private Message<?> createMessage(Object payload, String tags, String keys) {
        return MessageBuilder.withPayload(payload)
                .setHeader("TAGS", tags)
                .setHeader("KEYS", keys)
                .build();
    }

    private String buildDestination(String topic, String tags) {
        return tags.isEmpty() ? topic : topic + ":" + tags;
    }

    /**
     * 发送事务消息
     *
     * @param topic 主题
     * @param message 消息内容
     * @return 发送结果
     */
    public TransactionSendResult sendMessageInTransaction(String topic, Object message) {
        return sendMessageInTransaction(topic, "", "", message, null);
    }

    /**
     * 发送事务消息
     *
     * @param topic 主题
     * @param tags 标签
     * @param message 消息内容
     * @return 发送结果
     */
    public TransactionSendResult sendMessageInTransaction(String topic, String tags, Object message) {
        return sendMessageInTransaction(topic, tags, "", message, null);
    }

    /**
     * 发送事务消息
     *
     * @param topic 主题
     * @param tags 标签
     * @param keys 消息键
     * @param message 消息内容
     * @param arg 事务参数
     * @return 发送结果
     */
    public TransactionSendResult sendMessageInTransaction(String topic, String tags, String keys, Object message, Object arg) {
        try {
            Message<?> msg = createMessage(message, tags, keys);
            String destination = buildDestination(topic, tags);
            return rocketMQTemplate.sendMessageInTransaction(destination, msg, arg);
        } catch (Exception e) {
            log.error("事务消息发送失败，topic: {}, tags: {}, keys: {}, message: {}", topic, tags, keys, message, e);
            handleMessageSendFailure(topic, tags, keys, message, e);
            throw new RuntimeException("发送事务消息失败", e);
        }
    }

    private void handleMessageSendFailure(String topic, String tags, String keys, Object message, Throwable error) {
        Integer currentRetryCount = retryCount.get();
        if (currentRetryCount == null) {
            currentRetryCount = 1;
        } else {
            currentRetryCount++;
        }
        retryCount.set(currentRetryCount);

        if (currentRetryCount >= failureProperties.getMaxRetryCount()) {
            FailureHandler handler = "email-alert".equals(failureProperties.getFailureStrategy()) 
                    ? emailAlertHandler : deadLetterQueueHandler;
            handler.handleFailure(topic, tags, keys, message, currentRetryCount, error);
            retryCount.remove();
        }
    }
}