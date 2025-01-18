package com.starlc.service;

import com.starlc.common.annotation.RemoteService;

@RemoteService
public interface DemoService {
    String sayHello(String name);

    Long check(int len);
}
