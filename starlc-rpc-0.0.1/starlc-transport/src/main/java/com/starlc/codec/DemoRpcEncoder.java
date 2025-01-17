package com.starlc.codec;


import com.starlc.protocol.Header;
import com.starlc.protocol.Message;
import com.starlc.serialization.Compressor;
import com.starlc.serialization.CompressorFactory;
import com.starlc.serialization.Serialization;
import com.starlc.serialization.SerializationFactory;
import com.starlc.transport.Constants;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created on 2020-06-19
 */
public class DemoRpcEncoder extends MessageToByteEncoder<Message> {

    @Override
    protected void encode(ChannelHandlerContext ctx,
                          Message message, ByteBuf byteBuf) throws Exception {
        Header header = message.getHeader();
        // 依次序列化消息头中的魔数、版本、附加信息以及消息ID
        byteBuf.writeShort(header.getMagic());
        byteBuf.writeByte(header.getVersion());
        byteBuf.writeByte(header.getExtraInfo());
        byteBuf.writeLong(header.getMessageId());
        Object content = message.getContent();
        if (Constants.isHeartBeat(header.getExtraInfo())) {
            byteBuf.writeInt(0); // 心跳消息，没有消息体，这里写入0
            return;
        }
        // 按照extraInfo部分指定的序列化方式和压缩方式进行处理
        Serialization serialization = SerializationFactory.get(header.getExtraInfo());
        Compressor compressor = CompressorFactory.get(header.getExtraInfo());
        byte[] payload = compressor.compress(serialization.serialize(content));
        byteBuf.writeInt(payload.length); // 写入消息体长度
        byteBuf.writeBytes(payload); // 写入消息体
    }
}