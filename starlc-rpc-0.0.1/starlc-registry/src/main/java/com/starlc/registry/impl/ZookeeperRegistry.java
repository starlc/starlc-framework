package com.starlc.registry.impl;

import com.google.common.collect.Maps;
import com.starlc.registry.Registry;
import com.starlc.registry.ServerInfo;
import com.starlc.registry.ServiceInstanceListener;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceCache;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.InstanceSerializer;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.apache.curator.x.discovery.details.ServiceCacheListener;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ZookeeperRegistry<T> implements Registry<T> {
    private Map<String,List<ServiceInstanceListener<T>>> listeners = Maps.newConcurrentMap();

    private InstanceSerializer serializer = new JsonInstanceSerializer(ServerInfo.class);

    private ServiceDiscovery<T> serviceDiscovery;

    private ServiceCache<T> serviceCache;

    private String address = "127.0.0.1:2181";

    public void start()throws Exception{
        //初始化CuratorFramework
        CuratorFramework client = CuratorFrameworkFactory.newClient(address,
                new ExponentialBackoffRetry(1000, 3));
        client.start();//啟動curator客戶端
        client.blockUntilConnected();//阻塞当前线程，等待连接成功
        //初始化ServiceDiscovery
        serviceDiscovery = ServiceDiscoveryBuilder
                .builder(ServerInfo.class)
                .client(client).basePath("/demo")
                .watchInstances(true)
                .serializer(serializer)
                .build();
        serviceDiscovery.start();
        //启动服务发现

        //创建ServiceCache
        serviceCache = serviceDiscovery.serviceCacheBuilder()
                .name("demoService")
                .build();
        serviceCache.addListener(new ServiceCacheListener() {
            @Override
            public void stateChanged(CuratorFramework client, ConnectionState newState) {
                System.out.println("Service cache stateChanged.");
            }

            @Override
            public void cacheChanged() {
                System.out.println("Service cache updated.");
            }

        });

        //serviceDiscovery.start();
        serviceCache.start();
    }
    @Override
    public void registerService(ServiceInstance<T> service) throws Exception {
        serviceDiscovery.registerService(service);
    }

    @Override
    public void unRegisterService(ServiceInstance<T> service) throws Exception {
        serviceDiscovery.unregisterService(service);
    }

    @Override
    public List<ServiceInstance<T>> queryForInstances(String name) throws Exception {
        return serviceCache.getInstances().stream().filter(s->s.getName().equals(name)).collect(Collectors.toList());
    }
}
