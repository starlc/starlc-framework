package com.starlc.common.mq.config;

import com.starlc.common.mq.MessageSender;
import com.starlc.common.mq.MessageSenderFactory;
import com.starlc.common.mq.interceptor.MessageTraceInterceptor;
import com.starlc.common.mq.producer.factory.RocketMQMessageSenderFactory;
import com.starlc.common.mq.strategy.DeadLetterQueueHandler;
import com.starlc.common.mq.strategy.DefaultHandler;
import com.starlc.common.mq.strategy.EmailAlertHandler;
import com.starlc.common.mq.strategy.FailureHandler;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import lombok.extern.slf4j.Slf4j;

/**
 * RocketMQ自动配置类
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(RocketMQProperties.class)
public class RocketMQAutoConfiguration {
    // 使用Spring Boot RocketMQ starter的自动配置

    /**
     * 创建消息发送器
     */
    @Bean
    @ConditionalOnMissingBean
    public MessageSender messageSender(MessageSenderFactory messageSenderFactory) {
        log.info("create bean MessageSender");
        return messageSenderFactory.createMessageSender();
    }
    @Bean
    @ConditionalOnMissingBean
    public MessageTraceInterceptor messageTraceInterceptor(){
        log.info("create bean MessageTraceInterceptor");
        return new MessageTraceInterceptor();
    }

    @Bean
    public MessageSenderFactory messageSenderFactory(RocketMQTemplate rocketMQTemplate, MessageTraceInterceptor traceInterceptor){
        log.info("create bean MessageSenderFactory");
        return new RocketMQMessageSenderFactory(rocketMQTemplate,traceInterceptor);
    }

    @Bean
    @ConditionalOnMissingBean
    public FailureHandler failureHandler(){
        log.info("create bean DefaultHandler");
        return new DefaultHandler();
    }

    /**
     * 死信队列失败处理策略
     */
    @Bean
    @ConditionalOnProperty(name = "rocketmq.message.failure.strategy", havingValue = "deadLetter")
    @Primary
    public FailureHandler deadLetterQueueHandler(RocketMQTemplate rocketMQTemplate) {
        return new DeadLetterQueueHandler(rocketMQTemplate);
    }

    /**
     * 邮件告警失败处理策略
     */
    @Bean
    @ConditionalOnProperty(name = "rocketmq.message.failure.strategy", havingValue = "email")
    @Primary
    public FailureHandler emailAlertHandler(org.springframework.mail.javamail.JavaMailSender mailSender) {
        return new EmailAlertHandler(mailSender);
    }

}