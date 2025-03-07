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
    public void handleFailure(String topic, String tags, String keys, Object message, int failureCount, Throwable lastError) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(alertEmailTo);
            mailMessage.setSubject("RocketMQ消息发送失败告警");
            mailMessage.setText(String.format("消息发送失败详情：\n" +
                    "Topic: %s\n" +
                    "Tags: %s\n" +
                    "Keys: %s\n" +
                    "Message: %s\n" +
                    "失败次数: %d\n" +
                    "最后错误: %s",
                    topic, tags, keys, message, failureCount, lastError.getMessage()));

            mailSender.send(mailMessage);
            log.info("消息发送失败告警邮件已发送，topic: {}, tags: {}, keys: {}, 失败次数: {}", topic, tags, keys, failureCount);
        } catch (Exception e) {
            log.error("发送告警邮件失败，topic: {}, tags: {}, keys: {}", topic, tags, keys, e);
        }
    }
}