package com.starlc.product.demo.async.tasks;

import com.starlc.common.mq.MessageSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserMessageTask {
    @Autowired
    private MessageSender messageSender;

    @Scheduled(fixedRate = 5000) // 每5秒执行一次
    public void sendUserMessage() {
        if (messageSender == null) {
            log.warn("消息发送器未注入，无法发送消息");
            return;
        }
        
        try {
            // 1. 同步发送消息示例
            String msgId = System.currentTimeMillis() + "";
            String payload = "用户消息-" + msgId;
            
            // 构建消息
            Message<String> message = MessageBuilder.withPayload(payload)
                    .setHeader("msgId", msgId)
                    .build();
            
            // 发送消息
            Object result = messageSender.syncSend("user-topic", message, "tag1");
            log.info("同步发送消息成功：{}，结果：{}", payload, result);
            
            // 2. 异步发送消息示例
            String asyncPayload = "异步用户消息-" + System.currentTimeMillis();
            messageSender.asyncSend("user-topic", asyncPayload)
                    .thenAccept(sendResult -> log.info("异步消息发送成功：{}", sendResult))
                    .exceptionally(e -> {
                        log.error("异步消息发送失败", e);
                        return null;
                    });
            
            // 3. 延迟消息示例（每分钟发送一次）
            if (System.currentTimeMillis() % 60000 < 5000) {
                String delayPayload = "延迟用户消息-" + System.currentTimeMillis();
                messageSender.sendDelayMessage("user-topic", delayPayload, 3); // 延迟级别3，约10秒
                log.info("延迟消息已发送：{}", delayPayload);
            }
        } catch (Exception e) {
            log.error("发送消息失败", e);
        }
    }
}