package com.starlc.common.mq.producer.factory;

import com.starlc.common.mq.MessageSender;
import com.starlc.common.mq.MessageSenderFactory;
import com.starlc.common.mq.interceptor.MessageTraceInterceptor;
import com.starlc.common.mq.producer.RocketMQMessageSender;

import lombok.RequiredArgsConstructor;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

/**
 * RocketMQ消息发送者工厂实现
 * 用于创建RocketMQ消息发送者实例
 */
@RequiredArgsConstructor
public class RocketMQMessageSenderFactory implements MessageSenderFactory {


    private final RocketMQTemplate rocketMQTemplate;
    private final MessageTraceInterceptor messageTraceInterceptor;
    
    @Override
    public MessageSender createMessageSender() {
        return new RocketMQMessageSender(rocketMQTemplate, messageTraceInterceptor);
    }
}