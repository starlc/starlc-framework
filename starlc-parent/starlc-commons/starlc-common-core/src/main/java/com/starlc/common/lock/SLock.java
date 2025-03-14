package com.starlc.common.lock;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
* @Description:    锁对象抽象
* @Author:         liuc
* @CreateDate:     2025/3/14 16:50
* @UpdateRemark:   修改内容
* @Version:        1.0
*/
@AllArgsConstructor
public class SLock implements AutoCloseable {
    @Getter
    private final Object lock;

    private final DistributedLock locker;

    @Override
    public void close() throws Exception {
        locker.unlock(lock);
    }
}
