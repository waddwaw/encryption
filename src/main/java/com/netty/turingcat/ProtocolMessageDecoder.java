package com.netty.turingcat;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * Created by arvin on 2016/12/23.
 */
public class ProtocolMessageDecoder extends ByteToMessageDecoder {

    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        byteBuf.markReaderIndex();
        byte[] req = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(req);
        String body = new String(req,"utf-8");
        System.out.println(body);
        Head head = new Head();
        head.str = body;
        list.add(head);

    }

}
