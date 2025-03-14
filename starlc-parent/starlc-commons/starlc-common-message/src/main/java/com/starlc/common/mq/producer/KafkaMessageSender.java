package com.starlc.common.mq.producer;

import com.starlc.common.message.Message;
import com.starlc.common.message.MessageResult;
import com.starlc.common.mq.config.MessageFailureProperties;
import com.starlc.common.mq.strategy.FailureHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.concurrent.CompletableFuture;

/**
 * Kafka消息发送实现类
 */
@Slf4j
@RequiredArgsConstructor
public class KafkaMessageSender implements MessageSender<Object> {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final FailureHandler failureHandler;
    private final MessageFailureProperties failureProperties;

    @Override
    public MessageResult syncSend(Object message, Message messageProperties) {
        try {
            ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
                messageProperties.getTopic(),
                messageProperties.getPartition(),
                messageProperties.getKeys(),
                message
            );
            future.get(); // 等待发送结果
            return MessageResult.success();
        } catch (Exception e) {
            log.error("同步发送消息失败，topic: {}, partition: {}, key: {}, message: {}",
                messageProperties.getTopic(), messageProperties.getPartition(),
                messageProperties.getKeys(), message, e);
            handleFailure(message, messageProperties, 1, e);
            return MessageResult.failure(e.getMessage());
        }
    }

    @Override
    public CompletableFuture<MessageResult> asyncSend(Object message, Message messageProperties) {
        CompletableFuture<MessageResult> resultFuture = new CompletableFuture<>();
        try {
            ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
                messageProperties.getTopic(),
                messageProperties.getPartition(),
                messageProperties.getKeys(),
                message
            );
            future.addCallback(
                result -> resultFuture.complete(MessageResult.success()),
                ex -> {
                    log.error("异步发送消息失败，topic: {}, partition: {}, key: {}, message: {}",
                        messageProperties.getTopic(), messageProperties.getPartition(),
                        messageProperties.getKeys(), message, ex);
                    handleFailure(message, messageProperties, 1, ex);
                    resultFuture.complete(MessageResult.failure(ex.getMessage()));
                }
            );
        } catch (Exception e) {
            log.error("异步发送消息失败，topic: {}, partition: {}, key: {}, message: {}",
                messageProperties.getTopic(), messageProperties.getPartition(),
                messageProperties.getKeys(), message, e);
            handleFailure(message, messageProperties, 1, e);
            resultFuture.complete(MessageResult.failure(e.getMessage()));
        }
        return resultFuture;
    }
}