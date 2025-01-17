package com.starlc.protocol;

import java.io.Serializable;

import lombok.Data;

/**
* @Description:    java类作用描述
* @Author:         starlc
* @CreateDate:     2025/1/16 0:12
* @UpdateRemark:   修改内容
* @Version:        1.0
*/
@Data
public class Response implements Serializable {
    private int code = 0;//响应的错误码，正常为0，非0表示异常
    private String errMsg;//异常信息
    private Object result;//响应结果
}
