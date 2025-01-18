package com.starlc;

import com.starlc.common.config.RemoteServiceProxyConfig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@Import(value = RemoteServiceProxyConfig.class)
@SpringBootApplication
public class Main  {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}