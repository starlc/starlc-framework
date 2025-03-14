package com.starlc.common.mq.consumer;

/**
 * 通用消息消费者接口
 * 提供消息消费的基础定义，支持不同消息中间件的实现
 */
public interface MessageConsumer<T> {
    
    /**
     * 处理消息
     * 实现类需要实现此方法来处理具体的消息
     *
     * @param message 消息内容
     * @throws Exception 处理异常
     */
    void handleMessage(T message) throws Exception;
    
    /**
     * 处理消息消费异常
     * @param message 消息内容
     * @param e 异常信息
     */
    default void handleConsumptionException(T message, Exception e) {
        // 默认实现可以为空，由具体实现类覆盖
    }
    
    /**
     * 获取消息主题
     * @return 消息主题
     */
    String getMessageTopic();
    
    /**
     * 获取消费者组
     * @return 消费者组
     */
    String getConsumerGroup();
}