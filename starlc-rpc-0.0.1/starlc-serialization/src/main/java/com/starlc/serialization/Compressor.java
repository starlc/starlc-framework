package com.starlc.serialization;

import java.io.IOException;

/**
* @Description:    java类作用描述
* @Author:         starlc
* @CreateDate:     2025/1/16 1:24
* @UpdateRemark:   修改内容
* @Version:        1.0
*/
public interface Compressor {
    /**
     * 压缩数据
     * @param array
     * @return
     * @throws IOException
     */
    byte[] compress(byte[] array) throws IOException;

    /**
     * 解压数据
     * @param array
     * @return
     * @throws IOException
     */
    byte[] unCompress(byte[] array) throws IOException;
}
