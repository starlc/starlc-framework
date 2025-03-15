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
    public void handleFailure() {
        try {
            // 在实际应用中，这里应该从参数中获取原始消息和目标主题
            // 为了示例，这里使用一个简单的消息
            String originalTopic = "user-topic";
            String deadLetterTopic = DEAD_LETTER_TOPIC_PREFIX + originalTopic;
            
            Message<String> deadLetterMessage = MessageBuilder.withPayload("Dead letter message")
                    .setHeader("original_topic", originalTopic)
                    .setHeader("failure_time", System.currentTimeMillis())
                    .build();
            
            rocketMQTemplate.send(deadLetterTopic, deadLetterMessage);
            log.info("消息已发送到死信队列：{}", deadLetterTopic);
        } catch (Exception e) {
            log.error("发送消息到死信队列失败", e);
        }
    }
}