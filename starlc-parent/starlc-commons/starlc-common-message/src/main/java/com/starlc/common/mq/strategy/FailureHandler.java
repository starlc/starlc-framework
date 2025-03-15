package com.starlc.common.mq.strategy;



/**
 * 消息发送失败处理策略接口
 * 非常重视消息发送失败的处理。主要采用以下策略：
 * 1. 重试机制，设置最大重试次数和重试间隔；
 * 2. 死信队列，将无法处理的消息转移到特定队列进行分析和处理；
 * 3. 告警机制，当消息发送失败达到阈值时触发告警通知；
 * 4. 日志记录，记录详细的失败信息便于问题排查；
 * 5. 监控系统，实时监控消息发送状态和失败率。这些机制共同确保了消息的可靠性和系统的稳定性。
 */
public interface FailureHandler {
    /**
     * 处理失败的消息
     *
     * @param message 消息内容，可以是任意类型
     * @param destination 目标主题或队列
     * @param properties 消息属性，包含不同消息中间件的特殊参数
     * @param failureCount 失败次数
     * @param exception 失败异常
     */
    default void handleFailure(Object message, String destination, Object properties, int failureCount, Throwable exception) {
        // 默认实现调用无参方法，保持向后兼容
        handleFailure();
    }
    
    /**
     * 处理失败的消息（简化版本）
     * 为了向后兼容保留此方法
     */
    void handleFailure();
}