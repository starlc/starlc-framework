package com.starlc.product.demo.async.config;

import com.starlc.common.mq.strategy.DefaultHandler;
import com.starlc.common.mq.strategy.FailureHandler;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

/**
 * 消息配置类
 * 配置消息发送器和失败处理策略
 */
@Slf4j
@Configuration
@AutoConfigureAfter(RocketMQTemplate.class)
public class MessageConfig {

    /**
     * 默认失败处理策略
     */
    @Bean
    //@ConditionalOnMissingBean(name = "defaultHandler")
    public FailureHandler defaultHandler() {
        log.info("create bean DefaultHandler");
        return new DefaultHandler();
    }
    

    

}
