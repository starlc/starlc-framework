package com.starlc.product.demo.async.listener;

import com.starlc.common.mq.listener.AbstractTransactionCheckListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

/**
 * 用户事务消息监听器
 * 继承AbstractTransactionCheckListener抽象类，实现本地事务执行和检查逻辑
 */
@Slf4j
@Component
public class UserTransactionListener extends AbstractTransactionCheckListener {

    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        String transactionId = msg.getHeaders().get("transactionId", String.class);
        log.info("执行本地事务，事务ID：{}", transactionId);
        
        try {
            // 模拟本地事务执行
            log.info("模拟数据库操作...");
            Thread.sleep(100);
            
            // 模拟事务成功
            log.info("本地事务执行成功，事务ID：{}", transactionId);
            return RocketMQLocalTransactionState.COMMIT;
            
            // 模拟事务失败
            // 随机模拟事务未知状态，触发事务回查
            // if (System.currentTimeMillis() % 3 == 0) {
            //     log.info("本地事务执行结果未知，事务ID：{}", transactionId);
            //     return RocketMQLocalTransactionState.UNKNOWN;
            // }
            
            // 模拟事务回滚
            // if (System.currentTimeMillis() % 7 == 0) {
            //     log.info("本地事务执行失败，事务ID：{}", transactionId);
            //     return RocketMQLocalTransactionState.ROLLBACK;
            // }
        } catch (Exception e) {
            log.error("本地事务执行异常，事务ID：{}", transactionId, e);
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }

    @Override
    protected RocketMQLocalTransactionState doCheckLocalTransaction(Message<?> msg, String transactionId, int retryTimes) {
        log.info("检查本地事务状态，事务ID：{}，重试次数：{}", transactionId, retryTimes);
        
        try {
            // 模拟查询本地事务状态
            log.info("查询数据库中的事务记录...");
            Thread.sleep(100);
            
            // 模拟查询结果：事务已提交
            log.info("本地事务状态查询结果：已提交，事务ID：{}", transactionId);
            return RocketMQLocalTransactionState.COMMIT;
            
            // 模拟查询结果：事务已回滚
            // if (retryTimes > 1) {
            //     log.info("本地事务状态查询结果：已回滚，事务ID：{}", transactionId);
            //     return RocketMQLocalTransactionState.ROLLBACK;
            // }
            
            // 模拟查询结果：事务状态未知，需要继续查询
            // log.info("本地事务状态查询结果：未知，事务ID：{}，将在下次继续查询", transactionId);
            // return RocketMQLocalTransactionState.UNKNOWN;
        } catch (Exception e) {
            log.error("检查本地事务状态异常，事务ID：{}", transactionId, e);
            // 如果查询异常且重试次数较多，则回滚事务
            if (retryTimes >= getMaxRetryTimes() - 1) {
                return RocketMQLocalTransactionState.ROLLBACK;
            }
            return RocketMQLocalTransactionState.UNKNOWN;
        }
    }
}