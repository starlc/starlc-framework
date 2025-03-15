package com.starlc.product.demo.async;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class DemoAsyncMain {
    public static void main(String[] args) {
        SpringApplication.run(DemoAsyncMain.class, args);
    }
}
