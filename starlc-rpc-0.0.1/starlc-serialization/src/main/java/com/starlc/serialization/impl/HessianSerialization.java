package com.starlc.serialization.impl;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import com.starlc.serialization.Serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
* @Description:    java类作用描述
* @Author:         starlc
* @CreateDate:     2025/1/16 0:44
* @UpdateRemark:   修改内容
* @Version:        1.0
*/
public class HessianSerialization implements Serialization {
    @Override
    public <T> byte[] serialize(T obj) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        HessianOutput hessianOutput = new HessianOutput(os);
        hessianOutput.writeObject(obj);
        return os.toByteArray();
    }

    @Override
    public <T> T deSerialize(byte[] data, Class<T> clazz) throws IOException {
        ByteArrayInputStream is = new ByteArrayInputStream(data);
        HessianInput hessianInput = new HessianInput(is);
        return (T) hessianInput.readObject(clazz);
    }
}
