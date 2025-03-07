package com.starlc.common.mq.strategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * 死信队列处理策略实现类
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeadLetterQueueHandler implements FailureHandler {

    private final RocketMQTemplate rocketMQTemplate;
    private static final String DEAD_LETTER_TOPIC_PREFIX = "%DLQ%";

    @Override
    public void handleFailure(String topic, String tags, String keys, Object message, int failureCount, Throwable lastError) {
        String deadLetterTopic = DEAD_LETTER_TOPIC_PREFIX + topic;
        try {
            Message<?> deadLetterMessage = MessageBuilder.withPayload(message)
                    .setHeader("TAGS", tags)
                    .setHeader("KEYS", keys)
                    .setHeader("X-Original-Topic", topic)
                    .setHeader("X-Failure-Count", failureCount)
                    .setHeader("X-Last-Error", lastError.getMessage())
                    .build();

            rocketMQTemplate.syncSend(deadLetterTopic, deadLetterMessage);
            log.info("消息已发送至死信队列，topic: {}, tags: {}, keys: {}, 失败次数: {}", topic, tags, keys, failureCount);
        } catch (Exception e) {
            log.error("发送消息到死信队列失败，topic: {}, tags: {}, keys: {}", topic, tags, keys, e);
        }
    }
}