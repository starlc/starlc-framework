package com.starlc.registry;

import org.apache.curator.x.discovery.ServiceInstance;

import java.util.List;

public interface Registry<T> {

    /**
     * 註冊服務
     * @param service
     * @throws Exception
     */
    void registerService(ServiceInstance<T> service)throws Exception;

    /**
     * 注销服务
     * @param service
     * @throws Exception
     */
    void unRegisterService(ServiceInstance<T> service)throws Exception;

    List<ServiceInstance<T>> queryForInstances(String name)throws Exception;
}
