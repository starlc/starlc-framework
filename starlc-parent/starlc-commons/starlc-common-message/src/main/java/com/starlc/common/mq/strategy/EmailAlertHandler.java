package com.starlc.common.mq.strategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * 邮件告警处理策略实现类
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmailAlertHandler implements FailureHandler {

    private final JavaMailSender mailSender;
    private final String alertEmailTo = "admin@example.com"; // 配置告警邮件接收地址

    @Override
    public void handleFailure(Object message, com.starlc.common.message.Message messageProperties, int failureCount, Throwable lastError) {
        String topic = messageProperties.getTopic();
        String tags = messageProperties.getTags();
        String keys = messageProperties.getKeys();
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(alertEmailTo);
            mailMessage.setSubject("消息发送失败告警");
            mailMessage.setText(String.format("消息发送失败详情：\n" +
                    "Topic: %s\n" +
                    "Tags: %s\n" +
                    "Keys: %s\n" +
                    "Exchange: %s\n" +
                    "RoutingKey: %s\n" +
                    "Message: %s\n" +
                    "失败次数: %d\n" +
                    "最后错误: %s",
                    topic, tags, keys, messageProperties.getExchange(), 
                    messageProperties.getRoutingKey(), message, failureCount, lastError.getMessage()));

            mailSender.send(mailMessage);
            log.info("消息发送失败告警邮件已发送，topic/exchange: {}, tags/routingKey: {}, keys: {}, 失败次数: {}", 
                    topic != null ? topic : messageProperties.getExchange(), 
                    tags != null ? tags : messageProperties.getRoutingKey(), 
                    keys, failureCount);
        } catch (Exception e) {
            log.error("发送告警邮件失败，topic/exchange: {}, tags/routingKey: {}, keys: {}", 
                    topic != null ? topic : messageProperties.getExchange(), 
                    tags != null ? tags : messageProperties.getRoutingKey(), 
                    keys, e);
        }
    }
}