package com.starlc.common.mq.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.messaging.Message;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * RocketMQ事务消息回查抽象类
 * 提供事务消息回查的基础实现，子类只需要实现核心的回查逻辑
 */
@Slf4j
public abstract class AbstractTransactionCheckListener implements RocketMQLocalTransactionListener {

    /**
     * 默认回查超时时间（1小时）
     */
    private static final Duration DEFAULT_CHECK_TIMEOUT = Duration.ofHours(1);

    /**
     * 默认最大重试次数
     */
    private static final int DEFAULT_MAX_RETRY_TIMES = 3;

    /**
     * 获取回查超时时间
     * @return 回查超时时间
     */
    protected Duration getCheckTimeout() {
        return DEFAULT_CHECK_TIMEOUT;
    }

    /**
     * 获取最大重试次数
     * @return 最大重试次数
     */
    protected int getMaxRetryTimes() {
        return DEFAULT_MAX_RETRY_TIMES;
    }

    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
        String transactionId = msg.getHeaders().get("transactionId", String.class);
        LocalDateTime createTime = LocalDateTime.parse(msg.getHeaders().get("createTime", String.class));
        Integer retryTimes = msg.getHeaders().get("retryTimes", Integer.class);

        if (retryTimes == null) {
            retryTimes = 1;
        } else {
            retryTimes++;
        }

        // 检查是否超时
        if (LocalDateTime.now().isAfter(createTime.plus(getCheckTimeout()))) {
            log.warn("事务消息回查超时，transactionId: {}, createTime: {}", transactionId, createTime);
            return RocketMQLocalTransactionState.ROLLBACK;
        }

        // 检查重试次数是否超过最大值
        if (retryTimes > getMaxRetryTimes()) {
            log.warn("事务消息回查重试次数超过最大值，transactionId: {}, retryTimes: {}", transactionId, retryTimes);
            return RocketMQLocalTransactionState.ROLLBACK;
        }

        try {
            // 调用子类实现的具体回查逻辑
            return doCheckLocalTransaction(msg, transactionId, retryTimes);
        } catch (Exception e) {
            log.error("事务消息回查异常，transactionId: {}", transactionId, e);
            return RocketMQLocalTransactionState.UNKNOWN;
        }
    }

    /**
     * 执行本地事务
     * 子类需要实现此方法来执行具体的本地事务
     *
     * @param msg 消息对象
     * @param arg 事务参数
     * @return 事务执行结果
     */
    @Override
    public abstract RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg);

    /**
     * 检查本地事务状态
     * 子类需要实现此方法来检查具体的事务状态
     *
     * @param msg 消息对象
     * @param transactionId 事务ID
     * @param retryTimes 重试次数
     * @return 事务状态
     */
    protected abstract RocketMQLocalTransactionState doCheckLocalTransaction(Message<?> msg, String transactionId, int retryTimes);
}