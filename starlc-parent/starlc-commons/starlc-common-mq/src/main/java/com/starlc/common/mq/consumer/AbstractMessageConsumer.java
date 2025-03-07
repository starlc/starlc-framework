package com.starlc.common.mq.consumer;

import com.starlc.common.mq.config.MessageFailureProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 消息消费者抽象基类
 * 提供消息消费的基础实现，包括异常处理、重试机制等
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractMessageConsumer<T> implements RocketMQListener<T> {

    @Autowired
    private MessageFailureProperties failureProperties;

    @Override
    public void onMessage(T message) {
        String topic = getMessageTopic();
        String tags = getMessageTags();
        String consumerGroup = getConsumerGroup();

        try {
            log.debug("开始消费消息，topic: {}, tags: {}, consumerGroup: {}, message: {}", 
                    topic, tags, consumerGroup, message);
            
            // 调用子类实现的具体消息处理逻辑
            handleMessage(message);
            
            log.debug("消息消费完成，topic: {}, tags: {}, consumerGroup: {}, message: {}", 
                    topic, tags, consumerGroup, message);
        } catch (Exception e) {
            log.error("消息消费失败，topic: {}, tags: {}, consumerGroup: {}, message: {}", 
                    topic, tags, consumerGroup, message, e);
            handleConsumptionException(message, e);
        }
    }

    /**
     * 处理消息消费异常
     * @param message 消息内容
     * @param e 异常信息
     */
    protected void handleConsumptionException(T message, Exception e) {
        // 默认实现：记录错误日志
        // 子类可以覆盖此方法实现自定义的异常处理逻辑
        log.error("消息消费异常，message: {}", message, e);
    }

    /**
     * 获取消息主题
     * 从RocketMQMessageListener注解中获取topic值
     */
    protected String getMessageTopic() {
        RocketMQMessageListener annotation = this.getClass().getAnnotation(RocketMQMessageListener.class);
        return annotation != null ? annotation.topic() : "";
    }

    /**
     * 获取消息标签
     * 从RocketMQMessageListener注解中获取selectorExpression值
     */
    protected String getMessageTags() {
        RocketMQMessageListener annotation = this.getClass().getAnnotation(RocketMQMessageListener.class);
        return annotation != null ? annotation.selectorExpression() : "";
    }

    /**
     * 获取消费者组
     * 从RocketMQMessageListener注解中获取consumerGroup值
     */
    protected String getConsumerGroup() {
        RocketMQMessageListener annotation = this.getClass().getAnnotation(RocketMQMessageListener.class);
        return annotation != null ? annotation.consumerGroup() : "";
    }

    /**
     * 获取消费模式
     * 从RocketMQMessageListener注解中获取messageModel值
     */
    protected MessageModel getMessageModel() {
        RocketMQMessageListener annotation = this.getClass().getAnnotation(RocketMQMessageListener.class);
        return annotation != null ? annotation.messageModel() : MessageModel.CLUSTERING;
    }

    /**
     * 处理消息
     * 子类需要实现此方法来处理具体的消息
     *
     * @param message 消息内容
     * @throws Exception 处理异常
     */
    protected abstract void handleMessage(T message) throws Exception;
}