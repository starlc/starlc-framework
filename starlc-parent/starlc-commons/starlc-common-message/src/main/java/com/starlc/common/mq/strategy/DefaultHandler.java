package com.starlc.common.mq.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 默认失败处理策略实现类
 * 仅记录日志，不做其他处理
 */
@Slf4j
@Component
public class DefaultHandler implements FailureHandler{
    @Override
    public void handleFailure() {
        // 默认实现，只记录日志，不做其他处理
        log.warn("消息处理失败，使用默认处理策略（不做处理）");
    }
}
