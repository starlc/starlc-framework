package com.starlc.common.mq.producer;

import com.starlc.common.mq.MessageSender;
import com.starlc.common.mq.interceptor.MessageTraceInterceptor;
import com.starlc.common.mq.strategy.DefaultHandler;
import com.starlc.common.mq.strategy.FailureHandler;

import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.concurrent.CompletableFuture;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * RocketMQ消息发送实现类
 * 基于RocketMQTemplate的标准封装，提供同步、异步和事务消息发送方法
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RocketMQMessageSender implements MessageSender {

    private final RocketMQTemplate rocketMQTemplate;
    private final MessageTraceInterceptor traceInterceptor;

    @Autowired
    @Qualifier("defaultHandler")
    private FailureHandler failureHandler = new DefaultHandler();

    // 最大重试次数
    private static final int MAX_RETRY_TIMES = 3;

    @Override
    public SendResult syncSend(String destination, Object message) {
        return syncSend(destination, message, null);
    }

    @Override
    public SendResult syncSend(String destination, Object message, String tags) {
        try {
            // 添加消息追踪信息
            traceInterceptor.beforeSend(message);

            // 构建消息
            Message<?> rocketMessage;
            if (message instanceof Message) {
                rocketMessage = (Message<?>) message;
            } else {
                rocketMessage = MessageBuilder.withPayload(message).build();
            }

            // 发送消息
            String fullDestination = tags != null ? destination + ":" + tags : destination;
            SendResult sendResult = rocketMQTemplate.syncSend(fullDestination, rocketMessage);
            log.info("同步发送消息成功，目标：{}，消息ID：{}", fullDestination, sendResult.getMsgId());
            return sendResult;
        } catch (Exception e) {
            log.error("同步发送消息失败，目标：{}", destination, e);
            // 调用失败处理策略
            failureHandler.handleFailure(message, destination, null, 1, e);
            throw e;
        }
    }

    @Override
    public CompletableFuture<Object> asyncSend(String destination, Object message) {
        CompletableFuture<Object> future = new CompletableFuture<>();
        try {
            // 添加消息追踪信息
            traceInterceptor.beforeSend(message);

            // 构建消息
            Message<?> rocketMessage;
            if (message instanceof Message) {
                rocketMessage = (Message<?>) message;
            } else {
                rocketMessage = MessageBuilder.withPayload(message).build();
            }

            // 异步发送消息
            rocketMQTemplate.asyncSend(destination, rocketMessage, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    log.info("异步发送消息成功，目标：{}，消息ID：{}", destination, sendResult.getMsgId());
                    future.complete(sendResult);
                }

                @Override
                public void onException(Throwable e) {
                    log.error("异步发送消息失败，目标：{}", destination, e);
                    // 调用失败处理策略
                    failureHandler.handleFailure(message, destination, null, 1, e);
                    future.completeExceptionally(e);
                }
            });
        } catch (Exception e) {
            log.error("异步发送消息失败，目标：{}", destination, e);
            // 调用失败处理策略
            failureHandler.handleFailure(message, destination, null, 1, e);
            future.completeExceptionally(e);
        }
        return future;
    }

    @Override
    public Object sendMessageInTransaction(String destination, Object message, Object arg) {
        try {
            // 添加消息追踪信息
            traceInterceptor.beforeSend(message);

            // 构建消息
            Message<?> rocketMessage;
            if (message instanceof Message) {
                rocketMessage = (Message<?>) message;
            } else {
                rocketMessage = MessageBuilder.withPayload(message)
                        .setHeader("createTime", java.time.LocalDateTime.now().toString())
                        .setHeader("transactionId", java.util.UUID.randomUUID().toString())
                        .build();
            }

            // 如果当前存在事务，注册事务同步器
            if (TransactionSynchronizationManager.isActualTransactionActive()) {
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                    @Override
                    public void afterCommit() {
                        try {
                            // 事务提交后发送消息
                            SendResult sendResult = rocketMQTemplate.sendMessageInTransaction("user-transaction-listener", destination, rocketMessage, arg);
                            log.info("事务消息发送成功，目标：{}，消息ID：{}", destination, sendResult.getMsgId());
                        } catch (Exception e) {
                            log.error("事务消息发送失败，目标：{}", destination, e);
                            // 调用失败处理策略
                            failureHandler.handleFailure(message, destination, null, 1, e);
                        }
                    }
                });
                return null; // 事务未提交，暂不发送
            } else {
                // 无事务上下文，直接发送事务消息
                SendResult sendResult = rocketMQTemplate.sendMessageInTransaction("user-transaction-listener", destination, rocketMessage, arg);
                log.info("事务消息发送成功，目标：{}，消息ID：{}", destination, sendResult.getMsgId());
                return sendResult;
            }
        } catch (Exception e) {
            log.error("事务消息发送失败，目标：{}", destination, e);
            // 调用失败处理策略
            failureHandler.handleFailure(message, destination, null, 1, e);
            throw e;
        }
    }

    @Override
    public Object sendDelayMessage(String destination, Object message, int delayLevel) {
        try {
            // 添加消息追踪信息
            traceInterceptor.beforeSend(message);

            // 构建消息
            Message<?> rocketMessage;
            if (message instanceof Message) {
                rocketMessage = (Message<?>) message;
            } else {
                rocketMessage = MessageBuilder.withPayload(message).build();
            }

            // 发送延迟消息
            SendResult sendResult = rocketMQTemplate.syncSend(destination, rocketMessage,
                    rocketMQTemplate.getProducer().getSendMsgTimeout(), delayLevel);
            log.info("延迟消息发送成功，目标：{}，延迟级别：{}，消息ID：{}", destination, delayLevel, sendResult.getMsgId());
            return sendResult;
        } catch (Exception e) {
            log.error("延迟消息发送失败，目标：{}，延迟级别：{}", destination, delayLevel, e);
            // 调用失败处理策略
            failureHandler.handleFailure(message, destination, null, 1, e);
            throw e;
        }
    }
}