package com.starlc.rpc.service.impl;

import com.starlc.rpc.service.DemoService;

import java.text.MessageFormat;

public class DemoServiceImpl implements DemoService {
    private static final String MSG = "hello: {0}";
    @Override
    public String sayHello(String name) {
        System.out.println("msg:"+name);
        return MessageFormat.format(MSG,name);
    }

    @Override
    public Long check(int len) {
        return 10086L;
    }
}
