package com.starlc.common.mq.config;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RocketMQ自动配置类
 */
@Configuration
@EnableConfigurationProperties(RocketMQProperties.class)
public class RocketMQAutoConfiguration {

    /**
     * 创建RocketMQ生产者
     */
    @Bean
    @ConditionalOnMissingBean
    public DefaultMQProducer defaultMQProducer(RocketMQProperties properties) {
        DefaultMQProducer producer = new DefaultMQProducer();
        producer.setNamesrvAddr(properties.getNamesrvAddr());
        producer.setProducerGroup(properties.getProducerGroup());
        producer.setSendMsgTimeout(properties.getSendMsgTimeout());
        producer.setMaxMessageSize(properties.getMaxMessageSize());
        producer.setCompressMsgBodyOverHowmuch(properties.getCompressMsgBodyOverHowmuch());
        producer.setRetryTimesWhenSendFailed(properties.getRetryTimesWhenSendFailed());
        producer.setRetryTimesWhenSendAsyncFailed(properties.getRetryTimesWhenSendAsyncFailed());

        return producer;
    }
}