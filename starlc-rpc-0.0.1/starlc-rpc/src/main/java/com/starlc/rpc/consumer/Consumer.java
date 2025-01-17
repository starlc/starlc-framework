package com.starlc.rpc.consumer;

import com.starlc.proxy.DemoRpcProxy;
import com.starlc.registry.ServerInfo;
import com.starlc.registry.impl.ZookeeperRegistry;
import com.starlc.rpc.service.DemoService;

public class Consumer {
    public static void main(String[] args) throws Exception {
        //创建zookeeperRegisty对象
        ZookeeperRegistry<ServerInfo> discovery = new ZookeeperRegistry<>();
        discovery.start();
        //创建代理对象，通过代理调用远端Server
        DemoService demoService = DemoRpcProxy.newInstance(DemoService.class, discovery);
        //调用sayHello()方法，并输出结果
        String result = demoService.sayHello("张三");
        Long ss = demoService.check(11);
        System.out.println(result);
        System.out.println(ss);
    }
}
