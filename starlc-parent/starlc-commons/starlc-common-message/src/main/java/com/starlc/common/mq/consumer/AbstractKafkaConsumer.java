package com.starlc.common.mq.consumer;

import com.starlc.common.mq.config.MessageFailureProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;

/**
 * Kafka消息消费者抽象基类
 * 提供Kafka消息消费的基础实现，包括异常处理、重试机制等
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractKafkaConsumer<T> implements MessageConsumer<T> {

    private final MessageFailureProperties failureProperties;

    /**
     * 处理消息
     * 子类需要实现此方法来处理具体的消息
     *
     * @param message 消息内容
     * @throws Exception 处理异常
     */
    @Override
    public abstract void handleMessage(T message) throws Exception;

    /**
     * 处理消息消费异常
     * @param message 消息内容
     * @param e 异常信息
     */
    @Override
    public void handleConsumptionException(T message, Exception e) {
        // 默认实现：记录错误日志
        // 子类可以覆盖此方法实现自定义的异常处理逻辑
        log.error("消息消费异常，message: {}", message, e);
    }

    /**
     * 获取消息主题
     * 从KafkaListener注解中获取topics值
     */
    @Override
    public String getMessageTopic() {
        KafkaListener annotation = this.getClass().getAnnotation(KafkaListener.class);
        if (annotation != null) {
            String[] topics = annotation.topics();
            if (topics.length > 0) {
                return topics[0];
            }
            
            TopicPartition[] topicPartitions = annotation.topicPartitions();
            if (topicPartitions.length > 0) {
                return topicPartitions[0].topic();
            }
        }
        return "";
    }

    /**
     * 获取消费者组
     * 从KafkaListener注解中获取groupId值
     */
    @Override
    public String getConsumerGroup() {
        KafkaListener annotation = this.getClass().getAnnotation(KafkaListener.class);
        return annotation != null ? annotation.groupId() : "";
    }

    /**
     * 获取分区信息
     * 从KafkaListener注解中获取分区信息
     */
    protected Integer[] getPartitions() {
        KafkaListener annotation = this.getClass().getAnnotation(KafkaListener.class);
        if (annotation != null) {
            TopicPartition[] topicPartitions = annotation.topicPartitions();
            if (topicPartitions.length > 0) {
                return topicPartitions[0].partitions();
            }
        }
        return new Integer[0];
    }
}