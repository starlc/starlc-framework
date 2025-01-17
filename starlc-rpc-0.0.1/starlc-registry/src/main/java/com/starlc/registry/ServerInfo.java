package com.starlc.registry;

import java.io.Serializable;

import lombok.Data;

@Data
public class ServerInfo implements Serializable {
    /**
     *  主机地址
     */
    private String host;

    /**
     * 端口
     */
    private int port;

    public ServerInfo() {
    }

    public ServerInfo(String host, int port) {
        this.host = host;
        this.port = port;
    }
}
