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
    public void handleFailure() {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(alertEmailTo);
            message.setSubject("消息发送失败告警");
            message.setText("消息发送失败，请检查系统日志获取详细信息。时间：" + java.time.LocalDateTime.now());
            mailSender.send(message);
            log.info("发送告警邮件成功，接收地址：{}", alertEmailTo);
        } catch (Exception e) {
            log.error("发送告警邮件失败", e);
        }
    }
}