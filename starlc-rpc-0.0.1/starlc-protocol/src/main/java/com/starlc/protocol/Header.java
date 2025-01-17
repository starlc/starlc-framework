package com.starlc.protocol;

import lombok.Data;

/**
* @Description:    RPC协议 消息头
* @Author:         starlc
* @CreateDate:     2025/1/15 23:52
* @UpdateRemark:   修改内容
* @Version:        1.0
*/
@Data
public class Header {

    private short magic;//魔数
    private byte version;//协议版本
    private byte extraInfo;//附加信息
    private Long messageId;//消息ID
    private Integer size;//消息体长度


    public Header(short magic, byte version) {
        this.magic = magic;
        this.version = version;
        this.extraInfo = 0;
    }

    public Header(short magic, byte version, byte extraInfo, Long messageId, Integer size) {
        this.magic = magic;
        this.version = version;
        this.extraInfo = extraInfo;
        this.messageId = messageId;
        this.size = size;
    }
}
