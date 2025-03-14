package com.starlc.common.mq.consumer;

import com.starlc.common.message.Message;
import com.starlc.common.mq.config.MessageFailureProperties;
import com.starlc.common.mq.strategy.EmailAlertHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import java.util.HashMap;
import java.util.Map;

/**
 * RabbitMQ消息消费者抽象基类
 * 提供RabbitMQ消息消费的基础实现，包括异常处理、重试机制等
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractRabbitMQConsumer<T> implements MessageConsumer<T> {    
    @Autowired(required = false)
    private JavaMailSender mailSender;
    
    @Autowired(required = false)
    private EmailAlertHandler emailAlertHandler;
    
    @Autowired(required = false)
    private RabbitListenerEndpointRegistry rabbitListenerEndpointRegistry;
    
    @Autowired(required = false)
    private AmqpAdmin amqpAdmin;

    private final MessageFailureProperties failureProperties;

    /**
     * 处理消息
     * 子类需要实现此方法来处理具体的消息
     *
     * @param message 消息内容
     * @throws Exception 处理异常
     */
    @Override
    public abstract void handleMessage(T message) throws Exception;

    /**
     * 处理消息消费异常
     * @param message 消息内容
     * @param e 异常信息
     */
    @Override
    public void handleConsumptionException(T message, Exception e) {
        // 记录错误日志
        log.error("消息消费异常，message: {}", message, e);
        
        try {
            // 1. 发送邮件告警
            sendEmailAlert(message, e);
            
            // 2. 停止监听并挂起当前队列
            stopQueueListener();
            
            log.warn("队列 {} 已停止监听并挂起，请检查异常情况并手动恢复", getMessageTopic());
        } catch (Exception ex) {
            log.error("处理消息消费异常时发生错误", ex);
        }
    }
    
    /**
     * 发送邮件告警
     * @param message 消息内容
     * @param e 异常信息
     */
    private void sendEmailAlert(T message, Exception e) {
        if (emailAlertHandler != null) {
            // 使用已有的EmailAlertHandler发送邮件
            Message messageProperties = new Message();
            messageProperties.setTopic(getMessageTopic());
            messageProperties.setExchange(getExchangeName());
            messageProperties.setRoutingKey(getRoutingKey());
            
            Map<String, Object> headers = new HashMap<>();
            headers.put("consumerGroup", getConsumerGroup());
            messageProperties.setHeaders(headers);
            
            emailAlertHandler.handleFailure(message, messageProperties, 1, e);
        } else if (mailSender != null) {
            // 如果没有EmailAlertHandler但有JavaMailSender，则直接发送邮件
            try {
                org.springframework.mail.SimpleMailMessage mailMessage = new org.springframework.mail.SimpleMailMessage();
                mailMessage.setTo("admin@example.com"); // 配置告警邮件接收地址
                mailMessage.setSubject("RabbitMQ消息消费异常告警");
                mailMessage.setText(String.format("消息消费异常详情：\n" +
                        "队列: %s\n" +
                        "交换机: %s\n" +
                        "路由键: %s\n" +
                        "消息内容: %s\n" +
                        "异常信息: %s",
                        getMessageTopic(), getExchangeName(), getRoutingKey(), 
                        message, e.getMessage()));
                
                mailSender.send(mailMessage);
                log.info("消息消费异常告警邮件已发送，队列: {}", getMessageTopic());
            } catch (Exception ex) {
                log.error("发送告警邮件失败", ex);
            }
        } else {
            log.warn("未配置邮件发送组件，无法发送告警邮件");
        }
    }
    
    /**
     * 停止队列监听并挂起队列
     */
    private void stopQueueListener() {
        String queueName = getMessageTopic();
        String listenerId = this.getClass().getName();
        
        if (rabbitListenerEndpointRegistry != null) {
            try {
                // 停止特定的监听容器
                if (rabbitListenerEndpointRegistry.getListenerContainer(listenerId) != null) {
                    rabbitListenerEndpointRegistry.getListenerContainer(listenerId).stop();
                    log.info("已停止队列 {} 的监听器 {}", queueName, listenerId);
                } else {
                    log.warn("未找到队列 {} 的监听器 {}", queueName, listenerId);
                }
            } catch (Exception e) {
                log.error("停止队列 {} 的监听器失败", queueName, e);
            }
        } else {
            log.warn("未找到RabbitListenerEndpointRegistry，无法停止队列监听");
        }
    }

    /**
     * 获取消息主题（队列名称）
     * 从RabbitListener注解中获取队列名称
     */
    @Override
    public String getMessageTopic() {
        RabbitListener annotation = this.getClass().getAnnotation(RabbitListener.class);
        if (annotation != null) {
            // 首先检查queues属性
            String[] queues = annotation.queues();
            if (queues != null && queues.length > 0 && queues[0] != null) {
                if (!queues[0].isEmpty()) {
                    return queues[0];
                }
            }
            
            // 如果queues为空，则检查bindings属性
            QueueBinding[] bindings = annotation.bindings();
            if (bindings != null && bindings.length > 0 && bindings[0] != null) {
                Queue queue = bindings[0].value();
                if (queue != null) {
                    String queueName = queue.name();
                    if (queueName != null && !queueName.isEmpty()) {
                        return queueName;
                    }
                }
            }
        }
        return "";

    }

    /**
     * 获取消费者组（在RabbitMQ中通常是队列名称）
     */
    @Override
    public String getConsumerGroup() {
        return getMessageTopic();
    }

    /**
     * 获取交换机名称
     * 从RabbitListener注解中获取交换机名称
     */
    protected String getExchangeName() {
        RabbitListener annotation = this.getClass().getAnnotation(RabbitListener.class);
        if (annotation != null) {
            QueueBinding[] bindings = annotation.bindings();
            if (bindings.length > 0) {
                Exchange exchange = bindings[0].exchange();
                return exchange.value();
            }
        }
        return "";
    }

    /**
     * 获取路由键
     * 从RabbitListener注解中获取路由键
     */
    protected String getRoutingKey() {
        RabbitListener annotation = this.getClass().getAnnotation(RabbitListener.class);
        if (annotation != null) {
            QueueBinding[] bindings = annotation.bindings();
            if (bindings.length > 0) {
                return bindings[0].key();
            }
        }
        return "";
    }
}