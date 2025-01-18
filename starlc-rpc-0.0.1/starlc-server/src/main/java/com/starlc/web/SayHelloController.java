package com.starlc.web;

import com.starlc.service.DemoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
public class SayHelloController {

    @Autowired
    private DemoService demoService;


    @GetMapping
    @ResponseBody
    public String insertUser(@RequestParam String name) {
        String hello = demoService.sayHello(name);
        return hello;
    }

}
