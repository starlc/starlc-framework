package com.starlc.common.spring;

import com.starlc.proxy.DemoRpcProxy;
import com.starlc.registry.ServerInfo;
import com.starlc.registry.impl.ZookeeperRegistry;

import org.springframework.beans.factory.FactoryBean;

public class RemoteServiceFactoryBean<T> implements FactoryBean<T> {

    private final Class<T> interfaceType ;
    private ZookeeperRegistry<ServerInfo> discovery;

    public RemoteServiceFactoryBean(Class<T> interfaceType) {
        this.interfaceType = interfaceType;
        discovery = new ZookeeperRegistry<>();
        try {
            discovery.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public T getObject() throws Exception {
        return DemoRpcProxy.newInstance(interfaceType, discovery);
    }

    @Override
    public Class<?> getObjectType() {
        return interfaceType;
    }

    @Override
    public boolean isSingleton() {
        return FactoryBean.super.isSingleton();
    }
}
