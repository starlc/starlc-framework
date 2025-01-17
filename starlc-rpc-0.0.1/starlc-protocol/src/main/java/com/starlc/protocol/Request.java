package com.starlc.protocol;

import java.io.Serializable;

import lombok.Data;

/**
* @Description:    java类作用描述
* @Author:         starlc
* @CreateDate:     2025/1/16 0:11
* @UpdateRemark:   修改内容
* @Version:        1.0
*/
@Data
public class Request implements Serializable {
    //请求的service类名
    private String serviceName;
    //方法名称
    private String methodName;
    //请求方法的参数类型
    private Class[] argTypes;
    //请求的方法参数
    private Object[] args;

    public Request(String serviceName, String methodName, Object[] args) {
        this.serviceName = serviceName;
        this.methodName = methodName;
        this.args = args;
        this.argTypes = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            argTypes[i] = args[i].getClass();
        }
    }
}
