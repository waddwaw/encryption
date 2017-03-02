package com.netty.turingcat;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by arvin on 2016/12/23.
 */
public class ProtocolMessageEncoder extends MessageToByteEncoder<Head> {
    protected void encode(ChannelHandlerContext channelHandlerContext, Head head, ByteBuf byteBuf) throws Exception {

        byteBuf.writeBytes(head.str.getBytes());
    }
}
