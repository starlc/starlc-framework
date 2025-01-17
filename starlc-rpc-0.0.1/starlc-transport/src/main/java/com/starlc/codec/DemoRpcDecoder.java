package com.starlc.codec;

import com.starlc.protocol.Header;
import com.starlc.protocol.Message;
import com.starlc.protocol.Request;
import com.starlc.protocol.Response;
import com.starlc.serialization.Compressor;
import com.starlc.serialization.CompressorFactory;
import com.starlc.serialization.Serialization;
import com.starlc.serialization.SerializationFactory;
import com.starlc.transport.Constants;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class DemoRpcDecoder extends ByteToMessageDecoder {


    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out)throws Exception{
        if (byteBuf.readableBytes()< Constants.HEADER_SIZE){
            return;//不到16字节 无法解析消息头，暂不读取
        }
        //记录当前readIndex指针的位置，方便重置
        byteBuf.markReaderIndex();
        //尝试读取消息头的魔数部分
        short magic = byteBuf.readShort();
        if (magic!= Constants.MAGIC){//魔数不匹配会抛出异常
            byteBuf.resetReaderIndex();//重置readIndex指针
            throw new RuntimeException("magic number error:"+ magic);
        }
        //依次读取消息版本、附加消息、消息ID 以及消息长度四部分
        byte version = byteBuf.readByte();
        byte extraInfo = byteBuf.readByte();
        long messageId = byteBuf.readLong();
        int size = byteBuf.readInt();
        Object body = null;
        //心跳消息是没有消息体的，无需读取
        if (!Constants.isHeartBeat(extraInfo)){
            //对于非心跳消息，没有积累到足够的数据是无法进行反序列化的
            if (byteBuf.readableBytes()<size){
                byteBuf.resetReaderIndex();
                return;
            }
        }
        //读取消息体并进行反序列化
        byte[] payload = new byte[size];
        byteBuf.readBytes(payload);
        //这里根据消息头重的extraInfo部分选择相应的序列化和压缩方式
        Serialization serialization = SerializationFactory.get(extraInfo);
        Compressor compressor = CompressorFactory.get(extraInfo);
        if (Constants.isRequest(extraInfo)){
            //得到消息体
            body = serialization.deSerialize(compressor.unCompress(payload), Request.class);
        }else {
            body = serialization.deSerialize(compressor.unCompress(payload), Response.class);
        }
        //将上面读取到的消息头和消息体拼装成完整的Message并向后传递
        Header header = new Header(magic,version,extraInfo,messageId,size);
        Message message = new Message(header,body);
        out.add(message);
    }
}
