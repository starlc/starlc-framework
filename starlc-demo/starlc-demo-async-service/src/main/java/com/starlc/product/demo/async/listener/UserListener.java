package com.starlc.product.demo.async.listener;

import lombok.extern.slf4j.Slf4j;

import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.SelectorType;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.support.RocketMQUtil;
import org.springframework.stereotype.Component;

/**
 * 用户消息监听器
 * 继承AbstractRocketMQConsumer抽象类，实现消息处理逻辑
 */
@Slf4j
@Component
@RocketMQMessageListener(
    topic = "user-topic",
    consumerGroup = "user-consumer-group",
    selectorExpression = "*",
    selectorType = SelectorType.TAG,
        // 默认为并发消费，可省略
    consumeMode = ConsumeMode.CONCURRENTLY
)
public class UserListener implements RocketMQListener<String> {
    @Override
    public void onMessage(String message) {
        try {
            // 获取消息监听器注解信息
            RocketMQMessageListener annotation = this.getClass().getAnnotation(RocketMQMessageListener.class);
            String topic = annotation != null ? annotation.topic() : "unknown";
            String consumerGroup = annotation != null ? annotation.consumerGroup() : "unknown";

            log.debug("接收到消息，主题：{}，消费组：{},消息内容:{}", topic, consumerGroup,message);

        } catch (Exception e) {
            log.error("消息处理失败", e);
            // 调用失败处理策略
            //failureHandler.handleFailure(message, null, null, 1, e);
            // 重新抛出异常，让RocketMQ进行重试
            throw new RuntimeException("消息处理失败", e);
        }
    }

}
