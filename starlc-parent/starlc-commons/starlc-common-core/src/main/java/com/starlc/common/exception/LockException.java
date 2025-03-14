package com.starlc.common.exception;

/**
* @Description:    分布式锁异常
* @Author:         liuc
* @CreateDate:     2025/3/14 16:54
* @UpdateRemark:   修改内容
* @Version:        1.0
*/
public class LockException extends RuntimeException {
    private static final long serialVersionUID = 6610083281801529147L;

    public LockException(String message) {
        super(message);
    }
}
