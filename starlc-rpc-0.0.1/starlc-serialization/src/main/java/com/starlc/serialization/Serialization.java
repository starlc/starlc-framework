package com.starlc.serialization;

import java.io.IOException;

public interface Serialization {
    /**
     * 序列化
     * @param obj
     * @return
     * @param <T>
     * @throws IOException
     */
    <T> byte[] serialize(T obj)throws IOException;

    /**
     * 反序列化
     * @param data
     * @param clazz
     * @return
     * @param <T>
     * @throws IOException
     */
    <T> T deSerialize(byte[] data,Class<T> clazz)throws IOException;
}
