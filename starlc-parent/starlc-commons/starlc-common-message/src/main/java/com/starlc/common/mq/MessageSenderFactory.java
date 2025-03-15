package com.starlc.common.mq;

import com.starlc.common.mq.MessageSender;

/**
 * 消息发送者工厂接口
 */
public interface MessageSenderFactory {
    /**
     * 创建消息发送者
     *
     * @return 消息发送者实例
     */
    MessageSender createMessageSender();
}